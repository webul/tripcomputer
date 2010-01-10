package pl.tripcomputer.gps;

import java.util.Observable;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;


public class GpsLocationStatus extends Observable
{
	//fields
	public final static Integer STATE_NONE = 0;
	public final static Integer STATE_NEW_GPS_LOCATION = 1;

	//shared intent to process 
	private Intent intentToProcess = null;
	private final Boolean bIntentAccessMutex = false;

	//shared location to process 
	private Location locationToProcess = null;
	private final Boolean bLocationAccessMutex = false;
	
	//location currently get from gps
	private GpsLocation locCurrent = new GpsLocation();
	
	//location lastly saved to database
	private GpsLocation locLastSaved = new GpsLocation();
	
	//fields
	private Handler mHandler = new Handler();
	
	
	//methods
	public void postIntent(Intent intent)
	{
		synchronized(bIntentAccessMutex)
		{
			intentToProcess = new Intent(intent);
			
			//run thread
	    Thread t = new Thread()
	    {
	      public void run()
	      {
	      	set(intentToProcess);
	      	intentToProcess = null;	      	
	      }
	    };
	    t.start();
		}
	}
	
	private void set(Intent intent)
	{
		synchronized(locCurrent)
		{
			if (locCurrent.get(intent))
			{			
				//store last saved location
				if (isSaveMode())
				{
					synchronized(locLastSaved)
					{
						locLastSaved.set(locCurrent);
					}
				}
									
				//notify observers of location change
				mHandler.removeCallbacks(mUpdateObserversTask);
				mHandler.post(mUpdateObserversTask);
			}
		}		
	}

	public void postLocation(Location location)
	{
		if (location == null)
			return;
		
		synchronized(bLocationAccessMutex)
		{
			locationToProcess = new Location(location);
			
			//run thread
	    Thread t = new Thread()
	    {
	      public void run()
	      {
	      	set(locationToProcess);
	      	locationToProcess = null;	      	
	      }
	    };
	    t.start();
		}
	}
	
	private void set(Location location)
	{
		if (location == null)
			return;
		
		synchronized(locCurrent)
		{
			if (locCurrent.set(location, -1, GpsLogger.MODE_NONE))
			{
				//notify observers of location change
				mHandler.removeCallbacks(mUpdateObserversTask);
				mHandler.post(mUpdateObserversTask);
			}
		}		
	}
	
	private Runnable mUpdateObserversTask = new Runnable()
	{
		public void run()
		{
			try
			{
				GpsLocationStatus.this.setChanged();
				GpsLocationStatus.this.notifyObservers(STATE_NEW_GPS_LOCATION);
			} catch (Exception e) {				
			}
		}
	};
		
	public void getCurrent(GpsLocation out)
	{
		synchronized(locCurrent)
		{
			out.set(locCurrent);
		}
	}

	public void getLastSaved(GpsLocation out)
	{
		synchronized(locLastSaved)
		{
			out.set(locLastSaved);
		}
	}
	
	public boolean isSaveMode()
	{
		boolean bSaveMode = false;
		synchronized(locCurrent)
		{
			bSaveMode = (locCurrent.iSaveMode != GpsLogger.MODE_NONE);
		}
		return bSaveMode;
	}

	public int getAccuracy()
	{
		int iValue = 0;
		synchronized(locCurrent)
		{
			iValue = locCurrent.iAccuracy;
		}
		return iValue;
	}
		
}
