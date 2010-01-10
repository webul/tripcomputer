package pl.tripcomputer.gps;

import java.util.Observable;

import pl.tripcomputer.Preferences;
import pl.tripcomputer.R;
import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;


public abstract class GpsReader extends Observable
{
	//fields
	public final static float MAXMIMUM_ACCURACY = 5.0f;	
	public final static float MINIMUM_ACCURACY = 200.0f;
	public final static Integer LOCATION_READY = 1;

	private final static String sProviderName = LocationManager.GPS_PROVIDER;		
	
	//fields
	protected String sMsg_ServiceNotEnabled = null;
	
	//fields
	protected Context context = null;
	protected Preferences prefs = null;
		
	private LocationManager locManager = null;	
	private LocationProvider locProvider = null;
	private GpsStatus gpsStatus = null;
	
	protected Boolean bStateMutex = false;
	protected Boolean bStarted = false;
	
	protected Vibrator vibrator = null;
	
	
	//methods
	public GpsReader(Context context, Preferences prefs)
	{
		this.context = context;
		this.prefs = prefs;

		vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);		
		
		locManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
		sMsg_ServiceNotEnabled = context.getString(R.string.notify_msg_loc_service_disabled);
		
		getLocationProvider();
	}
	
	private LocationProvider getLocationProvider()
	{
		//get location provider object
		locProvider = locManager.getProvider(sProviderName);
		
		return locProvider;
	}
		
	public boolean isEnabled()
	{
		if (locProvider == null)
			return false;
		if (sProviderName == null)
			return false;
		return locManager.isProviderEnabled(sProviderName);
	}
	
	public boolean isLocationProvider()
	{				
		return (locProvider != null);
	}		

	public boolean isStarted()
	{
		synchronized (bStateMutex)
		{
			return bStarted;
		}
	}
		
	//status event
	private GpsStatus.Listener eventGpsStatus = new GpsStatus.Listener()
	{
		public void onGpsStatusChanged(int event)
		{
			//get status object
			if (gpsStatus == null)
			{
				gpsStatus = locManager.getGpsStatus(null);
			} else {
				locManager.getGpsStatus(gpsStatus);
			}
			//set status data
			if (gpsStatus != null)
			{
				//get finish status 
				if (event == GpsStatus.GPS_EVENT_STOPPED)
				{					
					setChanged();
					notifyObservers(LOCATION_READY);
				}
			}
		}		
	};
	
	//location event
	private LocationListener eventLocation = new LocationListener()
	{
		public void onLocationChanged(Location location)
		{	
			if (location != null)
			{
				final boolean bAccuracyAcceptable = isAccuracyAcceptable(location);

				onGpsLocationChanged(location, bAccuracyAcceptable);
				
				if (bAccuracyAcceptable)
				{
					setChanged();
					notifyObservers(LOCATION_READY);
				}
			}
		}

		public void onProviderDisabled(String provider)
		{
		}

		public void onProviderEnabled(String provider)
		{
		}

		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			switch (status)
			{
			case LocationProvider.OUT_OF_SERVICE:
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				break;
			case LocationProvider.AVAILABLE:
				break;				
			}
		}
	};

	
	//override to get locations
	protected abstract void onGpsLocationChanged(Location location, boolean bAccuracyAcceptable);
	
	
	protected void start(int iUpdateDurationSeconds)	
	{		
		if (isStarted())
			return;
		
		if (!isLocationProvider())
			getLocationProvider();
		
		prefs.load();
		
		if (isEnabled())
		{
			synchronized (bStateMutex)
			{
				bStarted = false;
				try
				{
					locManager.requestLocationUpdates(sProviderName, (iUpdateDurationSeconds * 1000), 0, eventLocation);										
					locManager.addGpsStatusListener(eventGpsStatus);
					
					bStarted = true;
				} catch (Exception e) {					
				}									
			}
		} else {			
			Toast.makeText(context, sMsg_ServiceNotEnabled, Toast.LENGTH_LONG).show();
		}
	}	
	
	public void stop()
	{
		if (isStarted())
		{			
			synchronized (bStateMutex)
			{
				locManager.removeUpdates(eventLocation);
				locManager.removeGpsStatusListener(eventGpsStatus);
				
				bStarted = false;				
			}
		}
	}

	public void forceStop()
	{
		synchronized (bStateMutex)
		{
			try
			{
				locManager.removeUpdates(eventLocation);
				locManager.removeGpsStatusListener(eventGpsStatus);
			} catch (Exception e) {
			}
			bStarted = false;				
		}
	}
	
	public Location getLastLocation()
	{
		if (isLocationProvider())
		{
			return locManager.getLastKnownLocation(sProviderName);
		}
		return null;
	}
	
	private boolean isAccuracyAcceptable(Location location)
	{
		if (location.hasAccuracy())
		{
			if (location.getAccuracy() < MINIMUM_ACCURACY)
			{
				if (location.getAccuracy() < prefs.iLocationAccuracy)
				{				
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}			
		} else {
			return false;
		}
	}

	protected void vibrate()
	{
		if (vibrator != null)
		{
			vibrator.vibrate(50);			
		}
	}
	
}
