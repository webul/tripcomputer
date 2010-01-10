package pl.tripcomputer;

import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.common.CommonThread;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


public class MainThread extends CommonThread
{
	//fields
  private SurfaceHolder mSurfaceHolder = null;
    
  //time elapsed between frames
  private long mLastFrameTime = 0;
    
  //drawing object
  private MainDraw mDraw = null;
    
  
	//methods
	public MainThread(SurfaceHolder holder, CommonActivity parent, Handler handler)
	{
		super(parent, handler);
				
    mSurfaceHolder = holder;
        
    mDraw = new MainDraw(parent);
	}
		
  protected void setSurfaceSize(int width, int height)
  {
    mDraw.surfaceSizeChanged(width, height, this.getScreenOrientation());	    
  }
	
  protected boolean onTouchEvent(MotionEvent event)
  {
 		return mDraw.onTouchEvent(event);
  }
    
  public void onInitializeData()
	{
  	//init layers
  	mDraw.initData();  	
	}
  
  //run thread
  public void run()
  {
  	super.run();
  	  	
  	//run animation loop
  	while (this.isActive())
    {
    	if (this.isRunning())
    	{
		    Canvas c = null;
		    try
		    {
		      c = mSurfaceHolder.lockCanvas(null);		      
      		if (isEnabled())
      		{
	      		updateLoop();
      			if (c != null)
      			{
   		      	mDraw.doDraw(c);
      			}
      		}
		    } finally {
		      if (c != null)
		      	mSurfaceHolder.unlockCanvasAndPost(c);
		    }
    	} else {
    		//sleep thread if not running
    		try
				{
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
    	}    	
    }  	
  }
  	
  private void updateLoop()
  {
  	//get now time
  	final long timeNow = System.currentTimeMillis();

		//start delay by 100ms or whatever
  	if (mLastFrameTime > timeNow)
  		return;

  	//update objects here
   	mDraw.updateObjectsState();
  	
  	//update tracks
  	ActivityMain.loader.updateObjectsState();

  	//update last frame time
  	mLastFrameTime = timeNow;  	
  }
  	
	public void onStateChanged()
	{
		if (isRunning())
			mLastFrameTime = System.currentTimeMillis() + 100;		
	}
  
  public Bundle saveState(Bundle map)
  {
  	if (map != null)
    {    		
  		//map.put...
    }
    return map;
  }
  
  public void restoreState(Bundle savedState)
  {
		if (savedState != null)
		{    	
  		//savedState.get...
		}
  }  
  
}
