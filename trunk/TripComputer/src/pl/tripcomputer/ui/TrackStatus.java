package pl.tripcomputer.ui;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.MainState;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;


public class TrackStatus extends UIElement implements Observer
{
	//fields
	private String sStatus[] = new String[3];

	private Rect rtStatus[] =
	{
			new Rect(0, 0, 0, 0),
			new Rect(0, 0, 0, 0),
			new Rect(0, 0, 0, 0),			
	};
	
  //fields
	private Paint mIcon = new Paint();
	private Paint mText = new Paint();
	
	private Typeface tfNormal = Typeface.create((String)null, Typeface.NORMAL);
	
	private int iIconSize = 0;
	private RectF rtIcon = new RectF(0, 0, 0, 0);
	private Rect rtText = new Rect(0, 0, 0, 0);
	
	private int iWidth = 0;
	private int iHeight = 0;
	
	private Boolean bStatusMutex = false;
	private Integer iStatus = MainState.TRACK_NOT_SELECTED;
	
	//fields
	private MainState state = null;
	
	
  //methods
	public TrackStatus(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
		this.state = parent.getMainState();
		
		this.bAlwaysVisible = true;		
		
		mIcon.setAntiAlias(true);
		mIcon.setStrokeWidth(1.0f);
		mIcon.setColor(style.iBackgroundColor);
		
		mText.setAntiAlias(true);
		mText.setColor(style.iTextColor1);
		mText.setTextSize(style.fTextSize1);
		mText.setTypeface(tfNormal);

		//get strings
		sStatus[MainState.TRACK_NOT_SELECTED] = parent.getString(R.string.track_status_not_selected);
		sStatus[MainState.TRACK_PAUSED] = parent.getString(R.string.track_status_paused);
		sStatus[MainState.TRACK_RECORDING] = parent.getString(R.string.track_status_recording);
		
		for (int i = 0; i < sStatus.length; i++)
		{
			mText.getTextBounds(sStatus[i], 0, sStatus[i].length(), rtStatus[i]);
			
			Paint.FontMetricsInt fm = mText.getFontMetricsInt();
			rtStatus[i].top = 0;
			rtStatus[i].bottom = Math.abs(fm.ascent);
		}		
		
		Paint.FontMetricsInt fm = mText.getFontMetricsInt();
		iHeight = Math.abs(fm.ascent) + fm.descent;
		
		iIconSize = iHeight;

		rtIcon.right = iIconSize;
		rtIcon.bottom = iIconSize;
		
		iWidth = getWidth(MainState.TRACK_NOT_SELECTED);
		
		//watch state object for UI mode change
		state.addObserver(this);		
	}

	public int getWidth(int iStatus)
	{
		final Rect rt = rtStatus[iStatus];
		return (int)(rtIcon.width() + (int)style.fBackgroundMargin + (int)rt.width()); // + (int)style.fBackgroundMargin);
	}
	
	public void updateStyle()
	{
		style.iBackgroundColor = 0xffff4444;
		style.iTextColor1 = 0xff444444;
		style.fTextSize1 = 14;
		style.fBackgroundMargin = 4;
	}
	
	public void onClick()
	{
	}

	public void surfaceSizeChanged(int width, int height, int screenOrientation)
	{		
		final int iLeft = (int)(width - style.iButtonMargin);
		final int iTop = iScreenHeight - (int)(iHeight >> 1) - (int)style.iButtonHeight;
		
		setSize(iWidth, iHeight);
		setPos(iLeft, iTop);
	}
	
	public void doDraw(Canvas cs)
	{
		super.doDraw(cs);

		int iCurrentStatus = MainState.TRACK_NOT_SELECTED;
		synchronized (bStatusMutex)
		{
			iCurrentStatus = iStatus;
		}
		
		if (iCurrentStatus == MainState.TRACK_NOT_SELECTED)
			return;
		
		String sText = sStatus[iCurrentStatus];
		
		rtText.set(rtStatus[iCurrentStatus]);
		
		iWidth = getWidth(iCurrentStatus);
		
		rtIcon.left = rtBounds.left - iWidth;
		rtIcon.right = rtIcon.left + iIconSize;		
		rtIcon.top = rtBounds.top; 			
		rtIcon.bottom = rtIcon.top + iIconSize; 			
		
		rtText.left = (int)rtIcon.right + (int)style.fBackgroundMargin;
		rtText.right = rtText.left + rtStatus[iCurrentStatus].width();
		rtText.top = rtBounds.top;
		rtText.bottom = rtText.top + rtStatus[iCurrentStatus].height();

		//draw icon
		if (iCurrentStatus == MainState.TRACK_NOT_SELECTED)
		{
			mIcon.setColor(0xffaaaaaa);
			cs.drawRect(rtIcon, mIcon);
		}
		if (iCurrentStatus == MainState.TRACK_PAUSED)
		{
			mIcon.setColor(0xff00aa00);
			cs.drawRect(rtIcon, mIcon);
		}
		if (iCurrentStatus == MainState.TRACK_RECORDING)
		{
			mIcon.setColor(0xffff5555);
			cs.drawOval(rtIcon, mIcon);
		}
				
		final int iTextPosY = (int) (rtText.top + rtText.height());
		
		//draw text
		cs.drawText(sText, rtText.left, iTextPosY, mText);
	}
	
	public void update(Observable observable, Object data)
	{
		if (observable == state)
		{
			if (data == MainState.UI_MODE_UPDATE)
			{
				synchronized (bStatusMutex)
				{
					iStatus = state.getTrackStatus();
				}
			}
		}		
	}

}
