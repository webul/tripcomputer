package pl.tripcomputer.loader;

import java.util.Collections;
import java.util.Comparator;

import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.map.Screen;


public class TrackCollection extends LoaderItemCollection<TrackItem>
{
	//methods
	public TrackCollection(ActivityMain parent, Screen screen, GpsLocationStatus gpsLocationStatus)
	{
		super(parent, screen, gpsLocationStatus, parent.getDatabase().tableTracks());		
	}

	protected void updateForResetViewPort()
	{
		updateLayersPath();
	}	

	protected void updateForNewLocation(boolean locationSaved)
	{
		//update collection only for save point mode 
		if (gpsLocationStatus.isSaveMode())
		{
			updateOpenTrackPoints();
			state.updateUiMode();
		}
	}
	
	protected TrackItem createItem(ActivityMain parent, Screen screen, Database dataBase, long lItemId)
	{
		return new TrackItem(parent, screen, dataBase, lItemId);
	}
		
	private void updateLayersPath()
	{
		synchronized(bListAccessMutex)
		{
			for (TrackItem trackItem : items)
				trackItem.getLayer().setUpdateDrawPath();
		}		
	}

	private void updateOpenTrackPoints()
	{
		synchronized(bListAccessMutex)
		{
			final long lOpenTrackId = state.getOpenTrackId();
			if (lOpenTrackId != -1)
			{
				final TrackItem trackItem = getLoaderItem(lOpenTrackId);
				if (trackItem != null)
				{
					trackItem.updatePoints();					
				}
			}	
		}
	}

	protected void sortItems()
	{
		Collections.sort(items, cmpSortTracks);
	}

	private Comparator<TrackItem> cmpSortTracks = new Comparator<TrackItem>()
	{
		public int compare(TrackItem t1, TrackItem t2)
		{
			final long lTrack1 = t1.getId();
			final long lTrack2 = t2.getId();
			
			if (lTrack1 < lTrack2)
				return 1;
			if (lTrack1 > lTrack2)
				return -1;
			
			return 0;
		}		
	};
	
}
