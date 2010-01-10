package pl.tripcomputer.data.items;

import pl.tripcomputer.data.common.DataItem;
import pl.tripcomputer.data.tables.DataTableGeoPoints;
import pl.tripcomputer.map.GeoPoint;
import android.database.Cursor;


public class DataItemGeoPoint extends DataItem
{
	//fields
	public long lTrackId = -1;
	
	public double lon = 0; //X - longitude
	public double lat = 0; //Y - latitude	

	public int iAltitude = 0;
	
	public long lTimeUTC = 0;

	public int iAccuracy = 0;
	public int iSpeed = 0;
	public int iBearing = 0;
	
	public long lCreationTime = 0;
	
	
	//methods
	public DataItemGeoPoint()
	{
	}
	
	public DataItemGeoPoint(DataItemGeoPoint item)
	{
		this.iID = item.iID;
		this.lTrackId = item.lTrackId;
		
		this.lon = item.lon;
		this.lat = item.lat;
		
		this.iAltitude = item.iAltitude;
		this.lTimeUTC = item.lTimeUTC;
		this.iAccuracy = item.iAccuracy;
		this.iSpeed = item.iSpeed;
		this.iBearing = item.iBearing;
		
		this.lCreationTime = item.lCreationTime;
		
		this.dDistance = item.dDistance;
	}
	
	public DataItemGeoPoint(final Cursor cr)
	{
		this.iID = cr.getLong(DataTableGeoPoints.field.ID);		
		this.lTrackId = cr.getLong(DataTableGeoPoints.field.TrackID);
		
		this.lon = cr.getDouble(DataTableGeoPoints.field.Lon);
		this.lat = cr.getDouble(DataTableGeoPoints.field.Lat);
		
		this.iAltitude = cr.getInt(DataTableGeoPoints.field.Altitude);		
		this.lTimeUTC = cr.getLong(DataTableGeoPoints.field.TimeUTC);		
		this.iAccuracy = cr.getInt(DataTableGeoPoints.field.Accuracy);
		this.iSpeed = cr.getInt(DataTableGeoPoints.field.Speed);
		this.iBearing = cr.getInt(DataTableGeoPoints.field.Bearing);
		
		this.lCreationTime = cr.getLong(DataTableGeoPoints.field.CreationTime);
	}
	
	//wgs distance in meters
	public double distance(double wgsLon, double wgsLat)
	{
		GeoPoint geoPoint1 = new GeoPoint();
		GeoPoint geoPoint2 = new GeoPoint();

		geoPoint1.wgsLon = lon;
		geoPoint1.wgsLat = lat;

		geoPoint2.wgsLon = wgsLon;
		geoPoint2.wgsLat = wgsLat;

		return geoPoint1.distance(geoPoint2);
	}
		
}
