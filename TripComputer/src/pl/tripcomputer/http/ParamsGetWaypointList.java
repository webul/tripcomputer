package pl.tripcomputer.http;

import org.json.JSONException;
import org.json.JSONObject;


public class ParamsGetWaypointList extends RequestParams
{
	//fields
	public static final int RANGE_MIN = 0;
	public static final int RANGE_MAX = 2;

	//fields
	public static final int MAX_ITEMS_PER_PAGE = 100;
	
	//fields
	public double dLon = 0; 
	public double dLat = 0; 
	public boolean bPublic = true; 
	public int iRangeType = 0;
	public int iPageIndex = 0;


	//methods
	public ParamsGetWaypointList()
	{	
	}
	
	public ParamsGetWaypointList(ParamsGetWaypointList params)
	{
		this.dLon = params.dLon; 
		this.dLat = params.dLat;
		this.bPublic = params.bPublic; 
		this.iRangeType = params.iRangeType;
		this.iPageIndex = params.iPageIndex;
	}
	
	public void getValues(JSONObject json) throws JSONException
	{
		super.getValues(json);

		dLon = json.getDouble("dLon");
		dLat = json.getDouble("dLat");
		bPublic = json.getBoolean("bPublic");
		iRangeType = json.getInt("iRangeType");
		iPageIndex = json.getInt("iPageIndex");
	}
	
	public void setValues(JSONObject json) throws JSONException
	{
		super.setValues(json);

		json.put("dLon", dLon);
		json.put("dLat", dLat);
		json.put("bPublic", bPublic);
		json.put("iRangeType", iRangeType);
		json.put("iPageIndex", iPageIndex);
	}
	
	public boolean dataValid()
	{
		if ((iRangeType >= RANGE_MIN) && (iRangeType <= RANGE_MAX))
		{
			if ((dLon < -180) || (dLon > 180))
				return false;
			
			if ((dLat < -90) || (dLat > 90))
				return false;						
					
			return true;
		}		
		return false;
	}
	
	public static boolean isNextPage(int iItemsCount)
	{
		return (iItemsCount >= MAX_ITEMS_PER_PAGE);
	}

}
