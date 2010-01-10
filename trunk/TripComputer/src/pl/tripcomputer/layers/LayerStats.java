package pl.tripcomputer.layers;

import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.*;


public class LayerStats extends Layer
{
  //time elapsed between frames
  private long mLastFrameTime = 0;
	  

  //methods
	public LayerStats(CommonActivity parent, Screen screen)
	{
		super(parent, screen);		
	}

	public void initData()
	{
		
	}
	
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		
	}
	
	public void doDraw(Canvas cs, Rect rtBounds)
	{
		final long mDuration = System.currentTimeMillis() - mLastFrameTime;
  	mLastFrameTime = System.currentTimeMillis();		
		
		Rect rt = cs.getClipBounds();
		
		final int iLineH = 16;		
		final int iPosX = 8;
		
		int iPosY = 120;
		
		cs.drawText("Rectangle: " + rt.width() + "x" + rt.height(), iPosX, iPosY, mDefaultText);
		iPosY += iLineH;
		
		cs.drawText("Frame time: " + mDuration + ", framerate: " + (1000 / mDuration), iPosX, iPosY, mDefaultText);
		iPosY += iLineH;

		/*
		cs.drawText("Viewport: " + screen.getViewportWidth() + ", " + screen.getViewportHeight(), iPosX, iPosY, mDefaultText);
		iPosY += iLineH;
		
		cs.drawText("Pixel width: " + screen.getPixelSizeX(), iPosX, iPosY, mDefaultText);
		iPosY += iLineH;

		cs.drawText("Screen width (m): " + screen.getPixelSizeX() * screen.getWidth(), iPosX, iPosY, mDefaultText);
		iPosY += iLineH;
		*/

		cs.drawText("Test time: " + mTestLastFrameTime + " ms", iPosX, iPosY, mDefaultText);
		iPosY += iLineH;
		
		//cs.drawText("True north declination: " + Float.toString(compassRotation.fTrueNorthDeclination), iPosX, iPosY, mDefaultText);
		//iPosY += iLineH;
		
	}

	public void updateObjectsState()
	{
		
	}

}
