package pl.tripcomputer.gps;

import pl.tripcomputer.ServiceDataReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;


public class GpsLocation
{
	//fields
	private static String sAppNameKey = "TripComputer";
	private static String sAppNameValue = "GpsLocation";
	
	//fields
	public boolean bEnabled = false;
	
	public long lPointID = -1;	
	public int iSaveMode = GpsLogger.MODE_NONE;
	
	public double wgsLon = 0;
	public double wgsLat = 0;
	
	public int iAltitude = 0;
	public int iAccuracy = 0;
	public int iBearing = 0;

	public long lTimeUTC = 0;
	
	
	//methods
	public void clear()
	{
		this.bEnabled = false;
		
		this.lPointID = -1;
		this.iSaveMode = GpsLogger.MODE_NONE;
		
		this.wgsLon = 0;
		this.wgsLat = 0;
				
		this.iAltitude = 0;
		this.iAccuracy = 0;
		this.iBearing = 0;
		
		this.lTimeUTC = 0;		
	}
	
	public boolean isEnabled()
	{
		return bEnabled;
	}
	
	public void send(Context context)
	{
		final Intent intent = new Intent(context, ServiceDataReceiver.class);
			
		intent.putExtra(sAppNameKey, sAppNameValue);
	
		intent.putExtra("lPointID", lPointID);
		intent.putExtra("iSaveMode", iSaveMode);
		
		intent.putExtra("wgsLon", wgsLon);
		intent.putExtra("wgsLat", wgsLat);
		
		intent.putExtra("iAltitude", iAltitude);
		intent.putExtra("iAccuracy", iAccuracy);
		intent.putExtra("iBearing", iBearing);
		
		intent.putExtra("lTimeUTC", lTimeUTC);
		
		context.sendOrderedBroadcast(intent, null);
	}

	public boolean get(Intent intent)
	{	
		if ((intent != null) && (intent.hasExtra(sAppNameKey)))
		{	
			final String sNewAppNameValue = intent.getStringExtra(sAppNameKey);			
			if (sNewAppNameValue != null)
			{
				if (sNewAppNameValue.equals(sAppNameValue))
				{
					this.bEnabled = true;
					
					this.lPointID = intent.getLongExtra("lPointID", -1);
					this.iSaveMode = intent.getIntExtra("iSaveMode", GpsLogger.MODE_NONE);
					
					this.wgsLon = intent.getDoubleExtra("wgsLon", 0);
					this.wgsLat = intent.getDoubleExtra("wgsLat", 0);
							
					this.iAltitude = intent.getIntExtra("iAltitude", 0);
					this.iAccuracy = intent.getIntExtra("iAccuracy", 0);
					this.iBearing = intent.getIntExtra("iBearing", 0);
					
					this.lTimeUTC = intent.getLongExtra("lTimeUTC", 0);
	
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean set(Location location, long lPointID, int iLocationSaveMode)
	{
		if (location != null)
		{		
			this.bEnabled = true;
			
			this.lPointID = lPointID;
			this.iSaveMode = iLocationSaveMode;
			
			this.wgsLon = location.getLongitude();
			this.wgsLat = location.getLatitude();
			
			this.iAltitude = (int)location.getAltitude();
			this.iAccuracy = (int)location.getAccuracy();
			this.iBearing = (int)location.getBearing();
	
			this.lTimeUTC = location.getTime();
			
			return true;
		}
		return false;
	}

	public boolean set(GpsLocation location)
	{
		if (location != null)
		{
			this.bEnabled = true;
			
			this.lPointID = location.lPointID;
			this.iSaveMode = location.iSaveMode;
			
			this.wgsLon = location.wgsLon;
			this.wgsLat = location.wgsLat;
			
			this.iAltitude = location.iAltitude;
			this.iAccuracy = location.iAccuracy;
			this.iBearing = location.iBearing;
	
			this.lTimeUTC = location.lTimeUTC;
			
			return true;
		}
		return false;
	}
		
}
