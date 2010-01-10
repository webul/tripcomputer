package pl.tripcomputer.ui;

import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.*;
import android.os.Handler;
import android.view.MotionEvent;


public abstract class UIElement
{
	//fields
	protected static final float fTextSkew = -0.2f;
	
	protected CommonActivity parent = null;
	protected Screen screen = null;

	private Paint mBackgroundShadow = new Paint();
	
	protected Paint mDefaultText = new Paint();
	protected Rect rtBounds = new Rect(0,0,0,0);
	protected RectF rtBackground = new RectF(0,0,0,0);
	protected RectF rtBackgroundShadow = new RectF(0,0,0,0);		

	protected UIElementStyle style = new UIElementStyle();
	
	protected static int iScreenWidth = 0;
	protected static int iScreenHeight = 0;
	
	private Boolean bPressedDown = false;
	protected boolean bAlwaysVisible = false;
		
	private Handler mClickHandler = new Handler();
	
	
	//methods
	public UIElement(CommonActivity parent, Screen screen)
	{
		this.parent = parent;
		this.screen = screen;

		//paint
		mDefaultText.setAntiAlias(true);
		mDefaultText.setARGB(255, 0, 0, 0);

		mBackgroundShadow.setAntiAlias(true);

		updateStyle();
	}
	
	public void setPos(int iLeft, int iTop)
	{
		rtBounds.offsetTo(iLeft, iTop);
	}
	
	public void setSize(int iWidth, int iHeight)
	{
		rtBounds.right = rtBounds.left + iWidth;
		rtBounds.bottom = rtBounds.top + iHeight;
	}
	
	public void setSize(float fWidth, float fHeight)
	{
		setSize((int)fWidth, (int)fHeight);		
	}

	public int getWidth()
	{
		return (int)rtBounds.width();
	}
	
	public int getHeight()
	{
		return (int)rtBounds.height();
	}
	
	private void updateBackgroundRect()
	{	
		rtBackground.set(rtBounds);
		rtBackground.inset(-style.fBackgroundMargin, -style.fBackgroundMargin);

		rtBackground.left += style.rtBackgroundMarginCorrect.left;
		rtBackground.right += style.rtBackgroundMarginCorrect.right;
		rtBackground.top += style.rtBackgroundMarginCorrect.top;
		rtBackground.bottom += style.rtBackgroundMarginCorrect.bottom;
	}

	protected void drawSimpleShadow(Canvas cs, float fRound)
	{	
		mBackgroundShadow.setColor(0xffdddddd);
		rtBackgroundShadow.set(rtBackground);
		rtBackgroundShadow.inset(-2, -2);
		cs.drawRoundRect(rtBackgroundShadow, fRound, fRound, mBackgroundShadow);
	}	
	
	public void updateStyle()
	{
		
	}

	public void doDraw(Canvas cs)
	{
		updateBackgroundRect();		
	}
	
	public void updateObjectsState()
	{
	}
	
	public boolean isPressedDown()
	{
		synchronized(bPressedDown)
		{
			return bPressedDown;
		}
	}
	
	protected void setPressed()
	{
		synchronized(bPressedDown)
		{
			bPressedDown = true;
		}		
	}

	protected void clearPressed()
	{
		synchronized(bPressedDown)
		{
			bPressedDown = false;
		}		
	}
	
	public boolean isInTouchArea(MotionEvent event)
	{
		return rtBackground.contains(event.getX(), event.getY());
	}
	
  public abstract void surfaceSizeChanged(int width, int height, int iScreenOrientation);  
	public abstract void onClick();

	protected void doClick()
	{
		mClickHandler.removeCallbacks(mTaskDoClick);
		mClickHandler.post(mTaskDoClick);
	}

	private Runnable mTaskDoClick = new Runnable()
	{
		public void run()
		{
			synchronized(mClickHandler)
			{			
				onClick();
			}		
		}
	};
	
}
