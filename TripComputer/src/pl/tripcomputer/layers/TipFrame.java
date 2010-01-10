package pl.tripcomputer.layers;

import pl.tripcomputer.MainState;
import pl.tripcomputer.Preferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;


public abstract class TipFrame
{
	//fields
  protected final static String sTestText = "Text";
  protected final static int PADDING = 8;
  protected final static int LINE_TITLE_MARGIN = 10;
  protected final static int LINE_MARGIN = 6;

  protected Preferences prefs = null;
	protected MainState state = null;
  
  private Paint mShadow = new Paint();
  private Paint mShape = new Paint();
  private Paint mBorder = new Paint();
  private Paint mTitle = new Paint();
  
  private int iTop = 0;
  private int iLeft = 0;
  private int iRight = 0;
  
  private String sTitle = "Title";
  
  protected RectF rtShadow = new RectF();
  protected RectF rtShape = new RectF();
  protected Rect rtTitle = new Rect(0, 0, 0, 0);
  
  
  //methods
  public TipFrame(Preferences prefs, MainState state, String sTitle)
  {
		this.prefs = prefs;
		this.state = state;
		this.sTitle = sTitle;
  	
  	//shape
		mShape.setAntiAlias(true);
		mShape.setColor(0xffffff88);
		mShape.setStrokeWidth(1);
		mShape.setStyle(Paint.Style.FILL);
		
		//shadow
		mShadow.setAntiAlias(true);
		mShadow.setColor(0x22888888);
		mShadow.setStrokeWidth(1);
		mShadow.setStyle(Paint.Style.FILL);
  	
  	//border
		mBorder.setAntiAlias(true);
		mBorder.setColor(0xffdada00);
		mBorder.setStrokeWidth(2);
		mBorder.setStyle(Paint.Style.STROKE);
		
  	//title
		mTitle.setAntiAlias(true);
		mTitle.setColor(0xffaa5500);
		mTitle.setFakeBoldText(true);		
		mTitle.setStyle(Paint.Style.FILL);
		mTitle.setTextSize(14);		
		mTitle.setTextAlign(Paint.Align.CENTER);
  }
    
  public void setPosition(int iTop)
  {
  	this.iTop = iTop;
  }
  
  public void setMargins(int iLeft, int iRight)
  {
  	this.iLeft = iLeft;
  	this.iRight = iRight;  	
  }
  
  private int getTitleHeight()
  {
  	mTitle.getTextBounds(sTestText, 0, sTestText.length(), rtTitle);
  	
  	int value = PADDING;
  	
  	value += rtTitle.height();
  	
  	value += LINE_TITLE_MARGIN;
  	
  	return value;
  }
  
  protected int getHeight()
  {
  	return getTitleHeight();
  }

  protected int getTitleBottom()
  {
  	return (int)rtShape.top + PADDING + rtTitle.height() + LINE_TITLE_MARGIN;
  }
  
	public void doDraw(Canvas cs, Rect rtBounds)
	{
		final int iHeight = getHeight();
		
		rtShape.set(rtBounds);
		
		rtShape.top += iTop;
		rtShape.left += iLeft;
		rtShape.right -= iRight;
		rtShape.bottom = rtShape.top + iHeight;

		//draw shadow
		rtShadow.set(rtShape);
		rtShadow.inset(-4, -4);
		cs.drawRoundRect(rtShadow, 6, 6, mShadow);
		
		//draw background
		cs.drawRoundRect(rtShape, 4, 4, mShape);
		cs.drawRoundRect(rtShape, 4, 4, mBorder);
		
		//draw title
		final int iTitlePosY = (int)rtShape.top + PADDING + rtTitle.height();
		cs.drawText(sTitle, rtShape.centerX(), iTitlePosY, mTitle);
	}
	
}
