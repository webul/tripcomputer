package pl.tripcomputer.http;

import org.json.JSONException;
import org.json.JSONObject;


public class OperationResult
{
	//fields
	private final static String KEY_PARAM = "tc_operation_result"; 
	private final static String KEY_VALUE = "tc"; 
	
	//fields
	public final static int RESULT_NONE = 0;
	public final static int RESULT_LIST_EMPTY = 1;
	public final static int RESULT_LIST_TOO_LONG = 2;
	public final static int RESULT_SERVICE_BLOCKED = 3;

	//fields
	private boolean bSuccess = false;
	private int iResultCode = 0;
	
	
	//methods
	public void setSuccess()
	{
		bSuccess = true;
		iResultCode = RESULT_NONE;				
	}
	
	public void setFail(int iCode)
	{
		bSuccess = false;
		iResultCode = iCode;		
	}

	public void setFail()
	{
		bSuccess = false;
		iResultCode = RESULT_NONE;		
	}
	
	public boolean isSuccess()
	{
		return bSuccess;
	}
	
	public int getResultCode()
	{
		return iResultCode;
	}
	
	public static OperationResult createFromJSON(String sJSON)
	{
		try
		{
			JSONObject json = new JSONObject(sJSON);
			if (json != null)
			{
				final String sKeyParam = json.getString(KEY_PARAM);
				if ((sKeyParam != null) && (sKeyParam.equals(KEY_VALUE)))
				{				
					OperationResult item = new OperationResult();
					if (item.parse(json))
						return item;
				}
			}
		} catch (JSONException e) {
		}
		return null;
	}
	
	public boolean parse(JSONObject json)
	{
		try
		{
			bSuccess = json.getBoolean("bSuccess");
			iResultCode = json.getInt("iResultCode");			
						
			return true;						
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
						
			json.put("bSuccess", bSuccess);				
			json.put("iResultCode", iResultCode);

			return json.toString();
		} catch (JSONException e) {
		}
		return null;
	}
	
}
