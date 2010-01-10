package pl.tripcomputer.activities;

import pl.tripcomputer.Command;
import pl.tripcomputer.CommandData;
import pl.tripcomputer.MainDraw;
import pl.tripcomputer.MainThread;
import pl.tripcomputer.MainView;
import pl.tripcomputer.R;
import pl.tripcomputer.Utils;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.common.DataTableOperation;
import pl.tripcomputer.data.tables.DataTableTracks;
import pl.tripcomputer.data.tables.DataTableWaypoints;
import pl.tripcomputer.gps.GpsActivityReader;
import pl.tripcomputer.loader.DataLoader;
import pl.tripcomputer.service.ServiceCommand;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.view.Menu;
import android.view.MenuItem;


public class ActivityMain extends CommonActivity
{
	//fields
	private static ActivityMain self = null;
	private static String sAppVersion = "";
	
	//fields
	private MainView mMainView = null;
	private MainThread mMainThread = null;
  
	//fields
	public static DataLoader loader = null;
	private static GpsActivityReader gpsReader = null;

  
	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);

	  ActivityMain.self = this;
	  
	  sAppVersion = Utils.getAppVersion(this);
	  
    //set layout
	  setContentView(R.layout.activity_main);
	    
		//set service context
		ServiceCommand.init(this);
		
		//initialize global commands
		Command.init(this);
		
	  //create data container
	  loader = new DataLoader(this);
	  
	  //create activity gps reader 
	  gpsReader = new GpsActivityReader(this, prefs);
	  	  
    //get handle to the View from XML
    mMainView = (MainView)findViewById(R.id.MainView);
        
    //initialize and prepare thread
    mMainView.init(this);
        
    //get thread handle
    mMainThread = mMainView.getThread();
    
    //create main thread
    mMainThread.createThread();
    
    //restore thread state
  	mMainThread.restoreState(savedInstanceState);

		//load tracks, waypoints
  	loader.loadData();
  	
	  //start service
		ServiceCommand.send(ServiceCommand.CMD_START_SERVICE);
		
		//get current location
		gpsReader.restart();		
	}
	
	public static String getAppVersion()
	{
		return sAppVersion; 
	}	
	
	protected void onDestroy()
	{
		//destroy main thread
		mMainThread.destroyThread();
								
		//stop getting current location
		gpsReader.forceStop();
				
		//stop reading compass
		MainDraw.compassRotation.disable();
		
		super.onDestroy();
	}
	
	protected void onStart()
	{		
		super.onStart();
			
		//start if not started yet (was disabled in settings)
		if (!gpsReader.isStarted())
			gpsReader.start();		
	}
		
	protected void onResume()
	{		
		super.onResume();
		
	}
	
  protected void onPause()
  {
    super.onPause();
    
    //pause thread
  	mMainThread.setEnabled(false);
  }

  protected void onStop()
	{
		super.onStop();
		
		//stop reading compass
		MainDraw.compassRotation.disable();
	}
	
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    
    //save thread state
    mMainThread.saveState(outState);
  }
		
  public void onWindowFocusChanged(boolean hasWindowFocus)
  {
    if (hasWindowFocus)
    {
    	mMainThread.setEnabled(true);
    } else {
    	mMainThread.setEnabled(false);    	
    }
  }

	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);

		Command.addMenuItem(menu, Command.CMD_ADD_WAYPOINT, R.drawable.ic_menu_add);		
		Command.addMenuItem(menu, Command.CMD_WAYPOINTS, R.drawable.ic_menu_myplaces);
		Command.addMenuItem(menu, Command.CMD_TRACKS, R.drawable.ic_menu_tracks);
		Command.addMenuItem(menu, Command.CMD_SETTINGS, R.drawable.ic_menu_preferences);	
		Command.addMenuItem(menu, Command.CMD_ABOUT, R.drawable.ic_menu_help);

		//more items
		Command.addMenuItem(menu, Command.CMD_WAYPOINTS_SEARCH);
		Command.addMenuItem(menu, Command.CMD_SET_SERVICE_ACCESS);

		return result;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		final int iSelectedCMD = item.getItemId();
		CommandData data = new CommandData(CommandData.MODE_NONE);
		return main.runCommand(iSelectedCMD, data);
	}

	protected void onRequestedActivityResultOK(int requestCode, Intent data)
	{
		
	}

	protected void onRequestedActivityResultCancel(int requestCode, Intent data)
	{
		
	}
	
	//called from others Activities (CommonActivity.update())
	public static void onChangeTableState(final DataTableOperation op, final String sTableName)
	{
		//if tracks table
		if (DataTableTracks.sTableName.equals(sTableName))
		{
			if (op.iOperation != DataTableOperation.OP_NONE)
				self.state.update();
			
			//on track update
			if (op.iOperation == DataTableOperation.OP_UPDATE)
			{
				//select open track
				ActivityMain.loader.selectOpenTrack();
				
				//select location if recording resume
				if (op.iCommand == ServiceCommand.CMD_TRACK_RECORDING_RESUME)
				{
					ActivityMain.loader.selectLocation();
				}
			}
			
			//on track insert
			if (op.iOperation == DataTableOperation.OP_INSERT)
			{
				ActivityMain.loader.selectOpenTrack();
				ActivityMain.loader.selectLocation();
			}
			
			//on track delete
			if (op.iOperation == DataTableOperation.OP_DELETE)
			{
				ActivityMain.loader.clearTrackSelection();
			}		
		}
		
		//if waypoints table
		if (DataTableWaypoints.sTableName.equals(sTableName))
		{
			//on waypoint update
			if (op.iOperation == DataTableOperation.OP_UPDATE)
			{
			}
			
			//on waypoint insert
			if (op.iOperation == DataTableOperation.OP_INSERT)
			{
			}
			
			//on waypoint delete
			if (op.iOperation == DataTableOperation.OP_DELETE)
			{			
				ActivityMain.loader.clearWaypointSelection();
			}		
		}
	}

	public static Location getLastLocation()
	{
		return gpsReader.getLastLocation();
	}
	
	public static void restartGPS()
	{
		gpsReader.restart();
	}
	
}
