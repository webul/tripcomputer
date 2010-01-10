package pl.tripcomputer.map;

import java.util.List;
import java.util.Observable;


public class Screen extends Observable
{
	//fields
	public final static Integer RESET_VIEWPORT_MODE_NONE = 0;
	public final static Integer RESET_VIEWPORT_MODE_TRACK = 1;
	public final static Integer RESET_VIEWPORT_MODE_LOCATION = 2;
	public final static Integer RESET_VIEWPORT_MODE_WAYPOINT = 3;
	
	//fields
	private Integer lCurrentResetViewPortMode = RESET_VIEWPORT_MODE_NONE;
	
	//fields
	private Viewport viewport = new Viewport();
	
	private int iWidth = 0;
	private int iHeight = 0;
	
	private double dPixelViewportFactor = 0;
	private double dPixelSize = 0;

	//grid
	private GeoPoint pointGridStart = new GeoPoint(0,0);
	private final static double RANGE_100M = 100.0f;
	private final static double RANGE_200M = 200.0f;
	private final static double RANGE_INV_100M = 0.01f;
	
	
	//methods
	public Screen()
	{
		resetViewPort();	
	}

	public void setSize(int iWidth, int iHeight)
	{
		this.iWidth = iWidth;
		this.iHeight = iHeight;
		
		viewport.setScreenAspectRatio(getAspectRatio());
		
		updatePixelSize();
	}
	
	private void updatePixelSize()
	{
		dPixelViewportFactor = 0;
		dPixelSize = 0;

		double dViewW = viewport.getWidth();
		double dViewH = viewport.getHeight();
		
		if ((dViewW == 0) || (dViewH == 0))
			return;
		
		dPixelViewportFactor = ((double)iWidth / dViewW);		
		dPixelSize = (dViewW / (double)iWidth);
	}

	public void resetViewPort()
	{
		final GeoPoint point = new GeoPoint(0,0);
		
		viewport.reset(point);
		updatePixelSize();
		
		synchronized(lCurrentResetViewPortMode)
		{
			lCurrentResetViewPortMode = RESET_VIEWPORT_MODE_NONE;
		}
	}
	
	public void resetViewPort(final GeoPoint point, Integer lResetViewPortMode)
	{
		viewport.reset(point);
		updatePixelSize();
		updateResetViewPortMode(lResetViewPortMode);
	}

	public void resetViewPort(final List<GeoPoint> points, Integer lResetViewPortMode)
	{
		viewport.reset(points);
		updatePixelSize();
		updateResetViewPortMode(lResetViewPortMode);
	}

	public void setStartupResetViewPortMode(Integer lResetViewPortMode)
	{
		synchronized(lCurrentResetViewPortMode)
		{
			lCurrentResetViewPortMode = lResetViewPortMode;
		}
	}
	
	private void updateResetViewPortMode(Integer lResetViewPortMode)
	{
		synchronized(lCurrentResetViewPortMode)
		{
			lCurrentResetViewPortMode = lResetViewPortMode;
		}
		setChanged();
		notifyObservers(lResetViewPortMode);		
	}

	private void updateCurrentResetViewPortMode()
	{
		synchronized(lCurrentResetViewPortMode)
		{
			setChanged();
			notifyObservers(lCurrentResetViewPortMode);		
		}
	}
	
	public Integer getCurrentResetViewPortMode()
	{
		Integer lMode = RESET_VIEWPORT_MODE_NONE;
		synchronized(lCurrentResetViewPortMode)
		{
			lMode = lCurrentResetViewPortMode;
		}
		return lMode;
	}

	public int getWidth()
	{
		return iWidth;
	}
	
	public int getHeight()
	{
		return iHeight; 
	}
	
	public double getPixelSize()
	{
		return dPixelSize;
	}
	
	public double getViewportWidth()
	{
		return viewport.getWidth();
	}
	
	public double getViewportHeight()
	{
		return viewport.getHeight();
	}
	
	public double getViewportLeft()
	{
		return viewport.getMinX();
	}

	public double getViewportRight()
	{
		return viewport.getMaxX();
	}
	
	public double getViewportTop()
	{
		return viewport.getMinY();
	}

	public double getViewportBottom()
	{
		return viewport.getMaxY();
	}
	
	public double getAspectRatio()
	{
		if (getHeight() == 0)
			return 0;
		return ((double)getWidth() / (double)getHeight());
	}
	
	public void shiftViewPort(float fPixelsX, float fPixelsY)
	{
		synchronized(viewport)
		{
			viewport.shift(fPixelsX * dPixelSize, fPixelsY * dPixelSize);
		}
		updateCurrentResetViewPortMode();
	}
	
	public void zoomViewPort(float dScaleMeters)
	{
		boolean bChanged = false;
		synchronized(viewport)
		{
			bChanged = viewport.addMargin(dScaleMeters, dScaleMeters);		
		}
		if (bChanged)
			updateCurrentResetViewPortMode();
	}
	
	//Mercator to flat screen projection
	public void toScreenPoint(GeoPoint geoPoint, ScreenPoint screenPoint)
	{		
		screenPoint.x = (int)((geoPoint.mercX - viewport.getMinX()) * dPixelViewportFactor);		

		//invert Y, because screen coords increments down, and geo coords increments up
		screenPoint.y = (int)((geoPoint.mercY - viewport.getMaxY()) * -dPixelViewportFactor);
	}
	
	//returns INTEGER coords of grid start for each 100 meters cell 
	public GeoPoint getGridStart()
	{
		//to calculate grid start for cell size at 100m,
		//mul double * 0.01, then trunc modulo, and mul again for 100 
		pointGridStart.mercX = (int)(viewport.getMinX() * RANGE_INV_100M) * RANGE_100M;
		
		//get bottom, because latitude increases down
		pointGridStart.mercY = (int)(viewport.getMaxY() * RANGE_INV_100M) * RANGE_100M;
		
		//check if value is at 200m marker and save result to unused fields
		pointGridStart.wgsLon = ((pointGridStart.mercX % RANGE_200M) == 0) ? 1 : 0;
		pointGridStart.wgsLat = ((pointGridStart.mercY % RANGE_200M) == 0) ? 1 : 0;

		return pointGridStart;
	}

	public double getGridCellPixelSize()
	{
		return (dPixelSize == 0) ? 0 : (RANGE_100M / dPixelSize);
	}
	
}
