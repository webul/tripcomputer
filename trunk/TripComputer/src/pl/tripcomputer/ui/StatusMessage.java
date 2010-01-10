package pl.tripcomputer.ui;

import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class StatusMessage extends UIElement
{
	//fields
	private static StatusMessage msg = null;
	private Paint mBackground = new Paint();
	private Paint mText = new Paint();
	private boolean bVisible = false;
	private Rect rtText = new Rect(0, 0, 0, 0);
	private String sText = "TEST";
	private int iTextHeight = 0;

	
	//methods
	public StatusMessage(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
		msg = this;
		
		mText.setAntiAlias(true);
		mText.setColor(style.iTextColor1);
		mText.setTextSize(style.fTextSize1);		

		mBackground.setAntiAlias(true);
		mBackground.setColor(style.iBackgroundColor);
		
		mText.getTextBounds(sText, 0, sText.length(), rtText);
		iTextHeight = rtText.height();
	}

	public void updateStyle()
	{
		style.iBackgroundColor = 0xff306070;
		style.iTextColor1 = 0xffffffff;
		style.fTextSize1 = 16;
		style.fBackgroundMargin = 6;
		style.fBackgroundRound = 6;		
	}
	
	public void doDraw(Canvas cs)
	{
		if (bVisible)
		{
			super.doDraw(cs);
	
			this.drawSimpleShadow(cs, 8);
			
			//draw background	
			cs.drawRoundRect(rtBackground, style.fBackgroundRound, style.fBackgroundRound, mBackground);

			//draw text
			cs.drawText(sText, rtBounds.left, rtBounds.top + iTextHeight, mText);
		}
	}	
	
	public void surfaceSizeChanged(int width, int height, int screenOrientation)
	{

	}

	public void onClick()
	{
		
	}
	
	public static void hide()
	{
		msg.bVisible = false;
		msg.sText = "";		
	}
	
	public static void showText(String sText)
	{
		synchronized (msg.sText)
		{
			msg.bVisible = false;
			msg.sText = sText;
	
			final int iMargin = msg.style.iButtonMargin + (int)msg.style.fBackgroundMargin;		
			final int iWidth = UIElement.iScreenWidth - (iMargin * 2); 
			final int iHeight = msg.iTextHeight + (int)msg.style.fBackgroundMargin;
			final int iLeft = iMargin;		
			final int iTop = iScreenHeight - iHeight;
			
			msg.setSize(iWidth, iHeight);
			msg.setPos(iLeft, iTop);
			
			msg.bVisible = true;
		}
	}

}
