package pl.tripcomputer.map;

import java.util.List;


public class Viewport
{
	private static double zeroRadius = 500.0; //meters
	private static double marginScale = 0.2;
	
	private double dScreenAspectRatio = 1;
	
	private GeoPoint min = new GeoPoint();
	private GeoPoint max = new GeoPoint();

	public void setScreenAspectRatio(double dScreenAspectRatio)
	{
		this.dScreenAspectRatio = dScreenAspectRatio;
	}
	
	public void reset(final GeoPoint point)
	{
		final double dHalfRadius = zeroRadius / 2;
		
		min.mercX = point.mercX - dHalfRadius;
		min.mercY = point.mercY - dHalfRadius;
		max.mercX = point.mercX + dHalfRadius;
		max.mercY = point.mercY + dHalfRadius;

		updateAspect();
		addMargin(marginScale);
	}

	public void reset(final GeoPoint point1, final GeoPoint point2)
	{
		min.mercX = Math.min(point1.mercX, point2.mercX);
		min.mercY = Math.min(point1.mercY, point2.mercY);
		max.mercX = Math.max(point1.mercX, point2.mercX);
		max.mercY = Math.max(point1.mercY, point2.mercY);

		updateAspect();
		addMargin(marginScale);
	}
	
	public void reset(final List<GeoPoint> points)
	{
		if (points.size() == 0)
			return;
				
		if (points.size() == 1)
		{
			reset(points.get(0));			
			return;
		}

		if (points.size() == 2)
		{
			reset(points.get(0), points.get(1));
			return;
		}
		
		GeoPoint pointBegin = points.get(0); 
		min.mercX = pointBegin.mercX;
		min.mercY = pointBegin.mercY;
		max.mercX = pointBegin.mercX;
		max.mercY = pointBegin.mercY;
		
		for (int i = 0; i < points.size(); i++)
		{
			final GeoPoint point = points.get(i);

			//min
			if (point.mercX < min.mercX)
				min.mercX = point.mercX;
			if (point.mercY < min.mercY)
				min.mercY = point.mercY;

			//max
			if (point.mercX > max.mercX)
				max.mercX = point.mercX;
			if (point.mercY > max.mercY)
				max.mercY = point.mercY;
		}
		
		updateAspect();
		addMargin(marginScale);
	}
	
	public double getMinX()
	{
		return min.mercX;
	}

	public double getMaxX()
	{
		return max.mercX;
	}
	
	public double getMinY()
	{
		return min.mercY;
	}

	public double getMaxY()
	{
		return max.mercY;
	}
		
	public double getWidth()
	{
		return (max.mercX - min.mercX);
	}

	public double getHeight()
	{
		return (max.mercY - min.mercY); 
	}
	
	public double getAspectRatio()
	{
		if (getHeight() == 0)
			return 0;
		return getWidth() / getHeight();
	}

	private void addMargin(double dScale)
	{
		addMargin((getWidth() * dScale), (getHeight() * dScale)); 
	}
	
	public boolean addMargin(double dMetersX, double dMetersY)
	{
		if (getWidth() + (dMetersX * 2) < 0)
			return false;
		
		if (getHeight() + (dMetersY * 2) < 0)
			return false;
		
		min.mercX -= dMetersX;
		min.mercY -= dMetersY;
		max.mercX += dMetersX;
		max.mercY += dMetersY;
		
		return true;
	}
	
	public void shift(double dMetersX, double dMetersY)
	{
		min.mercX += dMetersX;
		min.mercY += dMetersY;
		max.mercX += dMetersX;
		max.mercY += dMetersY;	
	}
	
	private void updateAspect()
	{
		if (dScreenAspectRatio > getAspectRatio())
		{
			final double dDiffW = (dScreenAspectRatio * getHeight()) - getWidth();
			addMargin(dDiffW / 2.0, 0);
		}
		if (dScreenAspectRatio < getAspectRatio())
		{
			final double dDiffH = (getWidth() / dScreenAspectRatio) - getHeight();
			addMargin(0, dDiffH / 2.0);	
		}
	}
	
	public String toString()
	{
		String s = "l: " + min.mercX + ", r: " + max.mercX + ", t: " + min.mercY + ", b: " + max.mercY;		
		return s;
	}

}
