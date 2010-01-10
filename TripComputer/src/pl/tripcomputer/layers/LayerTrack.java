package pl.tripcomputer.layers;

import android.graphics.*;
import pl.tripcomputer.MainDraw;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.loader.TrackItem;
import pl.tripcomputer.map.*;


public class LayerTrack extends Layer
{
  //fields
	private final static int iPointRadius = 5;
	
  //fields
  private Paint mPaintTrack = new Paint();
  private Paint mPaintPoint = new Paint();  
	private Paint mPointLabel = new Paint();
	
	private CornerPathEffect pathEffectCorner = new CornerPathEffect(6);
	
	private Typeface tfBold = Typeface.create((String)null, Typeface.BOLD);
	
  private Path mPath = new Path();
  
  private ScreenPoint screenPoint = new ScreenPoint();
  private ScreenPoint screenPointStart = new ScreenPoint();
  private ScreenPoint screenPointFinish = new ScreenPoint();
  
  private RectF rtPoint = new RectF();
	private Rect rtLabelStart = new Rect(0, 0, 0, 0);
	private Rect rtLabelFinish = new Rect(0, 0, 0, 0);
  
  private Boolean bResetViewPort = false;
  private Boolean bUpdateDrawPath = false;
  
  private String sLabelStart = null;
  private String sLabelFinish = null;
  private String sLabelLastPosition = null;

	private GeoPoint pointStart = null;
	private GeoPoint pointFinish = null;
  
  //fields
  private TrackItem trackItem = null;  
  

  //methods
	public LayerTrack(CommonActivity parent, Screen screen, TrackItem trackItem)
	{
		super(parent, screen);
		
		this.trackItem = trackItem;
		
		//get strings
		sLabelStart = parent.getString(R.string.label_track_point_start);
		sLabelFinish = parent.getString(R.string.label_track_point_finish);
		sLabelLastPosition = parent.getString(R.string.label_track_point_last_position);
		
  	//paint track
		mPaintTrack.setAntiAlias(true);
		mPaintTrack.setARGB(255, 0, 0, 0);
		mPaintTrack.setStrokeWidth(4);
		mPaintTrack.setStyle(Paint.Style.STROKE);
		mPaintTrack.setStrokeCap(Paint.Cap.ROUND);
		mPaintTrack.setStrokeJoin(Paint.Join.ROUND);
		
  	//paint point
		mPaintPoint.setAntiAlias(true);
		mPaintPoint.setColor(0xff000000);
		mPaintPoint.setStrokeWidth(1);
		mPaintPoint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintPoint.setStrokeCap(Paint.Cap.ROUND);
		mPaintPoint.setStrokeJoin(Paint.Join.ROUND);
		
  	//paint label		
		mPointLabel.setAntiAlias(true);
		mPointLabel.setTextSize(12);
		
		//get text bounds
		mPointLabel.getTextBounds(sLabelStart, 0, sLabelStart.length(), rtLabelStart);
		mPointLabel.getTextBounds(sLabelFinish, 0, sLabelFinish.length(), rtLabelFinish);
	}

	public void initData()
	{
	}
	
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
	}
	
	public void setUpdateDrawPath()
	{
		synchronized(bUpdateDrawPath)
		{
			bUpdateDrawPath = true;
		}
	}

	public void setResetViewPort()
	{
		synchronized(bResetViewPort)
		{
			if (parent.getMainState().allowObjectResetView())
			{
				bResetViewPort = true;
			}
		}
	}

	private void resetViewPort()
	{
		if (trackItem.isEmpty())
			return;

		synchronized(trackItem.trackPoints)
		{
			if (trackItem.isSelected())
			{
				screen.resetViewPort(trackItem.trackPoints, Screen.RESET_VIEWPORT_MODE_TRACK);
			}
		}
	}
	
	private void updateDrawPath()
	{
		if (trackItem.isEmpty())
			return;
		
		synchronized(trackItem.trackPoints)
		{		
			//process path
			synchronized(mPath)
			{
				mPath.reset();
				for (int i = 0; i < trackItem.trackPoints.size(); i++)
				{
					screen.toScreenPoint(trackItem.trackPoints.get(i), screenPoint);														
					if (i == 0)
					{
						mPath.moveTo(screenPoint.x, screenPoint.y);
					} else {
						mPath.lineTo(screenPoint.x, screenPoint.y);
					}
				}
			}
			
			//get start and finish points			
			pointStart = null;
			pointFinish = null;
			
			pointStart = trackItem.trackPoints.get(0);
						
			final int iPointsCount = trackItem.trackPoints.size();			
			if (iPointsCount > 1)
			{				
				pointFinish = trackItem.trackPoints.get(iPointsCount - 1);
			}
		}
	}
	
	public void doDraw(Canvas cs, Rect rtBounds)
	{	
		synchronized(bResetViewPort)
		{
			if (bResetViewPort)
			{
				bResetViewPort = false;
				resetViewPort();
			}
		}
		
		synchronized(bUpdateDrawPath)
		{
			if (bUpdateDrawPath)
			{
				bUpdateDrawPath = false;
				updateDrawPath();
			}
		}

		//set track color
		if (trackItem.isSelected())
		{
			if (trackItem.isRecording())
			{
				mPaintTrack.setColor(0xffff0000);		
			} else {
				mPaintTrack.setColor(0xff888888);				
			}
		} else {
			if (trackItem.isRecording())
			{
				mPaintTrack.setColor(0xffff8888);
			} else {
				mPaintTrack.setColor(0xffcccccc);
			}
		}

		//draw track
		synchronized(mPath)
		{
			mPaintTrack.setPathEffect(pathEffectCorner);
			
			MainDraw.compassRotation.set(cs);
			
			cs.drawPath(mPath, mPaintTrack);
			
			MainDraw.compassRotation.clear(cs);
			
			mPaintTrack.setPathEffect(null);
		}

		//draw start and finish points
		drawLimitPoints(cs);
	}
	
	private void drawLimitPoints(Canvas cs)
	{
		if (trackItem.isEmpty())
			return;
		
		mPointLabel.setTypeface(tfBold);
		
		synchronized(trackItem.trackPoints)
		{
			//draw start point
			if (pointStart != null)
			{
				screen.toScreenPoint(pointStart, screenPointStart);
				drawPoint(cs, screenPointStart, true, sLabelStart, rtLabelStart);				
			}
						
			//draw finish point
			if (pointFinish != null)
			{
				screen.toScreenPoint(pointFinish, screenPointFinish);
				
				if (trackItem.isClosed())
					drawPoint(cs, screenPointFinish, false, sLabelFinish, rtLabelFinish);
				else
					drawPoint(cs, screenPointFinish, false, sLabelLastPosition, rtLabelFinish);				
			}			
		}
	}
	
	private void drawPoint(Canvas cs, ScreenPoint coords, boolean bStartPoint, String sLabel, Rect rtLabel)
	{		
		//transform point with compass rotation
		MainDraw.compassRotation.transformPoint(coords);

		//draw border
		rtPoint.set(coords.x - iPointRadius, coords.y - iPointRadius, coords.x + iPointRadius, coords.y + iPointRadius);

		mPaintPoint.setColor(0xff000000);
		cs.drawOval(rtPoint, mPaintPoint);

		//fill inner
		rtPoint.inset(2, 2);
		
		mPaintPoint.setColor(0xffcccccc);
		cs.drawOval(rtPoint, mPaintPoint);

		//draw point label
		if (bStartPoint)
		{
			mPointLabel.setColor(0xff442200);
		} else {
			if (trackItem.isRecording())
			{
				mPointLabel.setColor(0xffff4444);				
			} else {
				mPointLabel.setColor(0xffff8800);
			}
		}
		
		cs.drawText(sLabel, coords.x + 8, coords.y + rtLabel.height() + 3, mPointLabel);
	}
		
	public void updateObjectsState()
	{
	}
  
}
