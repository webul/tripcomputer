package pl.tripcomputer.loader;

import java.util.Collections;
import java.util.Comparator;

import android.location.Location;

import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.map.Screen;


public class WaypointCollection extends LoaderItemCollection<WaypointItem>
{
	//fields
	public final static int SORT_BY_ID = 0;
	public final static int SORT_BY_DISTANCE = 1;
	
	//fields
	private Location locCurrent = null;
	private int iSortMode = SORT_BY_ID;
	
		
	//methods
	public WaypointCollection(ActivityMain parent, Screen screen, GpsLocationStatus gpsLocationStatus)
	{
		super(parent, screen, gpsLocationStatus, parent.getDatabase().tableWaypoints());		
	}

	protected void updateForResetViewPort()
	{

	}

	protected void updateForNewLocation(boolean locationSaved)
	{

	}
	
	protected WaypointItem createItem(ActivityMain parent, Screen screen, Database dataBase, long lItemId)
	{
		return new WaypointItem(parent, screen, dataBase, lItemId);
	}

	protected void sortItems()
	{
		final Location loc = ActivityMain.getLastLocation();
	  if (loc == null)
	  {
			Collections.sort(items, cmpSortWaypoints);
	  	iSortMode = SORT_BY_ID;
	  } else {
	  	locCurrent = new Location(loc);
			Collections.sort(items, cmpSortWaypointsByDistance);
	  	iSortMode = SORT_BY_DISTANCE;
	  }
	}
	
	//sort by distance
	private Comparator<WaypointItem> cmpSortWaypointsByDistance = new Comparator<WaypointItem>()
	{
		public int compare(WaypointItem wpt1, WaypointItem wpt2)
		{
			final double dDistance1 = wpt1.dataWaypoint.distance(locCurrent.getLongitude(), locCurrent.getLatitude());
			final double dDistance2 = wpt2.dataWaypoint.distance(locCurrent.getLongitude(), locCurrent.getLatitude());

			if (dDistance1 > dDistance2)
				return 1;
			if (dDistance1 < dDistance2)
				return -1;
			
			return 0;
		}
	};
	
	//sort by inserting order
	private Comparator<WaypointItem> cmpSortWaypoints = new Comparator<WaypointItem>()
	{
		public int compare(WaypointItem wpt1, WaypointItem wpt2)
		{
			final long lID1 = wpt1.getId();
			final long lID2 = wpt2.getId();
			
			if (lID1 < lID2)
				return 1;
			if (lID1 > lID2)
				return -1;			
			
			return 0;
		}		
	};

	public int getSortMode()
	{
		return iSortMode;
	}
	
}
