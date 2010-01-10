package pl.tripcomputer.http;

import org.json.JSONException;
import org.json.JSONObject;


public class RequestParams
{
	//fields
	private final static String KEY_PARAM = "tc_request_params"; 
	private final static String KEY_VALUE = "tc"; 
	
	
	//methods
	public boolean parse(String sJSON)
	{
		try
		{
			JSONObject json = new JSONObject(sJSON);
			if (json != null)
			{
				try
				{
					final String sKeyParam = json.getString(KEY_PARAM);
					if ((sKeyParam != null) && (sKeyParam.equals(KEY_VALUE)))
					{
						getValues(json);						
						if (dataValid())						
							return true;
					}
				} catch (JSONException e) {
				}				
			}
		} catch (JSONException e) {
		}
		return false;
	}
	
	public String toJSON()
	{
		JSONObject json = new JSONObject();		
		try
		{
			json.put(KEY_PARAM, KEY_VALUE);
			setValues(json);
			return json.toString();
		} catch (JSONException e) {
		}
		return null;
	}
	
	public boolean dataValid()
	{
		return false;
	}
	
	public void getValues(JSONObject json) throws JSONException
	{
		
	}
	
	public void setValues(JSONObject json) throws JSONException
	{
		
	}
	
}
