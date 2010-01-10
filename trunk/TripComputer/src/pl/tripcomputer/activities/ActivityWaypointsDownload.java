package pl.tripcomputer.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import pl.tripcomputer.R;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.common.DataTableOperation;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.data.tables.DataTableWaypoints;
import pl.tripcomputer.http.ParamsGetWaypointList;
import pl.tripcomputer.lists.WaypointDownloadListAdapter;
import pl.tripcomputer.lists.WaypointDownloadListItem;
import pl.tripcomputer.webservice.AccessGetWaypointCollection;
import pl.tripcomputer.webservice.AccessGetWaypointList;
import pl.tripcomputer.webservice.AccessGetWaypointCollection.WaypointCollectionStatus;
import pl.tripcomputer.webservice.AccessGetWaypointList.WaypointListStatus;


public class ActivityWaypointsDownload extends CommonActivity
{
	//fields
	public static ArrayList<DataItemWaypoint> itemsWaypoints = null;
	public static ParamsGetWaypointList findParams = new ParamsGetWaypointList();

	//fields
	private AccessGetWaypointCollection accessGetWaypointCollection = null;
	private AccessGetWaypointList accessGetWaypointList = null;
	
	//fields
  private ListView list = null;
  private WaypointDownloadListAdapter listAdapter = null;  
  private Location locCurrent = null;
  
  private WaypointDownloadListItem itemMoreItems = null; 
	

	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_waypoints_download);	  

	  setSubTitle(R.string.title_waypoints_download);
	  
	  accessGetWaypointCollection = new AccessGetWaypointCollection(this);
	  accessGetWaypointCollection.setStatusListener(eventCollectionStatus);
	  
	  accessGetWaypointList = new AccessGetWaypointList(this);
	  accessGetWaypointList.setStatusListener(eventListStatus);
	  
	  //get location
		final Location loc = ActivityMain.getLastLocation();
	  if (loc != null)
	  	locCurrent = new Location(loc);

	  list = (ListView)this.findViewById(R.id.list);
	  
	  //fill list
	  reloadList();
	}

	private void initWaypointList()
	{
	  //remove items, that already exists in local database
		itemsWaypoints = ActivityMain.loader.removeExistingWaypoints(itemsWaypoints);	  	  

	  //sort items by distance to current location
	  if (locCurrent != null)
	  {
	  	Collections.sort(itemsWaypoints, cmpSortWaypointsByDistance);
	  }		
	}
		
	private void reloadList()
	{
		if (itemsWaypoints == null)
			return;
		
		initWaypointList();
		
	  //create list
	  ArrayList<WaypointDownloadListItem> listData = new ArrayList<WaypointDownloadListItem>();
  
	  for (DataItemWaypoint item : itemsWaypoints)
	  {
	  	final WaypointDownloadListItem itemDownload = new WaypointDownloadListItem(this, item, locCurrent);
	  	
			listData.add(itemDownload);
	  }
	  
	  //enable more items button
	  itemMoreItems = null;	  
	  if (ParamsGetWaypointList.isNextPage(listData.size()))
		{
	  	itemMoreItems = new WaypointDownloadListItem(this);	  	
			itemMoreItems.setMoreItemsOnClickEvent(eventMoreItemsOnClick);
			listData.add(itemMoreItems);
		}
  		  		
	  listAdapter = new WaypointDownloadListAdapter(this, listData);
	  list.setAdapter(listAdapter);
	}
  
	private Comparator<DataItemWaypoint> cmpSortWaypointsByDistance = new Comparator<DataItemWaypoint>()
	{
		public int compare(DataItemWaypoint wpt1, DataItemWaypoint wpt2)
		{
			final double dDistance1 = wpt1.distance(locCurrent.getLongitude(), locCurrent.getLatitude());
			final double dDistance2 = wpt2.distance(locCurrent.getLongitude(), locCurrent.getLatitude());

			if (dDistance1 > dDistance2)
				return 1;
			if (dDistance1 < dDistance2)
				return -1;

			return 0;
		}
	};
	
	public boolean onClickedRevert(Bundle data)
	{
		return true;
	}
	
	public boolean onClickedDone(Bundle data)
	{
		final ArrayList<String> itemsToDownload = listAdapter.getSelectedItems();
		
		if (itemsToDownload.isEmpty())
		{
			UserAlert.show(this, getString(R.string.alert_no_waypoints_selected));
		} else {
			accessGetWaypointCollection.execute(itemsToDownload);
		}
		
	  return false;
	}
	
	private WaypointCollectionStatus eventCollectionStatus = new WaypointCollectionStatus()
	{
		public void eventCollectionReady(ArrayList<DataItemWaypoint> items)
		{
			final ActivityWaypointsDownload parent = ActivityWaypointsDownload.this;
			final DataTableWaypoints table = parent.getDatabase().tableWaypoints();				

			//add items to table	
			int iAddedCount = 0;
			for (final DataItemWaypoint item : items)
			{
				final long lNewRowId = table.addWaypoint(parent, item);
				if (lNewRowId == -1)
				{
					break;
				} else {
		  		//update observers
					table.sendOperation(lNewRowId, DataTableOperation.OP_INSERT);
					iAddedCount++;
				}
			}
			
			//reload list
			parent.reloadList();
			
			//check result
			if (iAddedCount == 0)
			{				
				UserAlert.show(parent, getString(R.string.alert_no_waypoints_added));
			}
		}
	};
	
	private WaypointListStatus eventListStatus = new WaypointListStatus()
	{
		public void eventListReady(ArrayList<DataItemWaypoint> items)
		{
			//get new list
			itemsWaypoints = ActivityMain.loader.removeExistingWaypoints(items);		  
			//reload list
			reloadList();			
		}
	};	
	
	private View.OnClickListener eventMoreItemsOnClick = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			findParams.iPageIndex++;
			accessGetWaypointList.execute(findParams);						
		}
	};
	
}
