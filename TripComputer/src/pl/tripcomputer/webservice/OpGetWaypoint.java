package pl.tripcomputer.webservice;

import pl.tripcomputer.data.items.WebDataItemWaypoint;


public abstract class OpGetWaypoint extends WebServiceOperation
{
	private String sWaypointUID = null;
	private WebDataItemWaypoint waypoint = null;

	public void execute(WebServiceAccess access, String sWaypointUID)
	{
		this.sWaypointUID = sWaypointUID;
				
		super.execute(access);
	}

	protected boolean eventOperationExec()
	{
		boolean bResult = false;
		
		client.setAccessToken(access.getAccessToken());
		
		if (client.getWaypoint(sWaypointUID))
		{
			bResult = client.statusCodeOk();
			
			if (bResult)
			{
				final String sData = client.getResponseData();				
				if (sData.length() > 0)
				{
					waypoint = WebDataItemWaypoint.createFromJSON(sData, false);					
				}
			}
		}
		
		return bResult;
	}
	
	protected boolean eventOperationAlert(boolean bSuccees)
	{
		return false;
	}
	
	public WebDataItemWaypoint getWaypoint()
	{
		return waypoint;
	}

}
