package pl.tripcomputer.webservice;

import pl.tripcomputer.Command;
import pl.tripcomputer.CommandData;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.data.items.WebDataItemWaypoint;


public class AccessWaypointData extends WebServiceAccess
{
	//fields
	private static AccessWaypointData access = null;	
	
	protected DataItemWaypoint itemWaypoint = null;	
	protected WebDataItemWaypoint webItemWaypoint = null;

	
	//methods
	public AccessWaypointData(CommonActivity parent)
	{
		super(parent);
	}
	
	public static AccessWaypointData get()
	{
		return access;
	}
	
	public static void execute(CommonActivity parent, Command cmd, CommandData data)
	{
		access = new AccessWaypointData(parent);
		
		if (access.serviceAccessNotSet())
			return;
		
		//get local waypoint item
		access.itemWaypoint = parent.getDatabase().tableWaypoints().getWaypointItem(data.getRowId());
		if (access.itemWaypoint == null)
		{
			//cannot find waypoint
			UserAlert.show(parent, UserAlert.Result.DATA_FIND_ERROR);
		} else {
			//request web operation and show dialog
			access.requestWebOperation();
		}
	}
	
	public void eventAccessReady()
	{
		opGetWaypoint.execute(this, itemWaypoint.getUID());
	}

	//web operation
	private final OpGetWaypoint opGetWaypoint = new OpGetWaypoint()
	{
		public void eventOperationFinish(boolean bSuccees)
		{
			if (bSuccees)
			{
				//get waypoint returned by server
				webItemWaypoint = getWaypoint();
				
				//show waypoint upload dialog
				parent.getMain().showWaypointUpload();
			}
		}
	};

	public DataItemWaypoint getDataItemWaypoint()	
	{
		return itemWaypoint;
	}

	public WebDataItemWaypoint getWebDataItemWaypoint()	
	{
		return webItemWaypoint;
	}
		
}
