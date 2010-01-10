package pl.tripcomputer.ui;

import java.util.ArrayList;

import pl.tripcomputer.MainDraw;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.Canvas;
import android.view.MotionEvent;


public class UIScreen extends UIElement
{
  //layers
	private ArrayList<UIElement> elements = new ArrayList<UIElement>();

	//buttons
	private ButtonZoomIn btnZoomIn = null;
	private ButtonZoomOut btnZoomOut = null;
	private ButtonZoomLocation btnZoomLocation = null;
	
	private ButtonSelectTrack btnSelectTrack = null;
	private ButtonSelectWaypoint btnSelectWaypoint = null;
	private ButtonChangeMode btnChangeMode = null;
	
	private ButtonTrackState btnTrackState = null;
	
	//misc
	private static Boolean bUserInterfaceVisible = false;
	private long mUserInterfaceVisibleTime = 0;
	private static final int mUserInterfaceVisibileDuration = 3000;


	//methods
	public UIScreen(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
  	//create misc items
  	elements.add(new DigitDisplayDistance(parent, screen));
  	elements.add(new DigitDisplayTime(parent, screen));
  	elements.add(new GPSDisplay(parent, screen));
  	elements.add(new ModeStatus(parent, screen));
  	elements.add(new TrackStatus(parent, screen));
  	elements.add(new StatusMessage(parent, screen));
  	elements.add(new Ruler(parent, screen));
  	
		//create buttons		
		int iPosY = style.iButtonTopPos;
		
		btnZoomIn = new ButtonZoomIn(parent, screen);
		btnZoomIn.iPosY = iPosY;
		
		btnZoomLocation = new ButtonZoomLocation(parent, screen);
		iPosY += style.iButtonMargin + style.iButtonHeight;
		btnZoomLocation.iPosY = iPosY;
		
		btnZoomOut = new ButtonZoomOut(parent, screen);
		iPosY += style.iButtonMargin + style.iButtonHeight;
		btnZoomOut.iPosY = iPosY;
				
		iPosY = style.iButtonTopPos;
		
		btnSelectTrack = new ButtonSelectTrack(parent, screen);
		btnSelectTrack.iPosY = iPosY;
		
		btnSelectWaypoint = new ButtonSelectWaypoint(parent, screen);
		iPosY += style.iButtonMargin + style.iButtonHeight;
		btnSelectWaypoint.iPosY = iPosY;
		
		btnChangeMode = new ButtonChangeMode(parent, screen);
		iPosY += style.iButtonMargin + style.iButtonHeight;
		btnChangeMode.iPosY = iPosY;
		
		btnTrackState = new ButtonTrackState(parent, screen);
		iPosY += style.iButtonMargin + style.iButtonHeight;
		btnTrackState.iPosY = iPosY;

		elements.add(btnZoomIn);
  	elements.add(btnZoomOut);
  	elements.add(btnZoomLocation);
  	elements.add(btnSelectTrack);
  	elements.add(btnSelectWaypoint);
  	elements.add(btnChangeMode);  	
  	elements.add(btnTrackState);  	
	}
	
	public void doDraw(Canvas cs)
	{		
		UIElement element = null;

		final boolean bVisible = isUserInterfaceVisible(); 
				
		//draw permanent elements first
		for (int i = 0; i < elements.size(); i++)
		{
			element = elements.get(i);
			if (element.bAlwaysVisible)
				element.doDraw(cs);
		}
		
		//then draw swichtable elements
		for (int i = 0; i < elements.size(); i++)
		{
			element = elements.get(i);
			if (!element.bAlwaysVisible)
				if (bVisible)
					element.doDraw(cs);
		}
	}

	public void updateObjectsState()
	{
		final boolean bVisible = isUserInterfaceVisible(); 		
		
  	for (int i = 0; i < elements.size(); i++)
  		elements.get(i).updateObjectsState();
  	
  	if (bVisible)
  	{
  		if ((System.currentTimeMillis() - mUserInterfaceVisibleTime) >= mUserInterfaceVisibileDuration)
  			hideUserInterface();
  	}
	}
	
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		UIElement.iScreenWidth = width;
		UIElement.iScreenHeight = height;

  	for (int i = 0; i < elements.size(); i++)
  		elements.get(i).surfaceSizeChanged(width, height, iScreenOrientation);
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		if (MainDraw.isMapTouchMode())
			return false;
		
		if (isUserInterfaceVisible())
		{	
	  	//process TOUCH
	  	if ((event.getAction() == MotionEvent.ACTION_DOWN) || (event.getAction() == MotionEvent.ACTION_MOVE)) 
	  	{
				for (int i = 0; i < elements.size(); i++)
		  	{
					final UIElement element = elements.get(i);													
					if (element.isInTouchArea(event))
					{
						element.setPressed();
						return true;
					}
		  	}
	  	}
	  	//process TOUCH_UP
	  	if (event.getAction() == MotionEvent.ACTION_UP)
	  	{
				for (int i = 0; i < elements.size(); i++)
		  	{
					final UIElement element = elements.get(i);					
					final boolean bPressed = element.isPressedDown();
	
					element.clearPressed();
					
					if (bPressed && element.isInTouchArea(event))
					{
						element.doClick();
						return true;
					}					
				}
	  	}
		}
		return false;
	}
		
	public static boolean isUserInterfaceVisible()
	{
		synchronized(bUserInterfaceVisible)
		{
			return bUserInterfaceVisible;
		}
	}

	public void showUserInterface()
	{
		synchronized(bUserInterfaceVisible)
		{
			bUserInterfaceVisible = true;
		}
		mUserInterfaceVisibleTime = System.currentTimeMillis();
	}

	private void hideUserInterface()
	{
		synchronized(bUserInterfaceVisible)
		{
			bUserInterfaceVisible = false;
		}
		StatusMessage.hide();
	}
	
	public void onClick()
	{
		
	}
	
}
