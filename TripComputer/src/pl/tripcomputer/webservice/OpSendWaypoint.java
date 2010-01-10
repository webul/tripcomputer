package pl.tripcomputer.webservice;

import pl.tripcomputer.data.items.WebDataItemWaypoint;
import pl.tripcomputer.http.OperationResult;


public abstract class OpSendWaypoint extends WebServiceOperation
{
	private WebDataItemWaypoint waypoint = null;
	private OperationResult opResult = null; 

	public void execute(WebServiceAccess access, WebDataItemWaypoint waypoint)
	{
		this.waypoint = waypoint;
				
		super.execute(access);
	}

	protected boolean eventOperationExec()
	{
		boolean bResult = false;
		
		client.setAccessToken(access.getAccessToken());
		
		if (client.sendWaypoint(waypoint))
		{
			bResult = client.statusCodeOk();
			
			if (bResult)
			{
				final String sData = client.getResponseData();				
				if (sData.length() > 0)
				{
					opResult = OperationResult.createFromJSON(sData);
				}
			}
		}
		
		return bResult;
	}
	
	protected boolean eventOperationAlert(boolean bSuccees)
	{
		return false;
	}
	
	public OperationResult getResult()
	{
		return opResult;
	}
	
}
