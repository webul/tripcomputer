package pl.tripcomputer.webservice;

import pl.tripcomputer.UserAlert;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.data.items.WebDataItemWaypoint;


public class AccessSendWaypoint extends WebServiceAccess
{
	//fields
	protected WebDataItemWaypoint webItemWaypoint = null;

	
	//methods
	public AccessSendWaypoint(CommonActivity parent)
	{
		super(parent);
	}

	public void execute(String sAccessCode, DataItemWaypoint itemWaypoint)
	{
		if (serviceAccessNotSet())
			return;
		
		webItemWaypoint = new WebDataItemWaypoint(itemWaypoint);
		
		setAccessCode(sAccessCode);
		requestWebOperation();
	}

	public void eventAccessReady()
	{
		opSendWaypoint.execute(this, webItemWaypoint);		
	}

	//web operation
	private final OpSendWaypoint opSendWaypoint = new OpSendWaypoint()
	{
		public void eventOperationFinish(boolean bSuccees)
		{
			if (bSuccees)
			{
				if (getResult().isSuccess())
				{
					//set upload status in local waypoint
					final boolean bPublic = webItemWaypoint.isPublic();
					
					parent.getDatabase().tableWaypoints().setUploadStatus(webItemWaypoint.iID, bPublic, true);
					
					//close activity
					parent.closeActivityWithResultOK();
				} else {
					UserAlert.show(parent, UserAlert.Result.SERVER_REJECTED_DATA);
				}
			}
		}		
	};
	
}
