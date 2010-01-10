package pl.tripcomputer.webservice;

import java.util.ArrayList;

import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.data.items.WebDataItemWaypoint;
import pl.tripcomputer.http.OperationResult;


public abstract class OpGetWaypointCollection extends WebServiceOperation
{
	private ArrayList<String> itemsWaypoints = null;
	private ArrayList<DataItemWaypoint> listItems = new ArrayList<DataItemWaypoint>();  
	private OperationResult opResult = null; 

	public void execute(WebServiceAccess access, ArrayList<String> itemsWaypoints)
	{
		this.itemsWaypoints = itemsWaypoints;
				
		super.execute(access);
	}

	private String getListUIDs()
	{
		String sList = "";
		for (int i = 0; i < itemsWaypoints.size(); i++)
			sList += itemsWaypoints.get(i) + "\n";
		return sList;		
	}
	
	protected boolean eventOperationExec()
	{
		boolean bResult = false;
		
		client.setAccessToken(access.getAccessToken());
						
		if (client.getWaypointCollection(getListUIDs()))
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
						listItems = WebDataItemWaypoint.stringToList(sData, false);
					}					
				}
			}
		}
		
		return bResult;
	}
	
	protected boolean eventOperationAlert(boolean bSuccees)
	{
		if (opResult != null)
		{
			if (!opResult.isSuccess())
			{
				if (opResult.getResultCode() == OperationResult.RESULT_LIST_EMPTY)
				{
					access.showAlertNoWaypointsFound();
					return true;
				}						
			}
		}
		return false;
	}
		
	public ArrayList<DataItemWaypoint> getItems()
	{
		return listItems;
	}	

}
