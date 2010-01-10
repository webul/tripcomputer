package pl.tripcomputer.ui;

import pl.tripcomputer.Utils;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class Ruler extends UIElement
{
	//fields
	private static Ruler ruler = null;
	private Paint mLine = new Paint();
	private Paint mText = new Paint();
	private Rect rtText = new Rect(0, 0, 0, 0);
	private String sText = "";
	private int iTextHeight = 0;
	private int iRulerWidth = 0;
	private Boolean bRulerVisible = false;
	
	
	//methods
	public Ruler(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
		ruler = this;
		
		mText.setAntiAlias(true);
		mText.setColor(style.iTextColor1);
		mText.setTextSize(style.fTextSize1);		

		mLine.setStrokeWidth(1.0f);
		mLine.setColor(style.iBackgroundColor);
		
		mText.getTextBounds(sText, 0, sText.length(), rtText);
		iTextHeight = rtText.height();
		
		this.bAlwaysVisible = true;
	}

	public void onClick()
	{

	}

	public void updateStyle()
	{
		style.iBackgroundColor = 0xffffcccc;
		style.iTextColor1 = 0xff880000;
		style.fTextSize1 = 14;
		style.fBackgroundMargin = 4;
	}
	
	public void surfaceSizeChanged(int width, int height, int screenOrientation)
	{
		final int iHeight = iTextHeight + (int)style.fBackgroundMargin;
		final int iTop = iScreenHeight - iHeight - (int)style.iButtonHeight;
		
		iRulerWidth = (width >> 1);

		setSize(iRulerWidth, iHeight);
		setPos(0, iTop);
	}
	
	public void doDraw(Canvas cs)
	{
		super.doDraw(cs);
		
		if (!bRulerVisible)
			return;

		update();
		
		if (sText.length() > 0)
		{
			//draw background
			cs.drawLine(rtBounds.left, rtBounds.bottom, iRulerWidth, rtBounds.bottom, mLine);
			cs.drawLine(rtBounds.right, rtBounds.bottom, iRulerWidth, rtBounds.bottom + 5, mLine);
			cs.drawLine(rtBounds.right, rtBounds.bottom, iRulerWidth, rtBounds.bottom - 5, mLine);
			
			//draw text			
			cs.drawText(sText, rtBounds.left + style.iButtonMargin, rtBounds.top + iTextHeight, mText);
		}
	}

	public static void show(boolean bEnable)
	{
		synchronized (ruler.bRulerVisible)
		{
			ruler.bRulerVisible = bEnable;
		}
	}
	
	private void update()
	{
		final double dScaleMeters = (screen.getPixelSize() * (double)iRulerWidth);		
		sText = Utils.getDistanceAsStringPrecise(parent.getPrefs(), dScaleMeters);
	}
	
}
