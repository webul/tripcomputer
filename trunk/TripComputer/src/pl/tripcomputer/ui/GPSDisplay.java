package pl.tripcomputer.ui;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.Preferences;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.gps.GpsReader;
import pl.tripcomputer.map.Screen;
import android.graphics.*;
import android.view.MotionEvent;


public class GPSDisplay extends UIElement implements Observer
{
  //fields
	private Paint mTextGPS = new Paint();
	private Paint mBarFrame = new Paint();
	private Paint mBarProgress = new Paint();
	private Paint mBackground = new Paint();	
	
	private Typeface tfBold = Typeface.create((String)null, Typeface.BOLD);

	private Rect rtTextGPS = new Rect(0, 0, 0, 0);
	private Rect rtBarFrame = new Rect(0, 0, 0, 0);	
	private Rect rtBarProgress = new Rect(0, 0, 0, 0);	
	
	private static final String sGPS = "GPS";
	private static final int fBarWidthFactor = 4;
	
	private float iWidth = 0;
	private float iHeight = 0;
	private static int iTopMargin = 2;
	private static int iStaticDisplayBottom = 0;
	
	//location precision value: 5-200 meters 
	private float fPrecision = -1;	
	
	private Preferences prefs = null;

	
  //methods
	public GPSDisplay(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
		prefs = parent.getPrefs();
						
		//paint
		mTextGPS.setAntiAlias(true);
		mTextGPS.setColor(style.iTextColor1);
		mTextGPS.setTextSize(style.fTextSize1);		
		mTextGPS.setTypeface(tfBold);
		mTextGPS.setTextAlign(Paint.Align.LEFT);		
				
		mBarFrame.setColor(style.iBarFrame);
		mBarFrame.setAlpha(128);
		
		mBarProgress.setColor(style.iBarProgress);
		
		mBackground.setAntiAlias(true);
		mBackground.setColor(style.iBackgroundColor);
		
		//get text bounds
		mTextGPS.getTextBounds(sGPS, 0, sGPS.length(), rtTextGPS);

		//init sizes
		rtBarFrame.set(0, 0, (int)((float)rtTextGPS.height() * (float)fBarWidthFactor), rtTextGPS.height() + 2);
		rtBarProgress.set(rtBarFrame);
		
		iWidth = rtTextGPS.width() + style.fHorSpace + rtBarFrame.width();		
		iHeight = rtTextGPS.height() + style.fBackgroundMargin;
		iStaticDisplayBottom = (int)iHeight + (int)style.fBackgroundMargin;

		setSize(iWidth, iHeight);
		
		this.bAlwaysVisible = true;
				
		//observe location status
		ActivityMain.loader.gpsLocationStatus().addObserver(this);
	}

	public void update(Observable observable, Object data)
	{
		if (observable instanceof GpsLocationStatus)
		{
			if (data == GpsLocationStatus.STATE_NEW_GPS_LOCATION)
			{
				setValue(ActivityMain.loader.gpsLocationStatus().getAccuracy());
			}
		}				
	}
	
	public void updateStyle()
	{
		style.iBackgroundColor = 0xff407080;
		style.iTextColor1 = 0xffffffff;
		style.iTextColor2 = 0xffffffff;
		
		style.fHorSpace = 4.0f;
		
		style.fBackgroundRound = 4.0f;
		style.fBackgroundMargin = 4.0f;
				
		style.rtBackgroundMarginCorrect.right = (int)style.fBackgroundRound;
				
		style.fTextSize1 = 14;
		style.fTextSize2 = 14;
		
		style.rtBackgroundRoundMargin.left = (int)style.fBackgroundRound;
		style.rtBackgroundRoundMargin.top = (int)style.fBackgroundRound;		
	}
	
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		//set position
		final int iPosX = width - (int)iWidth - (int)style.fBackgroundMargin;
		final int iPosY = DigitDisplayTime.getStaticDisplayBottom() + DigitDisplayTime.iDisplayBottomMargin;				

		setPos(iPosX, iPosY);
	}

	public static int getStaticDisplayBottom()
	{
		return iStaticDisplayBottom;
	}
	
	public void updateObjectsState()
	{
		super.updateObjectsState();
		
	}
	
	public void doDraw(Canvas cs)
	{
		super.doDraw(cs);
		
		final boolean bIsFix = (fPrecision != -1);
		final boolean bIsPrecision = (fPrecision < prefs.iLocationAccuracy);
		
		this.drawSimpleShadow(cs, 6);
		
		//draw background
		if (bIsFix)
		{
			mBackground.setColor(style.iBackgroundColor);			
			if (bIsPrecision)
				mBackground.setColor(0xff338833);			
		} else {
			mBackground.setColor(style.iFrameNoFix);
		}
		cs.drawRoundRect(rtBackground, style.fBackgroundRound, style.fBackgroundRound, mBackground);

		final float fTextGPSTop = rtBounds.top + rtTextGPS.height() + iTopMargin;
		rtBarFrame.offsetTo((int)(rtBounds.left + rtTextGPS.width() + style.fHorSpace), rtBounds.top + iTopMargin - 1);
		
		//draw GPS symbol
		cs.drawText(sGPS, rtBounds.left, fTextGPSTop, mTextGPS);
		
		//draw progress bar frame
		if (bIsFix)
		{
			mBarFrame.setColor(style.iBarFrame);
			if (bIsPrecision)
				mBarFrame.setColor(0xff004400);
		} else {
			mBarFrame.setColor(style.iBarNoFixFrame);
		}
		cs.drawRect(rtBarFrame, mBarFrame);
		
		if (bIsFix)
		{			
			//set progress bar width
			rtBarProgress.set(rtBarFrame);
			rtBarProgress.inset(2, 2);
			
			final int iProgressMax = rtBarProgress.right - rtBarProgress.left; 					
			final float fStep = ((float)rtBarProgress.width() / (GpsReader.MINIMUM_ACCURACY - GpsReader.MAXMIMUM_ACCURACY));
			
			int iProgressWidth = iProgressMax - (int)((fPrecision * fStep) - (GpsReader.MAXMIMUM_ACCURACY * fStep));			
			if (iProgressWidth > iProgressMax)
				iProgressWidth = iProgressMax;
			if (iProgressWidth < 0)
				iProgressWidth = 0;
			
			//draw progress bar			
			mBarProgress.setColor(style.iBarProgress);
			if (bIsPrecision)
				mBarProgress.setColor(0xff77cc00);
			
			rtBarProgress.right = rtBarProgress.left + iProgressWidth;			
			cs.drawRect(rtBarProgress, mBarProgress);
		}
	}
	
	public void setValue(float fPrecision)
	{
		this.fPrecision = fPrecision;
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		//do not want touch events, onClick(), etc..
		return false;
	}
	
	public void onClick()
	{
	}
	
}
