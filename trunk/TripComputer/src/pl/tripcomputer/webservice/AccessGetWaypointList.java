package pl.tripcomputer.webservice;

import java.util.ArrayList;

import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.http.ParamsGetWaypointList;


public class AccessGetWaypointList extends WebServiceAccess
{
	//event interface
	public static abstract class WaypointListStatus
	{
		public abstract void eventListReady(final ArrayList<DataItemWaypoint> items);		
	}

	private WaypointListStatus status = null;
	
	private ParamsGetWaypointList params = new ParamsGetWaypointList();
	
	
	//methods
	public AccessGetWaypointList(CommonActivity parent)
	{
		super(parent);
	}

	public void setStatusListener(WaypointListStatus status)
	{
		this.status = status;
	}
	
	public ParamsGetWaypointList getParams()
	{
		return params;
	}
	
	public void execute(ParamsGetWaypointList findParams)
	{	
		if (serviceAccessNotSet())
			return;
		
		params.bPublic = findParams.bPublic;
		params.dLon = findParams.dLon;
		params.dLat = findParams.dLat;
		params.iRangeType = findParams.iRangeType;
		params.iPageIndex = findParams.iPageIndex;
		
		requestWebOperation();
	}

	public void eventAccessReady()
	{		
		opGetWaypointList.execute(this, params);
	}

	//web operation
	private final OpGetWaypointList opGetWaypointList = new OpGetWaypointList()
	{
		public void eventOperationFinish(boolean bSuccess)
		{
			if (bSuccess)
			{
				ArrayList<DataItemWaypoint> items = getItems();
				if ((items != null) && (!items.isEmpty()))
				{
					items = ActivityMain.loader.removeExistingWaypoints(items);
					
					if (items.isEmpty())
					{
						access.showAlertNoWaypointsToDownload();
					} else {						
						if (status != null)
							status.eventListReady(items);
					}
					
				} else {
					access.showAlertNoWaypointsFound();
				}
			}
		}		
	};
	
}
