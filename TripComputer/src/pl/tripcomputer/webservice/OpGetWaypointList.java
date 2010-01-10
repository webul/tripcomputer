package pl.tripcomputer.webservice;

import java.util.ArrayList;

import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.data.items.WebDataItemWaypoint;
import pl.tripcomputer.http.OperationResult;
import pl.tripcomputer.http.ParamsGetWaypointList;


public abstract class OpGetWaypointList extends WebServiceOperation
{
	private ParamsGetWaypointList params = null;
	private ArrayList<DataItemWaypoint> listItems = null;  
	private OperationResult opResult = null; 
		
	public void execute(WebServiceAccess access, ParamsGetWaypointList params)
	{
		this.params = new ParamsGetWaypointList(params);

		super.execute(access);
	}

	protected boolean eventOperationExec()
	{
		boolean bResult = false;
		
		client.setAccessToken(access.getAccessToken());
		
		if (client.getWaypointList(params))
		{
			bResult = client.statusCodeOk();
			
			if (bResult)
			{
				final String sData = client.getResponseData();				
				if (sData.length() > 0)
				{					
					opResult = OperationResult.createFromJSON(sData);					
					if (opResult == null)
					{
						listItems = WebDataItemWaypoint.stringToList(sData, true);
					}					
				}
			}
		}
		
		return bResult;
	}
	
	protected boolean eventOperationAlert(boolean bSuccees)
	{
		return false;
	}
		
	public ArrayList<DataItemWaypoint> getItems()
	{
		return listItems;
	}
	
}
