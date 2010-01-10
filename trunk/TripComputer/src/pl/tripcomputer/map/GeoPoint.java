package pl.tripcomputer.map;

import pl.tripcomputer.data.items.DataItemGeoPoint;
import pl.tripcomputer.data.tables.DataTableGeoPoints;
import android.database.Cursor;


public class GeoPoint
{
	//fields
	private final static double earthRadius = 6372.795 * 1000.0; //meters
	
	//fields
	public long iID = -1;
	
	public int iAltitude = 0;
	
	public long lTimeUTC = 0;

	public int iAccuracy = 0;
	public int iSpeed = 0;
	public int iBearing = 0;
	
	public long lCreationTime = 0;
	
	public double wgsLon = 0; //X - longitude
	public double wgsLat = 0; //Y - latitude
	
	public double mercX = 0;
	public double mercY = 0;	

	
	//methods
	public GeoPoint()
	{
	}
	
	public GeoPoint(final Cursor cr)
	{
		this.iID = cr.getLong(DataTableGeoPoints.field.ID);
		
		this.wgsLon = cr.getDouble(DataTableGeoPoints.field.Lon);
		this.wgsLat = cr.getDouble(DataTableGeoPoints.field.Lat);
		
		this.iAltitude = cr.getInt(DataTableGeoPoints.field.Altitude);		
		this.lTimeUTC = cr.getLong(DataTableGeoPoints.field.TimeUTC);		
		this.iAccuracy = cr.getInt(DataTableGeoPoints.field.Accuracy);
		this.iSpeed = cr.getInt(DataTableGeoPoints.field.Speed);
		this.iBearing = cr.getInt(DataTableGeoPoints.field.Bearing);
		
		this.lCreationTime = cr.getLong(DataTableGeoPoints.field.CreationTime);
	}
	
	public GeoPoint(DataItemGeoPoint item)
	{
		this.iID = item.iID;
		
		this.wgsLon = item.lon;
		this.wgsLat = item.lat;
		
		this.iAltitude = item.iAltitude;
		this.lTimeUTC = item.lTimeUTC;
		this.iAccuracy = item.iAccuracy;
		this.iSpeed = item.iSpeed;
		this.iBearing = item.iBearing;
		this.lCreationTime = item.lCreationTime;
	}
	
	public GeoPoint(double lon, double lat)
	{
		this.wgsLon = lon;
		this.wgsLat = lat;
	}

	public GeoPoint(GeoPoint geoPoint)
	{
		this.iID = geoPoint.iID;
		
		this.wgsLon = geoPoint.wgsLon;
		this.wgsLat = geoPoint.wgsLat;
		
		this.iAltitude = geoPoint.iAltitude;
		this.lTimeUTC = geoPoint.lTimeUTC;
		this.iAccuracy = geoPoint.iAccuracy;
		this.iSpeed = geoPoint.iSpeed;
		this.iBearing = geoPoint.iBearing;
		this.lCreationTime = geoPoint.lCreationTime;		
	}
	
	public void clear()
	{
		iID = -1;
		iAltitude = 0;
		lTimeUTC = 0;
		iAccuracy = 0;
		iSpeed = 0;
		iBearing = 0;
		lCreationTime = 0;
		
		wgsLon = 0;
		wgsLat = 0;
		mercX = 0;
		mercY = 0;
	}
	
	//returns distance in meters by WGS coords
	public double distance(GeoPoint geoPoint)
	{
		double lat1 = Math.toRadians(wgsLat);
		double lon1 = Math.toRadians(wgsLon);
		double lat2 = Math.toRadians(geoPoint.wgsLat);
		double lon2 = Math.toRadians(geoPoint.wgsLon);		
		double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1));		
		return d * earthRadius;	 
	}
	
	public String toString()
	{
		return "lon: " + Double.toString(wgsLon) + ", lat: " + Double.toString(wgsLat) + ", x: " + Double.toString(mercX) + ", y: " + Double.toString(mercY);   
	}

	public String asText()
	{
		return Double.toString(wgsLon) + " , " + Double.toString(wgsLat);
	}
	
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(wgsLat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(wgsLon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GeoPoint))
			return false;
		
		GeoPoint other = (GeoPoint) obj;
		
		if (Double.doubleToLongBits(wgsLon) != Double.doubleToLongBits(other.wgsLon))
			return false;
		if (Double.doubleToLongBits(wgsLat) != Double.doubleToLongBits(other.wgsLat))
			return false;

		if (Double.doubleToLongBits(mercX) != Double.doubleToLongBits(other.mercX))
			return false;
		if (Double.doubleToLongBits(mercY) != Double.doubleToLongBits(other.mercY))
			return false;
		
		return true;
	}

	public Object clone()
	{			
		return new GeoPoint(this);
	}
	
}
