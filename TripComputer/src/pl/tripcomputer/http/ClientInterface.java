package pl.tripcomputer.http;

import pl.tripcomputer.data.items.WebDataItemWaypoint;


public class ClientInterface extends ClientDataTransport
{
	//methods
	public ClientInterface(String sUserEmail, String sLanguageId)
	{
		super(sUserEmail, sLanguageId);
	}
	
	//returns access code to given email
	public boolean getAccessCode()
	{
		setAccessCode(getUserEmail());
		if (execRequest(CommonData.CMD_GET_ACCESS_CODE))
		{
			return true;
		}
		return false;
	}

	//returns access token with response
	public boolean getAccessToken(String sAccessCode)
	{
		setAccessToken(sAccessCode);
		if (execRequest(CommonData.CMD_GET_ACCESS_TOKEN))
		{
			return true;
		}
		return false;
	}
	
	public boolean postData(String sData)
	{
		if (execRequest(CommonData.CMD_POST_WAYPOINT, sData))
		{
			return true;
		}
		return false;
	}
	
	public boolean sendWaypoint(WebDataItemWaypoint item)
	{				
		if (item != null)
		{
			final String sData = item.toJSON();
			if (sData != null)
			{
				if (postData(sData))
				{
					return true;					
				}
			}
		}
		return false;
	}
	
	public boolean getWaypoint(String sUID)
	{
		if (execRequest(CommonData.CMD_GET_WAYPOINT, sUID))
		{
			return true;
		}
		return false;
	}

	public boolean getWaypointList(ParamsGetWaypointList params)
	{	
		if (execRequest(CommonData.CMD_GET_WAYPOINT_LIST, params.toJSON()))
		{
			return true;
		}
		return false;
	}

	public boolean getWaypointCollection(String sListUIDs)
	{
		if (execRequest(CommonData.CMD_GET_WAYPOINT_COLLECTION, sListUIDs))
		{
			return true;
		}
		return false;
	}
	
}
