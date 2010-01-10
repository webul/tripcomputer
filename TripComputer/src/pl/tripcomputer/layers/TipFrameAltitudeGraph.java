package pl.tripcomputer.layers;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.Command;
import pl.tripcomputer.MainState;
import pl.tripcomputer.Preferences;
import pl.tripcomputer.Utils;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.loader.TrackItem;
import pl.tripcomputer.map.GeoPoint;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;


public class TipFrameAltitudeGraph extends TipFrame implements Observer
{
	//fields
	private final static int GRAPH_HEIGHT = 64;
		
	//fields
  private Paint mText = new Paint();
  private Paint mGraphBkg = new Paint();
  private Paint mGraphPen = new Paint();
  
	private Rect rtText = new Rect(0, 0, 0, 0);
	private Rect rtGraph = new Rect(0, 0, 0, 0);
	private RectF rtBar = new RectF(0, 0, 0, 0);
	private RectF rtDrawingBar = new RectF(0, 0, 0, 0);
	
	private ArrayList<Integer> vecData = new ArrayList<Integer>();
	private int iAltMaxValue = 0;
	private int iAltMinValue = 0;
	
	private LinearGradient bkgLinearGradient = null;	
	

	//methods
	public TipFrameAltitudeGraph(Preferences prefs, MainState state, String sTitle)
	{
		super(prefs, state, sTitle);
		
  	//text
		mText.setAntiAlias(true);
		mText.setColor(0xff442200);
		mText.setStyle(Paint.Style.FILL);
		mText.setTextSize(14);	
		mText.setTextAlign(Paint.Align.CENTER);
		
		//graph bkg
		mGraphBkg.setAntiAlias(false);
		mGraphBkg.setColor(0xffe0e044);
		mGraphBkg.setStyle(Paint.Style.FILL);
		
		//graph pen
		mGraphPen.setAntiAlias(true);
		mGraphPen.setColor(0xff884400);
		mGraphPen.setStyle(Paint.Style.FILL);
		
		//watch DataStats object for data change
		ActivityMain.loader.stats.addObserver(this);		
	}

  protected int getHeight()
  {
  	int value = super.getHeight();
  	
  	mText.getTextBounds(sTestText, 0, sTestText.length(), rtText);
  	
		value += rtText.height() + LINE_MARGIN;
		value += PADDING;
  	
  	value += GRAPH_HEIGHT;
  	
   	value += PADDING;
  	
  	return value;
  }

  private void getData()
  {
  	iAltMaxValue = 0;
  	iAltMinValue = 0;
  	vecData.clear();
  	
		final TrackItem track = ActivityMain.loader.getSelectedTrack();
		if (track != null)
		{
			if (!track.isEmpty())
			{
				final GeoPoint startPoint = track.trackPoints.get(0);
		  	iAltMaxValue = startPoint.iAltitude;
		  	iAltMinValue = startPoint.iAltitude;
				
				synchronized(track.trackPoints)
				{						
					for (int i = 0; i < track.trackPoints.size(); i++)
					{						
						final GeoPoint point = track.trackPoints.get(i);
						
						vecData.add(point.iAltitude);
						
						if (point.iAltitude > iAltMaxValue)
							iAltMaxValue = point.iAltitude;
						if (point.iAltitude < iAltMinValue)
							iAltMinValue = point.iAltitude;
					}										
				}
			}
		}
  }
  
	public void doDraw(Canvas cs, Rect rtBounds)
	{
		super.doDraw(cs, rtBounds);
		
		if (vecData.isEmpty())
			return;
		
		final float fAltRange = Math.abs(iAltMaxValue - iAltMinValue);

		if (fAltRange == 0)
			return;
		
		final String sAltMinValue = Utils.getDistanceAsStringPrecise(prefs, iAltMinValue);
		final String sAltMaxValue = Utils.getDistanceAsStringPrecise(prefs, iAltMaxValue);
		final String sAlt = sAltMinValue + " - " + sAltMaxValue;
		
		//draw text
		final int iTextPosY = getTitleBottom() + rtText.height();		
		cs.drawText(sAlt, rtShape.centerX(), iTextPosY, mText);
						
		//set graph rect
		rtGraph.set((int)rtShape.left, (int)rtShape.top, (int)rtShape.right, (int)rtShape.bottom);
		
		rtGraph.top += PADDING + rtTitle.height() + LINE_TITLE_MARGIN;
				
		//room for text
		rtGraph.top += rtText.height() + LINE_MARGIN;
		rtGraph.top += PADDING;
		
		rtGraph.left += PADDING;
		rtGraph.right -= PADDING;
		rtGraph.bottom -= PADDING;
		
		//draw background
		if (bkgLinearGradient == null)
		{
			bkgLinearGradient = new LinearGradient(rtGraph.left, rtGraph.top, rtGraph.left, rtGraph.bottom,
					0xffffff88, 0xffc8c844, Shader.TileMode.CLAMP);
		}
			
		mGraphBkg.setShader(bkgLinearGradient);
		cs.drawRect(rtGraph, mGraphBkg);
		mGraphBkg.setShader(null);
		
		//draw graph
		final float fStepWidth = (float)rtGraph.width() / (float)vecData.size(); 								
		final float fStepHeight = (float)rtGraph.height() / fAltRange;

		rtBar.set(rtGraph);
		rtBar.right = rtBar.left + fStepWidth;
		
		for (int i = 0; i < vecData.size(); i++)
		{						
			final int iAlt = (vecData.get(i) - iAltMinValue);
			
			rtBar.top = Math.round(rtBar.bottom - (iAlt * fStepHeight));
			
			//shift graph bars up to zero values be barely visible			
			rtBar.top = (int)(rtBar.top - 1);

			//check bounds
			if (rtBar.top < rtGraph.top)
				rtBar.top = rtGraph.top;

			if (rtBar.left > rtGraph.right)
				rtBar.left = rtGraph.right;
			
			if (rtBar.right > rtGraph.right)
				rtBar.right = rtGraph.right;			
			
			//draw bar
			rtDrawingBar.set(rtBar);
			rtDrawingBar.left = Math.round(rtBar.left);
			rtDrawingBar.right = Math.round(rtBar.right);
			
			cs.drawRect(rtDrawingBar, mGraphPen);
			
			//increase step
			rtBar.left += fStepWidth;
			rtBar.right += fStepWidth;			
		}				
	}

	public void update(Observable observable, Object data)
	{
		if (state.getCurrentUiMode() == Command.CMD_MODE_INFO)
		{				
			if (observable == ActivityMain.loader.stats)
			{
				if (data.equals(ActivityMain.loader.stats.bUpdatedLocation))
				{
					getData();
				}
				if (data.equals(ActivityMain.loader.stats.bUpdatedMode))
				{
					getData();
				}
			}
		}
	}
	
}
