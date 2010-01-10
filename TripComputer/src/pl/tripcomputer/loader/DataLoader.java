package pl.tripcomputer.loader;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import pl.tripcomputer.MainState;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.R;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.layers.LayerLocation;
import pl.tripcomputer.layers.WaypointIcons;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.ui.StatusMessage;


public class DataLoader
{
	//fields
  private MainState state = null;
	private Screen screen = new Screen();
	private GpsLocationStatus gpsLocationStatus = new GpsLocationStatus();
	
	//data
	private TrackLoader trackLoader = null;
	private WaypointLoader waypointLoader = null;
		
	private boolean bSelectOpenTrackAtStart = true; 
	private String sMsgLocationTrackingOff = null;
	
	//fields
	public DataStats stats = null;

	//shift data
	private float fShiftInitX = -1;
	private float fShiftInitY = -1;
	
	
  //methods
	public DataLoader(ActivityMain parent)
  {
		WaypointIcons.load(parent);
		
		this.state = parent.getMainState();
		
  	this.trackLoader = new TrackLoader(parent, screen, gpsLocationStatus);
  	this.waypointLoader = new WaypointLoader(parent, screen, gpsLocationStatus);  	  	

  	sMsgLocationTrackingOff = parent.getString(R.string.location_tracking_off);
  	
		this.stats = new DataStats(parent, gpsLocationStatus);
  }

  public GpsLocationStatus gpsLocationStatus()
  {
  	return gpsLocationStatus;
  }

  public ArrayList<Long> getTrackList()
  {
  	trackLoader.items().sortItems();
  	return trackLoader.items().getIdList();
  }

  public ArrayList<Long> getWaypointList()
  {
  	waypointLoader.items().sortItems();
  	return waypointLoader.items().getIdList();
  }

  public TrackItem getTrackItem(long lTrackId)
  {
  	TrackItem item = null;
  	synchronized(trackLoader.items())
  	{
  		item = trackLoader.items().getLoaderItem(lTrackId);
  	}
  	return item;
  }

  public WaypointItem getWaypointItem(long lWaypointId)
  {
  	WaypointItem item = null;
  	synchronized(waypointLoader.items())
  	{  	
  		item = waypointLoader.items().getLoaderItem(lWaypointId);
  	}
  	return item;
  }
  
	public Screen getScreen()
	{
		return screen;
	}

	public TrackCollection tracks()
	{
		return trackLoader.items();
	}

	public WaypointCollection waypoints()
	{
		return waypointLoader.items();
	}
	
  public void loadData()
  {
  	//load all data
  	trackLoader.loadData();
  	waypointLoader.loadData();  	

  	//set startup viewport mode
  	screen.setStartupResetViewPortMode(Screen.RESET_VIEWPORT_MODE_LOCATION);
  	state.setSelectedLocation();
  }

  public void selectLocation()
  { 		
  	state.setSelectedLocation();
 		LayerLocation.resetViewPort();
  }

  public void switchLocationState()
  { 		
		if (state.isSelectedLocation())
		{
			state.clearLocationSelection();
		} else {
	  	state.setSelectedLocation();
	 		LayerLocation.resetViewPort();
		}
  }
  
  public void clearTrackSelection()
  {
  	trackLoader.clearTrackSelection();
  }

  public void clearWaypointSelection()
  {
  	waypointLoader.clearWaypointSelection();
  }
  
  public void selectOpenTrack()
  {
  	trackLoader.selectOpenTrack();
  }
  
  public void selectNextTrack()
  {
  	trackLoader.selectNextObject();
  }

  public void selectNextWaypoint()
  {
  	waypointLoader.selectNextObject();
  }

  public boolean isSelectedLocation()
  {
  	return state.isSelectedLocation();
  }

	public TrackItem getSelectedTrack()
	{
		return trackLoader.getSelectedTrack();
	}
  
	public WaypointItem getSelectedWaypoint()
	{
		return waypointLoader.getSelectedWaypoint();
	}
  
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		trackLoader.items().surfaceSizeChanged(width, height, iScreenOrientation);
		waypointLoader.items().surfaceSizeChanged(width, height, iScreenOrientation);
	}
  
  public void updateObjectsState()
	{
  	trackLoader.items().updateObjectsState();
  	waypointLoader.items().updateObjectsState();
  	
  	//select open track at startup
  	if (bSelectOpenTrackAtStart)
  	{
  		bSelectOpenTrackAtStart = false;
  		trackLoader.selectOpenTrack();
  	}
	}
  
  public void doDraw(Canvas cs, Rect rt)
  {
  	synchronized(screen)
  	{
  		screen.setSize(rt.width(), rt.height());
  	}
  	
  	//draw tracks
  	trackLoader.items().doDraw(cs, rt, state.getSelectedTrackId());

  	//draw waypoints
  	waypointLoader.items().doDraw(cs, rt, state.getSelectedWaypointId());
  }
  
  public void initShiftViewPort(MotionEvent event)
  {
  	fShiftInitX = event.getX();
  	fShiftInitY = event.getY();  	
  }
	
  public void handleShiftViewPort(MotionEvent event)
  {
		final float fCurrX = event.getX();
		final float fCurrY = event.getY();
  	  	
		final float fShiftX = fShiftInitX - fCurrX;  		
		final float fShiftY = fCurrY - fShiftInitY;
		
		fShiftInitX = fCurrX;
		fShiftInitY = fCurrY;
		
		//shift viewport
  	synchronized(screen)
  	{
  		screen.shiftViewPort(fShiftX, fShiftY);  		
  	}
  	
  	//disable tracking location if map view shifted
  	if ((fShiftX > 2) || (fShiftY > 2))
  		disableLocationTracking();
  }
  
  private void disableLocationTracking()
  {
  	if (state.isSelectedLocation())
  	{
  		state.clearLocationSelection();
  		StatusMessage.showText(sMsgLocationTrackingOff);
  	}
  }
  
  private float getZoomStep()
  {
  	float fZoomStep = 100;
  	
  	//2km
  	if (screen.getViewportWidth() >= 2000)
  		fZoomStep = 200;
  	
  	//5km
  	if (screen.getViewportWidth() >= 5000)
  		fZoomStep = 400;
  	
  	return fZoomStep;
  }
  
  public void viewPortZoomIn()
  {
  	state.clearLocationSelection();

  	synchronized(screen)
  	{
  		screen.zoomViewPort(-getZoomStep());
  	}
  }

  public void viewPortZoomOut()
  {
  	state.clearLocationSelection();

  	synchronized(screen)
  	{
  		screen.zoomViewPort(getZoomStep());
  	}  	
  }
  
	public ArrayList<DataItemWaypoint> removeExistingWaypoints(ArrayList<DataItemWaypoint> items)
	{
		return waypointLoader.removeExistingItems(items);
	}
  
}
