package pl.tripcomputer.compass;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.Command;
import pl.tripcomputer.MainState;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.GeoPoint;
import pl.tripcomputer.map.Mercator;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.map.ScreenPoint;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.location.Location;


public class CompassRotation implements Observer
{
	//fields
	private static CompassRotation self = null;
	
	//fields
	private MainState state = null;
	
	//fields
	private Compass compass = null;
	private boolean bEnabled = false;

	//fields
	private boolean bCanvasSaved = false;
	
  //fields rotation
  private float fAzimuth = 0;
  private float fCenterX = 0;
  private float fCenterY = 0;
  private boolean bIsLocation = false;
  private GeoPoint geoPoint = new GeoPoint();  
  private ScreenPoint screenPoint = new ScreenPoint();
  private Matrix matrix = new Matrix(); 
	private float[] points = { 0,0 };
	
	
	//methods
	public CompassRotation(CommonActivity parent)
	{
		self = this;
		
		compass = new Compass(parent);
		state = parent.getMainState();
		
		//watch state object for UI mode change
		state.addObserver(this);
	}
	
	public void enable()
	{
		compass.start();
		bEnabled = true;
	}
	
	public void disable()
	{
		compass.stop();
		bEnabled = false;
		fAzimuth = 0;
	}
	
	public void updateState(Screen screen, Canvas cs, Rect rtBounds)
	{
		bIsLocation = false;
		matrix = cs.getMatrix();
		
		if (state.getCurrentUiMode() == Command.CMD_MODE_COMPASS)
		{		
			//get location
			final Location location = ActivityMain.getLastLocation();
		  if (location != null)
		  {
		  	bIsLocation = true;
		  	
		  	//update declination once at start for aproximity location
		  	compass.updateTrueNorthCorrection(location);
		  	
		  	//get azimuth
				fAzimuth = compass.getAzimuth();			
				
		  	geoPoint.wgsLon = location.getLongitude();
		  	geoPoint.wgsLat = location.getLatitude();
				
				Mercator.ConvertToFlat(geoPoint);
		  	
				screen.toScreenPoint(geoPoint, screenPoint);
	
				fCenterX = screenPoint.x; 
				fCenterY = screenPoint.y;
				
			  //apply compass rotation transformation with negate azimuth for screen view
				matrix.postRotate(-fAzimuth, fCenterX, fCenterY);
		  } else {	  	
		  	fCenterX = rtBounds.centerX();
		  	fCenterY = rtBounds.centerY();
		  }
		}
	}
	
	public void set(Canvas cs)
	{
		if (bEnabled && bIsLocation)
		{
			cs.save();
			bCanvasSaved = true;

		  //apply compass rotation transformation with negate azimuth for screen view
			cs.rotate(-fAzimuth, fCenterX, fCenterY);
		}
	}

	public void clear(Canvas cs)
	{
		if (bCanvasSaved)
		{
			bCanvasSaved = false;
			cs.restore();
		}
	}
 	
	public void transformPoint(ScreenPoint point)
	{		
		if (bEnabled && bIsLocation)
		{
			points[0] = point.x;
			points[1] = point.y;
			
			matrix.mapPoints(points);
			
			point.x = (int)points[0]; 
			point.y = (int)points[1]; 		
		}
	}

	public void update(Observable observable, Object data)
	{
		//watch for UI state change
		if (observable == state)
		{
			if (data == MainState.UI_MODE_UPDATE)
			{
				disable();
				
				final int iCurrentUiMode = state.getCurrentUiMode();
				
				if (iCurrentUiMode == Command.CMD_MODE_COMPASS)
				{
					enable();					
				}								
			}
		}
	}
	
	public static int rotationCenterX()
	{
		return (int)self.fCenterX;
	}
	
	public static int rotationCenterY()
	{
		return (int)self.fCenterY;
	}

	public static float getAzimuth()
	{
		return self.compass.getAzimuth();
	}	
	
}
