package pl.tripcomputer.ui;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.MainState;
import pl.tripcomputer.Preferences;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.*;


public abstract class DigitDisplay extends UIElement implements Observer
{
	//fields
	protected Preferences prefs = null;
	protected MainState state = null;
	
  //fields
	private Paint mTextValue = new Paint();
	private Paint mTextSuffix = new Paint();	
	private Paint mTextType = new Paint();
	private Paint mBackground = new Paint();
	private Paint mUnderline = new Paint();	

  //fields
	private Rect rtTextValue = new Rect(0, 0, 0, 0);
	private Rect rtTextSuffix = new Rect(0, 0, 0, 0);
	private Rect rtTextType = new Rect(0, 0, 0, 0);

  //fields
	private LinearGradient bkgLinearGradient = null;	
	private Typeface tfBold = Typeface.create((String)null, Typeface.BOLD);
	
  //fields
	private final int iMargin = 6;
	private final int iPadding = 8;
	private final int iValueSpacing = 2;
	private final int iUnderlineHeight = 2;
	
	private static int iStaticDisplayBottom = 0;
	public static final int iDisplayBottomMargin = 16;
	
  //fields
	private String sValueMask = null;
	private String sSuffixMask = null;
	private int iValueHeight = 0;
	private int iValueWidth = 0;

  //fields
	private final Boolean bValueMutex = false;
	private String sValue = "";
	private String sSuffix = "";
	private String sType = null;
	
  //fields
	protected String sDegreeSymbol = null; 

	
  //methods
	public DigitDisplay(CommonActivity parent, Screen screen, String sValueMask, String sSuffixMask, int res_id_type)
	{
		super(parent, screen);
		
		this.prefs = parent.getPrefs();
		this.state = parent.getMainState();
		
		this.sValueMask = sValueMask;
		this.sSuffixMask = sSuffixMask;
		this.sType = parent.getString(res_id_type);

		sDegreeSymbol = parent.getString(R.string.azimuth_degree);
		
		mTextValue.setAntiAlias(true);
		mTextValue.setColor(style.iTextColor1);
		mTextValue.setTextSize(style.fTextSize1);		
		mTextValue.setTypeface(tfBold);
		mTextValue.setTextAlign(Paint.Align.RIGHT);		

		mTextSuffix.setAntiAlias(true);
		mTextSuffix.setColor(style.iTextColor2);
		mTextSuffix.setTextSize(style.fTextSize2);		
		mTextSuffix.setTypeface(tfBold);
		mTextSuffix.setTextSkewX(fTextSkew);
		
		mTextType.setAntiAlias(true);
		mTextType.setColor(style.iTextColorUnderline);
		mTextType.setTypeface(tfBold);
		mTextType.setTextSize(style.fTextSizeUnderline);
		
		mBackground.setAntiAlias(true);
		mBackground.setColor(style.iBackgroundColor);

		mUnderline.setStrokeWidth(1.0f);		
		mUnderline.setColor(style.iUnderlineColor);
								
		updateSize();
		
		resetValue();
		
		//watch DataStats object for data change
		ActivityMain.loader.stats.addObserver(this);
	}

	private void updateSize()
	{
		Paint.FontMetricsInt fm = null;
		
		//get value char size for one digit
		mTextValue.getTextBounds("8", 0, 1, rtTextValue);
		
		fm = mTextValue.getFontMetricsInt();
		iValueWidth = (sValueMask.length() * rtTextValue.width());				
		iValueHeight = Math.abs(fm.ascent);
		
		//get value text bounds for value
		mTextValue.getTextBounds(sValue, 0, sValue.length(), rtTextValue);				
		
		//get suffix text rectangle
		if (sSuffixMask != null)
			mTextSuffix.getTextBounds(sSuffixMask, 0, sSuffixMask.length(), rtTextSuffix);
		
		//get type text rectangle
		updateTypeTextBounds();
		
		//initialize sizes
		final int iWidth = iPadding + iValueWidth + iMargin + rtTextSuffix.width() + iPadding;
		final int iHeight = iMargin + iValueHeight + iMargin + iUnderlineHeight + rtTextType.height() + iMargin;
		
		iStaticDisplayBottom = iHeight;
		
		setSize(iWidth, iHeight);
	}
	
	private void updateTypeTextBounds()
	{
		//get type text rectangle
		mTextType.getTextBounds(sType, 0, sType.length(), rtTextType);

		Paint.FontMetricsInt fm = mTextType.getFontMetricsInt();
		rtTextType.top = 0;
		rtTextType.bottom = Math.abs(fm.ascent);		
	}
	
	public void updateStyle()
	{
		style.iBackgroundColor = 0xff407080;
		style.iBackgroundColorDark = 0xff104050;

		style.iTextColor1 = 0xffffffff;
		style.iTextColor2 = 0xff6090b0;
		style.iTextColorUnderline = 0xff70b0c0;
		style.iUnderlineColor = 0xff507090;
		
		style.fTextSize1 = 36;
		style.fTextSize2 = 22;
		style.fTextSizeUnderline = 10;
		
		style.fBackgroundRound = 6.0f;
				
		style.rtBackgroundMarginCorrect.top = -12;		
	}

	public void doDraw(Canvas cs)
	{
		super.doDraw(cs);

		//draw gradient background
		if (bkgLinearGradient == null)
		{
			bkgLinearGradient = new LinearGradient(rtBackground.left, rtBackground.top, rtBackground.left, rtBackground.bottom,
					style.iBackgroundColor, style.iBackgroundColorDark, Shader.TileMode.CLAMP);
		}

		this.drawSimpleShadow(cs, 8);
		
		mBackground.setShader(bkgLinearGradient);
		cs.drawRoundRect(rtBackground, style.fBackgroundRound, style.fBackgroundRound, mBackground);		
		mBackground.setShader(null);

		//draw digits
		final float fTextValueTop = rtBounds.top + iMargin + iValueHeight;
		final float fTextValueRight = rtBounds.right - iPadding;
		
		synchronized(bValueMutex)
		{
			if (sSuffixMask == null)
			{
				//draw value
				cs.drawText(sValue, fTextValueRight, fTextValueTop, mTextValue);
			} else {				
				//get suffix text rectangle
				mTextSuffix.getTextBounds(sSuffix, 0, sSuffix.length(), rtTextSuffix);				
				//draw suffix
				cs.drawText(sSuffix, fTextValueRight - rtTextSuffix.width(), fTextValueTop, mTextSuffix);						
				//draw value
				cs.drawText(sValue, fTextValueRight - rtTextSuffix.width() - iValueSpacing, fTextValueTop, mTextValue);
			}
		}			
		
		//draw line
		final int iLinePosTop = iMargin + iValueHeight + iMargin;
					
		cs.drawLine(rtBounds.left + iPadding, iLinePosTop, rtBounds.right - iPadding, iLinePosTop, mUnderline);

		//draw type text
		final float fLineTextPosLeft = rtBounds.left + (rtBounds.width() >> 1) - (rtTextType.width() >> 1);		
		final float fLineTextPosTop = iLinePosTop + iUnderlineHeight + rtTextType.height();
		
		cs.drawText(sType, fLineTextPosLeft, fLineTextPosTop, mTextType);
	}

	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		
	}
	
	public abstract String getZeroValue();
	
	public void resetValue()
	{
		synchronized(bValueMutex)
		{
			this.sValue = getZeroValue();
			if (sSuffixMask == null)
				this.sSuffix = "";
			else
				this.sSuffix = "...";
		}		
	}
	
	public void setValue(String sValue, String sSuffix)
	{
		synchronized(bValueMutex)
		{
			this.sValue = sValue;
			this.sSuffix = sSuffix;
		}
	}
	
	public void setType(String sType)
	{
		this.sType = sType;		
		updateTypeTextBounds();
	}
		
	public static int getStaticDisplayBottom()
	{
		return iStaticDisplayBottom;
	}
	
	public void onClick()
	{
	}	

	public abstract boolean updateValues();

	public boolean isLocationEnabled()
	{
		return ActivityMain.loader.stats.isLocationEnabled();
	}
	
	public void update(Observable observable, Object data)
	{		
		if (observable == ActivityMain.loader.stats)
		{
			if (data.equals(ActivityMain.loader.stats.bUpdatedLocation))
			{
				if (!updateValues())
					resetValue();
			}
			if (data.equals(ActivityMain.loader.stats.bUpdatedMode))
			{
				if (!updateValues())
					resetValue();
			}
		}
	}
	
}
