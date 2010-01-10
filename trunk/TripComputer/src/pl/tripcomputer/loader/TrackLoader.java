package pl.tripcomputer.loader;

import java.util.ArrayList;

import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.ui.StatusMessage;


public class TrackLoader extends Loader
{
  //fields
  private TrackCollection trackList = null;
    
  //selected object
  private final Boolean bSelectedObjectMutex = false;
  private TrackItem selectedItem = null;
  
	
	//methods
	public TrackLoader(ActivityMain parent, Screen screen, GpsLocationStatus gpsLocationStatus)
	{
		super(parent, screen, gpsLocationStatus);

		trackList = new TrackCollection(parent, screen, gpsLocationStatus);		
	}
		
	public TrackCollection items()
	{
		return trackList;
	}
	
	protected void loadAllItems()
	{
		trackList.loadAllItems();
	}
	
	public synchronized void selectNextObject()
	{
		final long lSelectedTrackId = state.getSelectedTrackId();
		
		synchronized(dataBase.tableTracks())
		{
			final ArrayList<Long> listVisibleTracks = dataBase.tableTracks().getVisibleTracksRotated(lSelectedTrackId);
			selectNextObject(lSelectedTrackId, listVisibleTracks);
		}
	}

	public void selectOpenTrack()
	{	
		final long lOpenTrackId = state.getOpenTrackId();
		if (lOpenTrackId != -1)
		{						
			synchronized(trackList)
			{
				//get track from list
				final TrackItem trackItem = trackList.getLoaderItem(lOpenTrackId);
				
				//show track
				if ((trackItem != null) && trackItem.isVisible()) 
				{
					setSelectedTrack(trackItem);
					
					state.setSelectedTrackId(lOpenTrackId, false);
					
					//show status message of selected track
					StatusMessage.showText(trackItem.getDescription());
				}
			}
		}
	}

	public void clearTrackSelection()
	{
		setSelectedTrack(null);
		state.setSelectedTrackId(-1, false);		
	}
	
	private void setSelectedTrack(TrackItem item)
	{
		synchronized(bSelectedObjectMutex)
		{
			selectedItem = item;
		}		
	}

	public TrackItem getSelectedTrack()
	{
		synchronized(bSelectedObjectMutex)
		{
			return selectedItem;						
		}		
	}
	
	protected boolean selectObject(long lObjectId)
	{
		boolean bSuccess = false;
		
		synchronized(trackList)
		{
			//get track from list
			final TrackItem trackItem = trackList.getLoaderItem(lObjectId);
			
			//show track
			if ((trackItem != null) && trackItem.isVisible()) 
			{
				if (!trackItem.isEmpty())
				{
					setSelectedTrack(trackItem);
					
					state.setSelectedTrackId(lObjectId, true);
					
					trackItem.getLayer().setResetViewPort();
					
					//show status message of selected track
					StatusMessage.showText(trackItem.getDescription());

					bSuccess = true;
				}
			}
		}

		if (!bSuccess)
			clearTrackSelection();
		
		return bSuccess;
	}
	
}
