package pl.tripcomputer.layers;

import pl.tripcomputer.Command;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.compass.CompassRotation;
import pl.tripcomputer.map.Screen;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class LayerCross extends Layer
{
  //fields
	private static final int iRadius = 24;
  private Paint mShape = new Paint();  

  
  //methods
	public LayerCross(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
  	//paint lines
		mShape.setAntiAlias(false);
		mShape.setColor(0xff80b0c0);
		mShape.setStrokeWidth(1);
		mShape.setStyle(Paint.Style.FILL);
	}

	public void initData()
	{

	}

	public void surfaceSizeChanged(int width, int height, int screenOrientation)
	{

	}

	public void doDraw(Canvas cs, Rect rtBounds)
	{
		if (state.getCurrentUiMode() == Command.CMD_MODE_COMPASS)
		{		
			final int iCenterX = CompassRotation.rotationCenterX();
			final int iCenterY = CompassRotation.rotationCenterY();
	
			mShape.setColor(0xffff6666);
			cs.drawLine(iCenterX, rtBounds.top, iCenterX, iCenterY - iRadius, mShape);

			mShape.setColor(0xffa0d0e0);
			cs.drawLine(iCenterX, iCenterY + iRadius, iCenterX, rtBounds.bottom, mShape);
			cs.drawLine(rtBounds.left, iCenterY, iCenterX - iRadius, iCenterY, mShape);
			cs.drawLine(iCenterX + iRadius, iCenterY, rtBounds.right, iCenterY, mShape);
		}
	}

	public void updateObjectsState()
	{

	}
	
}
