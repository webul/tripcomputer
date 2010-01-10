package pl.tripcomputer.data.items;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;


public class WebDataItemWaypoint extends DataItemWaypoint
{
	//methods	
	public WebDataItemWaypoint()
	{
		super();
	}	
	
	public WebDataItemWaypoint(DataItemWaypoint item)
	{
		super(item);
	}
	
	private boolean parse(JSONObject json)
	{
		try
		{
			lTrackId = json.getLong("lTrackId");			
			bPublic = json.getBoolean("bPublic");
			sUID = json.getString("sUID");
			lon = json.getDouble("dLon");
			lat = json.getDouble("dLat");
			iAltitude = json.getInt("iAltitude");
			lTimeUTC = json.getLong("lTimeUTC");
			sName = json.getString("sName");
			sDescription = json.getString("sDescription");			
			iType = json.getInt("iType");
						
			return true;						
		} catch (JSONException e) {
		}
		return false;
	}
	
	private boolean parse_SimpleItem(JSONObject json)
	{
		try
		{
			sUID = json.getString("sUID");
			sName = json.getString("sName");
			bPublic = json.getBoolean("bPublic");
			lon = json.getDouble("dLon");
			lat = json.getDouble("dLat");
			iType = json.getInt("iType");
						
			return true;						
		} catch (JSONException e) {
		}
		return false;
	}
	
	public static WebDataItemWaypoint createFromJSON(String sJSON, boolean bSimpleItem)
	{
		try
		{
			JSONObject json = new JSONObject(sJSON);
			if (json != null)
			{
				WebDataItemWaypoint item = new WebDataItemWaypoint();				
				if (bSimpleItem)
				{
					if (item.parse_SimpleItem(json))
						return item;					
				} else {
					if (item.parse(json))
						return item;					
				}				
			}
		} catch (JSONException e) {
		}
		return null;
	}

	private boolean dataValid()
	{
		if (sUID == null)
			return false;
		if (sName == null)
			return false;		
		return true;
	}
	
	public String toJSON()
	{
		if (dataValid())
		{		
			JSONObject json = new JSONObject();
			
			try
			{
				json.put("lTrackId", lTrackId);				
				json.put("bPublic", bPublic);
				json.put("sUID", sUID);
				json.put("dLon", lon);
				json.put("dLat", lat);
				json.put("iAltitude", iAltitude);
				json.put("lTimeUTC", lTimeUTC);
				json.put("sName", sName);
				json.put("sDescription", sDescription);
				json.put("iType", iType);
	
				return json.toString();
			} catch (JSONException e) {
			}
		}
		return null;
	}
	
	public static ArrayList<DataItemWaypoint> stringToList(final String sData, boolean bSimpleItem)
	{	
		final ArrayList<DataItemWaypoint> listItems = new ArrayList<DataItemWaypoint>();
		
		final String[] vecItems = sData.split("\n");
		
		for (String sJSONItem : vecItems)
		{
			final WebDataItemWaypoint item = WebDataItemWaypoint.createFromJSON(sJSONItem, bSimpleItem);
			if (item != null)
			{
				listItems.add(item);
			}
		}

		return listItems;
	}
	
}

