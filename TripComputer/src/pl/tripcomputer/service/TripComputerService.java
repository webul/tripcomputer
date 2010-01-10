package pl.tripcomputer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import pl.tripcomputer.MainState;
import pl.tripcomputer.Preferences;
import pl.tripcomputer.R;
import pl.tripcomputer.service.ITripComputerService;
import pl.tripcomputer.data.common.*;
import pl.tripcomputer.gps.*;


public class TripComputerService extends Service
{
	//fields
	private Database dataBase = null;
	private MainState state = null;
	private Preferences prefs = null;

	//fields
	private GpsLogger gpsLogger = null;
	private GpsServiceReader gpsReader = null;
		
	
	//methods
	public void onCreate()
	{
		dataBase = new Database(this, false);
		state = new MainState(this, dataBase);
		prefs = new Preferences(this);
		
		//get current state
		state.update();
		
		//initialize gps reader
		gpsLogger = new GpsLogger(this, dataBase);
		gpsReader = new GpsServiceReader(this, prefs, gpsLogger);
				
		//restore recording state if set for track
		updateRecordingState(null);
	}	
	
	private void notifyTrackRecordingState(int str_res_id, String sTrackName)
	{
		final String sMsg = String.format(getString(str_res_id), sTrackName);					
		Toast msg = Toast.makeText(this, sMsg, Toast.LENGTH_SHORT);
		msg.show();
	}

	//called only if started by bindService() 
	public IBinder onBind(Intent intent)
	{		
		//interface to return
    if (ITripComputerService.class.getName().equals(intent.getAction())) 
    {
    	return mBinder;
    }
		return null;
	}
		
	//called only if started by startService() 
	public void onStart(Intent intent, int startId) 
	{
		super.onStart(intent, startId);
		
		ServiceCommand cmd = new ServiceCommand(intent);
		
		if (cmd.isCommand(ServiceCommand.CMD_TRACK_RECORDING_PAUSE))
		{			
			state.setRecordingTrack(cmd.getValueLong());
			updateRecordingState(cmd.getValueString());
		}
		
		if (cmd.isCommand(ServiceCommand.CMD_TRACK_RECORDING_RESUME))
		{
			state.setRecordingTrack(cmd.getValueLong());
			updateRecordingState(cmd.getValueString());
		}
	}
	
	public void onDestroy()
	{
		gpsReader.stop();
		
		dataBase.close();
				
		super.onDestroy();
	}
  
	public void updateRecordingState(String sTrackName)
	{	
		final long lRecordingTrackId = state.getRecordingTrackId();
		if (lRecordingTrackId == -1)
		{
			gpsLogger.setRecordingTrackId(-1);
			gpsReader.stop();
			
			if (sTrackName != null)
				notifyTrackRecordingState(R.string.notify_msg_track_paused, sTrackName);
		} else {
			gpsReader.stop();
			gpsLogger.setRecordingTrackId(lRecordingTrackId);
			gpsReader.start();
			
			if (sTrackName != null)
				notifyTrackRecordingState(R.string.notify_msg_track_resumed, sTrackName);
		}	
	}

	//remote interface binder
  private final ITripComputerService.Stub mBinder = new ITripComputerService.Stub()
  {
  };
	
}
