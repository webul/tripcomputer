package pl.tripcomputer;

import java.util.Observable;

import android.content.Context;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.data.items.DataItemTrack;


public class MainState extends Observable
{
	//current UI mode
	private final Boolean bUiModeMutex = false;
	private int iUiCurrentMode = Command.CMD_MODE_TRACKS;
	
	//currently selected track in UI
	private long lSelectedTrackId = -1;
	
	//currently selected waypoint in UI
	private long lSelectedWaypointId = -1;
	
	//currently selected location in UI
	private boolean bSelectedLocation = false;
	
	//observers notify data
	public final static Integer OPEN_TRACK_NONE = 0;
	public final static Integer OPEN_TRACK_SELECTED = 1;
	public final static Integer UI_MODE_UPDATE = 2;
	
	//track recording status
	public final static int TRACK_NOT_SELECTED = 0;
	public final static int TRACK_PAUSED = 1;
	public final static int TRACK_RECORDING = 2;
	
	//track mutex
	private final Boolean bTrackMutex = false;
	//selection mutex
	private final Boolean bSelectionMutex = false;
	
	//fields
	protected Context context = null;
	private Database dataBase = null;
	
	private DataItemTrack trackOpen = null;
	private DataItemTrack trackRecording = null;
		
	
	//methods
	public MainState(Context context, Database dataBase)
	{
		this.context = context;
		this.dataBase = dataBase;
	}

	public void update()
	{
		readOpenTrack();
		updateUiMode();
	}
			
	private void readOpenTrack()
	{
		setOpenTrack(null);
		setRecordingTrack(null);
		
		final DataItemTrack item = dataBase.tableTracks().getOpenTrackItem();		
		if (item != null)
		{
			setOpenTrack(item);
			
			if (item.isRecording())
				setRecordingTrack(item);
		}
	}

	private void readRecordingTrack(long lTrackId)
	{
		setRecordingTrack(null);
		
		if (lTrackId != -1)
		{
			final DataItemTrack item = dataBase.tableTracks().getTrackItem(lTrackId);				
			if (item != null)
				if (item.isRecording())
					setRecordingTrack(item);
		}
	}
	
	private void setOpenTrack(DataItemTrack newTrack)
	{
		synchronized(bTrackMutex)
		{			
			if (newTrack == null)
			{
				this.trackOpen = null;
				
				setChanged();
				notifyObservers(OPEN_TRACK_NONE);
			} else {
				this.trackOpen = new DataItemTrack(newTrack);
				
				setChanged();
				notifyObservers(OPEN_TRACK_SELECTED);
			}
		}
	}
	
	private void setRecordingTrack(DataItemTrack newTrack)
	{
		synchronized(bTrackMutex)
		{					
			if (newTrack == null)
			{
				this.trackRecording = null;
			} else {
				this.trackRecording = new DataItemTrack(newTrack);
			}
		}
	}

	public long getOpenTrackId()
	{
		long value = -1;
		synchronized(bTrackMutex)
		{
			if (trackOpen != null)
				value = trackOpen.iID;
		}
		return value;
	}
	
	public long getRecordingTrackId()
	{
		long value = -1;
		synchronized(bTrackMutex)
		{
			if (trackRecording != null)			
				value = trackRecording.iID;
		}
		return value;		
	}
	
	public void setRecordingTrack(long lTrackId)
	{
		setRecordingTrack(null);
		readRecordingTrack(lTrackId);		
	}

	public int getTrackStatus()
	{
		int iTrackStatus = TRACK_NOT_SELECTED;
		synchronized(bTrackMutex)
		{
			final DataItemTrack item = trackOpen;
			if (item != null)
			{
				if (item.isRecording())
					iTrackStatus = TRACK_RECORDING;
				else
					iTrackStatus = TRACK_PAUSED;
			}
		}
		return iTrackStatus;
	}
	
	public void setSelectedTrackId(long lTrackId, boolean bResetLocation)
	{		
		synchronized(bSelectionMutex)
		{
			lSelectedTrackId = lTrackId;
			if (lTrackId != -1)
			{
				if (bResetLocation)
				{
					if (allowObjectResetView())					
						bSelectedLocation = false;
				}

				setChanged();
				notifyObservers(UI_MODE_UPDATE);
			}
		}
	}

	public void setSelectedWaypointId(long lWaypointId, boolean bResetLocation)
	{
		synchronized(bSelectionMutex)
		{
			lSelectedWaypointId = lWaypointId;
			if (lWaypointId != -1)
			{
				if (bResetLocation)
				{
					if (allowObjectResetView())					
						bSelectedLocation = false;
				}
				
				setChanged();
				notifyObservers(UI_MODE_UPDATE);
			}
		}
	}

	public void setSelectedLocation()
	{
		synchronized(bSelectionMutex)
		{
			bSelectedLocation = true;

			setChanged();
			notifyObservers(UI_MODE_UPDATE);			
		}
	}

	public void clearLocationSelection()
	{
		synchronized(bSelectionMutex)
		{
			bSelectedLocation = false;

			setChanged();
			notifyObservers(UI_MODE_UPDATE);			
		}
	}
	
	public long getSelectedTrackId()
	{
		long value = -1;
		synchronized(bSelectionMutex)
		{
			value = lSelectedTrackId;
		}
		return value;
	}

	public long getSelectedWaypointId()
	{
		long value = -1;
		synchronized(bSelectionMutex)
		{
			value = lSelectedWaypointId;
		}
		return value;
	}
	
	public boolean isSelectedLocation()
	{
		boolean value = false;
		synchronized(bSelectionMutex)
		{
			value = bSelectedLocation;
		}
		return value;
	}

	public void changeUiMode()
	{
		synchronized(bUiModeMutex)
		{
			iUiCurrentMode++;
			if (iUiCurrentMode >= Command.CMD_MODE_LAST)
				iUiCurrentMode = Command.CMD_MODE_TRACKS;					

			setChanged();
			notifyObservers(UI_MODE_UPDATE);
		}
	}

	public void updateUiMode()
	{
		//run thread
    Thread t = new Thread()
    {
      public void run()
      {
    		setChanged();
    		notifyObservers(UI_MODE_UPDATE);		
      }
    };
    t.start();		
	}
	
	public int getCurrentUiMode()
	{
		synchronized(bUiModeMutex)
		{
			return iUiCurrentMode;
		}
	}

	public boolean allowObjectResetView()
	{
		final int iUiMode = getCurrentUiMode();
		if (iUiMode == Command.CMD_MODE_TRACKS)
		{
			return true;		
		}
		return false;
	}
	
}
