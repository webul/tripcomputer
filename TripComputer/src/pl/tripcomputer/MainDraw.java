package pl.tripcomputer;

import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.compass.CompassRotation;
import pl.tripcomputer.layers.*;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.ui.*;
import android.graphics.*;
import android.view.MotionEvent;


public class MainDraw
{
	//fields
  protected CommonActivity parent = null;
  private Screen screen = null;
  private Paint mPaintBackground = new Paint();
  private SetOfLayers layers = null;
  private UIScreen ui = null;
    
  //compass rotation
	public static CompassRotation compassRotation = null;
	
	private static boolean bTouchMapMode = false;
	
	private LayerStats stats = null;
  
  
	//methods  
  public MainDraw(CommonActivity parent)
  {
  	this.parent = parent;
  	this.screen = ActivityMain.loader.getScreen();
  	
  	mPaintBackground.setAntiAlias(false);
  	mPaintBackground.setStyle(Paint.Style.FILL);  	
  	mPaintBackground.setARGB(255, 255, 255, 255);
  	
		//create shared compass
		compassRotation = new CompassRotation(parent);  	
  	
  	layers = new SetOfLayers(parent, screen);
  	ui = new UIScreen(parent, screen);
  	
  	//stats = new LayerStats(parent, screen);  
  }
    
  //called before animation loop starts
  public void initData()
  {
  	layers.initData();
  }
  
  public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
  {
  	//update new size to all objects, resize images, etc..
  	  	
  	layers.surfaceSizeChanged(width, height, iScreenOrientation);
  	
  	ui.surfaceSizeChanged(width, height, iScreenOrientation);
  	
  	ActivityMain.loader.surfaceSizeChanged(width, height, iScreenOrientation);
  }
  
  public void doDraw(Canvas cs)
  { 	
  	final Rect rt = cs.getClipBounds();

  	//screen size set in surfaceSizeChanged(..)
  	synchronized(screen)
  	{
  		screen.setSize(rt.width(), rt.height());
  	}
  	
  	//clear screen
  	cs.drawRect(rt, mPaintBackground);

  	//draw grid first
  	if (parent.getPrefs().bShowGrid)
  	{
    	layers.doDrawGrid(cs, rt);
  	}  	
  	
  	//update shared compass data
  	compassRotation.updateState(screen, cs, rt);
	
  	//draw bearing line under objects
  	layers.doDrawBearing(cs, rt);
  	
  	//draw data
  	ActivityMain.loader.doDraw(cs, rt);

  	//draw layers
  	layers.doDraw(cs, rt);
  	
  	//draw UI elements last
  	ui.doDraw(cs);
		
		if (stats != null)
			stats.doDraw(cs, rt);		  	
  }
  
  //update objects state
  public void updateObjectsState()
  {  	
  	layers.updateObjectsState();
  	ui.updateObjectsState();
  }  

  public static boolean isMapTouchMode()
  {
  	return bTouchMapMode;
  }
  
  //handle touch events
  public boolean onTouchEvent(MotionEvent event)
  { 
  	boolean bHandled = false;

  	if (ui.onTouchEvent(event))
  	{
  		ui.showUserInterface();
  		
  		bHandled = true;  		
  	} else {
	  	
	  	if (event.getAction() == MotionEvent.ACTION_DOWN)
	  	{
	  		bTouchMapMode = true;
	  		
	  		ActivityMain.loader.initShiftViewPort(event);
	  	  
				ui.showUserInterface();
				  	  
				bHandled = true;
	  	}

	  	if (event.getAction() == MotionEvent.ACTION_MOVE)	  		
	  	{
	  		//shift map
	  		ActivityMain.loader.handleShiftViewPort(event);

	  		bHandled = true;
	  	}
	  	
	  	if (event.getAction() == MotionEvent.ACTION_UP)
	  	{
	  		bTouchMapMode = false;
	  		
	  		//bHandled = true;
	  	}
	  	
  	}  	
  	
  	return bHandled;
  }
      
}
