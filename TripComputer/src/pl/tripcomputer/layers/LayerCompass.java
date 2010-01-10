package pl.tripcomputer.layers;

import pl.tripcomputer.Command;
import pl.tripcomputer.MainDraw;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.compass.CompassRotation;
import pl.tripcomputer.map.Screen;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


public class LayerCompass extends Layer
{
  //fields
	private static final int iRadius = 70;
	private static final int iBig = 4;

	//fields
  private Paint mShape = new Paint();
  private Paint mLine = new Paint();

  protected Drawable dirN = null;
  protected Drawable dirS = null;
  protected Drawable dirW = null;
  protected Drawable dirE = null;

  protected int iDirIconW = 0;
  protected int iDirIconH = 0;

  
  //methods
	public LayerCompass(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
		dirN = parent.getResources().getDrawable(R.drawable.dir_arrow_n);
		dirS = parent.getResources().getDrawable(R.drawable.dir_arrow_s);
		dirW = parent.getResources().getDrawable(R.drawable.dir_arrow_w);
		dirE = parent.getResources().getDrawable(R.drawable.dir_arrow_e);
		
		iDirIconW = dirN.getIntrinsicWidth();
		iDirIconH = dirS.getIntrinsicHeight();
		
  	//paint shape
		mShape.setAntiAlias(true);
		mShape.setColor(0x33408090);
		mShape.setStrokeWidth(1);
		mShape.setStyle(Paint.Style.FILL);

		//paint lines
		mLine.setAntiAlias(true);
		mLine.setColor(0xff408090);
		mLine.setStrokeWidth(1);
		mLine.setStyle(Paint.Style.STROKE);
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
	
			cs.drawCircle(iCenterX, iCenterY, iRadius, mShape);
			cs.drawCircle(iCenterX, iCenterY, iRadius, mLine);
			
			MainDraw.compassRotation.set(cs);
			
			//draw arrows left/right
			cs.drawLine(iCenterX - iRadius - iBig, iCenterY, iCenterX + iRadius + iBig, iCenterY, mLine);
			cs.drawLine(iCenterX, iCenterY - iRadius - iBig, iCenterX, iCenterY + iRadius + iBig, mLine);
			
			//draw directions
			final int iIconY_WE = iCenterY - (iDirIconH >> 1);
			final int iIconX_NS = iCenterX - (iDirIconW >> 1);
			final int iIconY_N = iCenterY - iRadius - iBig - iDirIconH;
			final int iIconY_S = iCenterY + iRadius + iBig;
			final int iIconY_W = iCenterX - iRadius - iBig - iDirIconH;
			final int iIconY_E = iCenterX + iRadius + iBig;
			
			dirN.setBounds(iIconX_NS, iIconY_N, iIconX_NS + iDirIconW, iIconY_N + iDirIconH);	
			dirN.draw(cs);

			dirS.setBounds(iIconX_NS, iIconY_S, iIconX_NS + iDirIconW, iIconY_S + iDirIconH);	
			dirS.draw(cs);
			
			dirW.setBounds(iIconY_W, iIconY_WE, iIconY_W + iDirIconW, iIconY_WE + iDirIconH);	
			dirW.draw(cs);
			
			dirE.setBounds(iIconY_E, iIconY_WE, iIconY_E + iDirIconW, iIconY_WE + iDirIconH);	
			dirE.draw(cs);
			
			MainDraw.compassRotation.clear(cs);
		}
	}

	public void updateObjectsState()
	{

	}
	
}
