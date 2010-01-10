package pl.tripcomputer.layers;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.MainState;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.gps.GpsLocation;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.map.GeoPoint;
import pl.tripcomputer.map.Mercator;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.map.ScreenPoint;
import android.graphics.*;


public abstract class Layer
{	
	//context
	protected CommonActivity parent = null;
	protected Screen screen = null;
	protected MainState state = null;

  //paint
	protected Paint mDefaultText = new Paint();

	//test time
	protected static long mTestLastFrameTime = 0;
		
  //location
	private GpsLocation gpsLocation = new GpsLocation();
	protected GeoPoint locationGeoPoint = new GeoPoint();
	protected ScreenPoint locationScreenPoint = new ScreenPoint();	
	
	
	//methods
	public Layer(CommonActivity parent, Screen screen)
	{
		this.parent = parent;
		this.screen = screen;
		this.state = parent.getMainState();

		//paint
		mDefaultText.setAntiAlias(true);
		mDefaultText.setARGB(255, 0, 0, 0);		
	}
			
	public Screen getScreen()
	{
		return screen;
	}
	
	public static void startTestTime()
	{
		mTestLastFrameTime = System.currentTimeMillis();
	}

	public static void stopTestTime()
	{
		mTestLastFrameTime = System.currentTimeMillis() - mTestLastFrameTime;
	}
	
	public abstract void initData();
  public abstract void surfaceSizeChanged(int width, int height, int iScreenOrientation);	
	public abstract void doDraw(Canvas cs, Rect rtBounds);
  public abstract void updateObjectsState();

  //location observer methods
  public void updateLocation()
  {
  	
  }

  public void startObserveLocation(Observer observer)
  {
  	//observe location status
  	ActivityMain.loader.gpsLocationStatus().addObserver(observer);
  }
  
	public void updateLocation(Observable observable, Object data)
	{
		if (observable instanceof GpsLocationStatus)
		{
			if (data == GpsLocationStatus.STATE_NEW_GPS_LOCATION)
			{
				synchronized(locationGeoPoint)
				{
					ActivityMain.loader.gpsLocationStatus().getCurrent(gpsLocation);
					
					if (gpsLocation.isEnabled())
					{
						locationGeoPoint.wgsLon = gpsLocation.wgsLon;
						locationGeoPoint.wgsLat = gpsLocation.wgsLat;
						locationGeoPoint.iAccuracy = gpsLocation.iAccuracy;
						
						Mercator.ConvertToFlat(locationGeoPoint);
						
						updateLocation();						
					}
				}					
			}
		}
	}

	public boolean isLocationEnabled()
	{
		return gpsLocation.isEnabled();
	}

	protected void updateLocationScreenPoint()
	{
		synchronized(locationGeoPoint)
		{
			screen.toScreenPoint(locationGeoPoint, locationScreenPoint);
		}
	}

}
