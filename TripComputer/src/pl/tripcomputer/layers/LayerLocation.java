package pl.tripcomputer.layers;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;


public class LayerLocation extends Layer implements Observer
{
  //fields
	private final static int iRadius = 8;
	private final static int iBorderRadius = 12;
	
  //fields
	private static LayerLocation self = null;
  private Paint mPaintLocation = new Paint();  
  private Paint mPaintLocationBorder = new Paint();  
  private RectF rtPoint = new RectF();
  
  
  //methods
	public LayerLocation(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
		self = this;
		
  	//paint location
		mPaintLocation.setAntiAlias(true);
		mPaintLocation.setColor(0xff6090a0);
		mPaintLocation.setStrokeWidth(1);
		mPaintLocation.setStyle(Paint.Style.FILL_AND_STROKE);

		mPaintLocationBorder.setAntiAlias(true);
		mPaintLocationBorder.setColor(0xff104050);
		mPaintLocationBorder.setStrokeWidth(1);
		mPaintLocationBorder.setStyle(Paint.Style.STROKE);
		
		//observe location status
		super.startObserveLocation(this);
	}	

	public void update(Observable observable, Object data)
	{
		super.updateLocation(observable, data);
	}
	
  public void updateLocation()
  {
		//reset to this object if currently reset location mode and zoom to location selected
		if (getScreen().getCurrentResetViewPortMode() == Screen.RESET_VIEWPORT_MODE_LOCATION)
		{
			if (ActivityMain.loader.isSelectedLocation())
				resetViewPort();
		}  	
  }

	public void initData()
	{
	}

	public void surfaceSizeChanged(int width, int height, int screenOrientation)
	{
	}

	public static void resetViewPort()
	{
		synchronized(self.locationGeoPoint)
		{
			if (self.isLocationEnabled())
			{
				self.screen.resetViewPort(self.locationGeoPoint, Screen.RESET_VIEWPORT_MODE_LOCATION);
			}
		}
	}
	
	public void doDraw(Canvas cs, Rect rtBounds)
	{
		if (super.isLocationEnabled())
		{
			updateLocationScreenPoint();
			
			final boolean bSelected = ActivityMain.loader.isSelectedLocation();

			//draw location point
			rtPoint.set(locationScreenPoint.x - iRadius, locationScreenPoint.y - iRadius, locationScreenPoint.x + iRadius, locationScreenPoint.y + iRadius);

			mPaintLocation.setColor(0xff205060);
			cs.drawOval(rtPoint, mPaintLocation);

			rtPoint.inset(3, 3);
			
			mPaintLocation.setColor(0xff80b0c0);
			cs.drawOval(rtPoint, mPaintLocation);
			
			//show selection
			mPaintLocationBorder.setStrokeWidth(bSelected ? 3 : 1);
			
			final int iNewBorderRadius = bSelected ? iBorderRadius + 1 : iBorderRadius; 
			
			//draw border
			rtPoint.set(locationScreenPoint.x - iNewBorderRadius, locationScreenPoint.y - iNewBorderRadius, locationScreenPoint.x + iNewBorderRadius, locationScreenPoint.y + iNewBorderRadius);			
			cs.drawOval(rtPoint, mPaintLocationBorder);
		}
	}
	
	public void updateObjectsState()
	{

	}

}
