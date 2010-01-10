package pl.tripcomputer.loader;

import java.util.ArrayList;

import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.ui.StatusMessage;


public class WaypointLoader extends Loader
{
  //fields
  private WaypointCollection waypointList = null;
	
  //selected object
  private final Boolean bSelectedObjectMutex = false;
  private WaypointItem selectedItem = null;

  
	//methods
	public WaypointLoader(ActivityMain parent, Screen screen, GpsLocationStatus gpsLocationStatus)
	{
		super(parent, screen, gpsLocationStatus);

		waypointList = new WaypointCollection(parent, screen, gpsLocationStatus);		
	}
		
	public WaypointCollection items()
	{
		return waypointList;
	}
	
	protected void loadAllItems()
	{
		waypointList.loadAllItems();		
	}
	
	public synchronized void selectNextObject()
	{
		final long lSelectedWaypointId = state.getSelectedWaypointId();
		
		synchronized(dataBase.tableWaypoints())
		{		
			final ArrayList<Long> listVisibleWaypoints = dataBase.tableWaypoints().getVisibleWaypointsRotated(lSelectedWaypointId);
			selectNextObject(lSelectedWaypointId, listVisibleWaypoints);
		}
	}

	public void clearWaypointSelection()
	{
		setSelectedWaypoint(null);
		state.setSelectedWaypointId(-1, false);		
	}
	
	private void setSelectedWaypoint(WaypointItem item)
	{
		synchronized(bSelectedObjectMutex)
		{
			selectedItem = item;						
		}		
	}

	public WaypointItem getSelectedWaypoint()
	{
		synchronized(bSelectedObjectMutex)
		{
			return selectedItem;						
		}		
	}
	
	protected boolean selectObject(long lObjectId)
	{				
		boolean bSuccess = false;
		
		synchronized(waypointList)
		{
			//get item from list
			final WaypointItem waypointItem = waypointList.getLoaderItem(lObjectId);

			//show item
			if ((waypointItem != null) && waypointItem.isVisible()) 
			{
				setSelectedWaypoint(waypointItem);
								
				state.setSelectedWaypointId(lObjectId, true);
				
				waypointItem.getLayer().setResetViewPort();
				
				//show status message of selected item
				StatusMessage.showText(waypointItem.getDescription());

				bSuccess = true;
			}
		}

		if (!bSuccess)
			clearWaypointSelection();
		
		return bSuccess;
	}	
		
	public ArrayList<String> getWaypointListUID()
	{
		final ArrayList<String> UIDs = new ArrayList<String>();
		synchronized(waypointList)
		{
			final ArrayList<Long> listIDs = waypointList.getIdList();
			for (Long lID : listIDs)
			{
				final WaypointItem item = waypointList.getLoaderItem(lID);
				UIDs.add(item.dataWaypoint.getUID());
			}
		}
		return UIDs;
	}
	
	public ArrayList<DataItemWaypoint> removeExistingItems(ArrayList<DataItemWaypoint> items)
	{
		final ArrayList<String> listExistingUIDs = getWaypointListUID();
		
		final ArrayList<DataItemWaypoint> newList = new ArrayList<DataItemWaypoint>();
		
		for (DataItemWaypoint item : items)
		{
			//if UID not found
			if (listExistingUIDs.indexOf(item.getUID()) == -1)
			{
				newList.add(item);				
			}
		}
				
		return newList; 
	}

}
