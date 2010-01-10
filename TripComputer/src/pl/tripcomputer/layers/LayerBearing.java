package pl.tripcomputer.layers;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.Command;
import pl.tripcomputer.MainDraw;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.loader.TrackItem;
import pl.tripcomputer.loader.WaypointItem;
import pl.tripcomputer.map.GeoPoint;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.map.ScreenPoint;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;


public class LayerBearing extends Layer implements Observer
{
  //fields
  private Paint mPoint = new Paint();
  private Paint mLine = new Paint();
  private ScreenPoint targetScreenPoint = new ScreenPoint();	

  private float[] mDashIntervals = { 4, 4 };
	private DashPathEffect pathEffectDash = new DashPathEffect(mDashIntervals, 0);
	
  //selected objects
	protected TrackItem trackItem = null;
	protected WaypointItem waypointItem = null;	
	
  
  //methods
	public LayerBearing(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
  	//paint line
		mLine.setAntiAlias(true);
		mLine.setColor(0xff000000);
		mLine.setStrokeWidth(1);	
		mLine.setStyle(Paint.Style.FILL);
		
  	//paint point
		mPoint.setAntiAlias(true);
		mPoint.setColor(0xff000000);
		mPoint.setStrokeWidth(1);	
		mPoint.setStyle(Paint.Style.FILL);
		
		//observe location status
		super.startObserveLocation(this);		
	}

	public void update(Observable observable, Object data)
	{
		super.updateLocation(observable, data);		
	}
	
  public void updateLocation()
  {
  }
	
	public void initData()
	{
	}

	public void surfaceSizeChanged(int width, int height, int screenOrientation)
	{
	}
	
	public void updateObjectsState()
	{
	}
	
	public void doDraw(Canvas cs, Rect rtBounds)
	{
		if (super.isLocationEnabled())
		{
			updateLocationScreenPoint();
			
			final int iUiMode = state.getCurrentUiMode();
			
			//bearing to selected track
			if (iUiMode == Command.CMD_MODE_LOC_TO_SEL_TRACK)
			{
				updateSelectedTrack();
				
				if (trackItem != null)
				{
					final GeoPoint targetGeoPoint = ActivityMain.loader.stats.getTrackNearestPoint();					
					if (targetGeoPoint != null)
					{
						screen.toScreenPoint(targetGeoPoint, targetScreenPoint);
					
						//draw bearing line
						mLine.setPathEffect(pathEffectDash);
						cs.drawLine((int)locationScreenPoint.x, (int)locationScreenPoint.y, (int)targetScreenPoint.x, (int)targetScreenPoint.y, mLine);				
						mLine.setPathEffect(null);
						
						//draw target point
						mPoint.setColor(0xff000000);
						cs.drawCircle(targetScreenPoint.x, targetScreenPoint.y, 5, mPoint);
						mPoint.setColor(0xffcccccc);
						cs.drawCircle(targetScreenPoint.x, targetScreenPoint.y, 3, mPoint);
					}
				}
			}
			
			//bearing to selected waypoint
			if ((iUiMode == Command.CMD_MODE_COMPASS) || (iUiMode == Command.CMD_MODE_LOC_TO_SEL_WPT))
			{
				updateSelectedWaypoint();
				
				if (waypointItem != null)
				{
					final GeoPoint targetGeoPoint = waypointItem.getPosition();
				
					screen.toScreenPoint(targetGeoPoint, targetScreenPoint);

					//draw bearing line
					mLine.setPathEffect(pathEffectDash);
					
					if (iUiMode == Command.CMD_MODE_COMPASS)
						MainDraw.compassRotation.set(cs);
					
					cs.drawLine((int)locationScreenPoint.x, (int)locationScreenPoint.y, (int)targetScreenPoint.x, (int)targetScreenPoint.y, mLine);
					
					if (iUiMode == Command.CMD_MODE_COMPASS)
						MainDraw.compassRotation.clear(cs);
					
					mLine.setPathEffect(null);
				}
			}
		
		}
	}
	
	private void updateSelectedTrack()
	{		
		final long lTrackId = state.getSelectedTrackId();
	
		trackItem = null;	
		if (lTrackId != -1)
			trackItem = ActivityMain.loader.getSelectedTrack();
	}

	private void updateSelectedWaypoint()
	{		
		final long lWaypointId = state.getSelectedWaypointId();
		
		waypointItem = null;
		if (lWaypointId != -1)
			waypointItem = ActivityMain.loader.getSelectedWaypoint();		
	}
	
}
