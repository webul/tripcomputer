package pl.tripcomputer;

import pl.tripcomputer.common.CommonActivity;
import android.os.*;
import android.view.*;
import android.content.Context;
import android.util.AttributeSet;


public class MainView extends SurfaceView implements SurfaceHolder.Callback
{
	//objects
  private MainThread thread = null;
  
	
	//methods
	public MainView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

    //set properties
    setFocusable(true);    
	}
	
	public void init(CommonActivity parent)
	{
		//get surface events
    SurfaceHolder holder = getHolder();
    holder.setType(SurfaceHolder.SURFACE_TYPE_HARDWARE); 
    holder.addCallback(this);

    setFocusable(true);
    
    // create thread, it's started in surfaceCreated()
    thread = new MainThread(holder, parent, new Handler()
    {
	    public void handleMessage(Message msg)
	    {
	    	//handle messagess put by child thread	    	
	    	//final Bundle bundle = msg.getData();	    	
	    }
    });
	}
	
	//returns animation thread
  public MainThread getThread()
  {
  	return thread;
  }

  //surface event
  public void surfaceCreated(SurfaceHolder holder)
  {
  	thread.setStateRunning();
  }
	
  //surface event
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
  {
  	thread.setSurfaceSize(width, height);
  }
	
  //surface event
  public void surfaceDestroyed(SurfaceHolder holder)
  {
  	thread.setStatePaused();
  }
  
  public boolean onTouchEvent(MotionEvent event)
  {
  	return thread.onTouchEvent(event);
  }
  
}
