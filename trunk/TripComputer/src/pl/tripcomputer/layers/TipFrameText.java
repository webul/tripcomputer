package pl.tripcomputer.layers;

import pl.tripcomputer.MainState;
import pl.tripcomputer.Preferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class TipFrameText extends TipFrame
{
	//fields
  private Paint mText = new Paint();
  private String[] vecLines = { };
	private Rect rtText = new Rect(0, 0, 0, 0);
	

	//methods
	public TipFrameText(Preferences prefs, MainState state, String sTitle)
	{
		super(prefs, state, sTitle);
		
  	//text
		mText.setAntiAlias(true);
		mText.setColor(0xff442200);
		mText.setStyle(Paint.Style.FILL);
		mText.setTextSize(14);	
		mText.setTextAlign(Paint.Align.CENTER);		
	}

  public void setText(String sText)
  {
  	if (sText == null)
  	{
  		vecLines = new String[0];
  		return;
  	}
  	
  	if (sText.trim().length() == 0)
  	{
  		vecLines = new String[0];
  	} else {
  		vecLines = sText.split("\n");
  	}
  }

  protected int getHeight()
  {
  	int value = super.getHeight();
  	
  	final int iLinesCount = vecLines.length;
  	mText.getTextBounds(sTestText, 0, sTestText.length(), rtText);
  	  	
  	if (iLinesCount > 0)  		
  	{
  		value += (rtText.height() + LINE_MARGIN) * iLinesCount;
  		value -= LINE_MARGIN;
    	value += PADDING;
  	}
  	
  	return value;
  }
  
	public void doDraw(Canvas cs, Rect rtBounds)
	{
		super.doDraw(cs, rtBounds);
		
		//draw text
		int iTextPosY = getTitleBottom() + rtText.height();
		for (int i = 0; i < vecLines.length; i++)
		{
			cs.drawText(vecLines[i], rtShape.centerX(), iTextPosY, mText);			
			iTextPosY += rtText.height() + LINE_MARGIN;
		}
		
	}
  
}
