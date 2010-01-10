package pl.tripcomputer.layers;

import pl.tripcomputer.MainDraw;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.loader.WaypointItem;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.map.ScreenPoint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;


public class LayerWaypoint extends Layer
{
  //statics
	private static Typeface tfName = Typeface.create((String)null, Typeface.BOLD);
	private static Typeface tfType = Typeface.create((String)null, Typeface.ITALIC);
	
  private static Drawable icon = null;
  private static Drawable iconSelected = null;
  private static int iIconSpotX = 0;
  private static int iIconSpotY = 0;

	private static int iLabelNameHeight = 0;
	private static int iLabelNameBaseLine = 0;
  
  private static Paint mPaintPoint = new Paint();
	private static Paint mPaintName = new Paint();
	private static Paint mPaintType = new Paint();
  
  static
  {
    //paint point
		mPaintPoint.setAntiAlias(true);
		mPaintPoint.setColor(0xff000000);
		mPaintPoint.setStrokeWidth(1);
		mPaintPoint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintPoint.setStrokeCap(Paint.Cap.ROUND);
		mPaintPoint.setStrokeJoin(Paint.Join.ROUND);
		
		//paint label
		mPaintName.setAntiAlias(true);
		mPaintName.setTextSize(12);
		mPaintName.setColor(0xff004400);
		mPaintName.setTypeface(tfName);
		
		//paint type
		mPaintType.setAntiAlias(true);
		mPaintType.setTextSize(12);
		mPaintType.setColor(0xff228822);
		mPaintType.setTypeface(tfType);
		
		//get text height
		Paint.FontMetricsInt fm = null;
		fm = mPaintName.getFontMetricsInt();
		iLabelNameHeight = Math.abs(fm.ascent) + fm.descent;		
		iLabelNameBaseLine = Math.abs(fm.bottom);		
  }

  //fields
  private ScreenPoint screenPoint = new ScreenPoint();
    
  private String sLabelName = null;
  private String sLabelType = null;

  private Boolean bResetViewPort = false;
  
  //fields
  private WaypointItem waypointItem = null;
  private boolean bIsTypeSet = false;


  //methods
  public LayerWaypoint(CommonActivity parent, Screen screen, WaypointItem waypointItem)
	{
		super(parent, screen);
		
		this.waypointItem = waypointItem;		
	}
	
	public void initData()
	{
	}

	public void surfaceSizeChanged(int width, int height, int screenOrientation)
	{
	}

	public void init(DataItemWaypoint data)
	{
		synchronized(waypointItem)
		{
			sLabelName = waypointItem.dataWaypoint.getName();
			sLabelType = waypointItem.dataWaypoint.getTypeAsString(parent);
			bIsTypeSet = waypointItem.dataWaypoint.isTypeSet(); 			
		}

		//common icon
		if (icon == null)
		{
			icon = parent.getResources().getDrawable(R.drawable.map_icon_wpt);
			iconSelected = parent.getResources().getDrawable(R.drawable.map_icon_wpt_selected);
			
			iIconSpotX = (icon.getIntrinsicWidth() >> 1);
		  iIconSpotY = (icon.getIntrinsicHeight() - 4);
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
		if (waypointItem.isSelected())
		{
			synchronized(waypointItem.getPosition())
			{
				screen.resetViewPort(waypointItem.getPosition(), Screen.RESET_VIEWPORT_MODE_WAYPOINT);				
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

		final boolean bIsSelected = waypointItem.isSelected();
		
		synchronized(waypointItem.getPosition())
		{
			screen.toScreenPoint(waypointItem.getPosition(), screenPoint);			
		}
	
		//transform point with compass rotation
		MainDraw.compassRotation.transformPoint(screenPoint);
		
		//draw icon
		if (bIsTypeSet)
		{
			//draw waypoint type icon
			Drawable wptIcon = waypointItem.dataWaypoint.getTypeIcon();
			if (wptIcon != null)
			{		
				final int iWptIconLeft = screenPoint.x - (WaypointIcons.ICON_WIDTH >> 1);
				final int iWptIconTop = screenPoint.y - (WaypointIcons.ICON_HEIGHT >> 1);
				
				wptIcon.setBounds(iWptIconLeft, iWptIconTop, iWptIconLeft + WaypointIcons.ICON_WIDTH, iWptIconTop + WaypointIcons.ICON_HEIGHT);	
				wptIcon.draw(cs);
			}		
		} else {			
			//default icon
			final int iIconLeft = screenPoint.x - iIconSpotX;
			final int iIconTop = screenPoint.y - iIconSpotY;
			
			if (bIsSelected)
			{
				iconSelected.setBounds(iIconLeft, iIconTop, iIconLeft + icon.getIntrinsicWidth(), iIconTop + icon.getIntrinsicHeight());	
				iconSelected.draw(cs);			
			} else {
				icon.setBounds(iIconLeft, iIconTop, iIconLeft + icon.getIntrinsicWidth(), iIconTop + icon.getIntrinsicHeight());	
				icon.draw(cs);			
			}			
		}
		
		//draw center spot point
		//drawTestSpotPoint(cs);
				
		//draw name and type
		mPaintName.setUnderlineText(bIsSelected ? true : false);
	
		final int iLabelLeft = screenPoint.x + iIconSpotX;
		final int iLabelTop = screenPoint.y + iLabelNameBaseLine;
		
		if (bIsSelected)
		{
			mPaintName.setColor(0xff000000);
			mPaintType.setColor(0xff222222);
		} else {			
			mPaintName.setColor(0xff666666);
			mPaintType.setColor(0xff888888);
		}				

		cs.drawText(sLabelName, iLabelLeft, iLabelTop, mPaintName);
		
		if (bIsTypeSet)
		{
			cs.drawText(sLabelType, iLabelLeft, iLabelTop + iLabelNameHeight, mPaintType);
		}
	}

	public void updateObjectsState()
	{
	}

	public void drawTestSpotPoint(Canvas cs)
	{
		RectF rtPoint = new RectF();
		rtPoint.set(screenPoint.x - 2, screenPoint.y - 2, screenPoint.x + 2, screenPoint.y + 2);			
		mPaintPoint.setColor(0xff000000);
		cs.drawOval(rtPoint, mPaintPoint);
	}
	
}
