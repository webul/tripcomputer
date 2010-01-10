package pl.tripcomputer.map;

import pl.tripcomputer.map.GeoPoint;

import java.util.List;


//returns x/y position in meters
public class Mercator
{
	final private static double R_MAJOR = 6378137.0;
	final private static double R_MINOR = 6356752.3142;
	final private static double eccent_e = Math.sqrt(1.0 - Math.pow(R_MINOR / R_MAJOR, 2));
	final private static double eccent_e_half = 0.5 * eccent_e;
	final private static double PI_half = Math.PI * 0.5;

	public static void ConvertToFlat(final GeoPoint geoPoint)
	{
		geoPoint.mercX = GetX(geoPoint.wgsLon);
		geoPoint.mercY = GetY(geoPoint.wgsLat);
	}
	
	public static void ConvertToFlat(final List<GeoPoint> points)
	{
		for (int i = 0; i < points.size(); i++)
		{
			GeoPoint point = points.get(i);
			point.mercX = GetX(point.wgsLon);
			point.mercY = GetY(point.wgsLat);
		}
	}

	public static double GetX(double lon)
	{
		return R_MAJOR * Math.toRadians(lon);
	}

	public static double GetY(double lat)
	{
		if (lat > 89.5)
			lat = 89.5;

	  if (lat < -89.5)
	  	lat = -89.5;
	   
	  double phi = Math.toRadians(lat);
	  double con = eccent_e * Math.sin(phi);
	  con = Math.pow(((1.0 - con) / (1.0 + con)), eccent_e_half);
	  double ts = Math.tan(0.5 * (PI_half - phi)) / con;
	  double y = 0 - R_MAJOR * Math.log(ts);
	  return y;
	}

	//simple mercator for square area
	public static double GetSquareY(double lat)	
	{
    double latitude = Math.toRadians(lat);
    return R_MAJOR / 2.0 * Math.log( (1.0 + Math.sin(latitude)) / (1.0 - Math.sin(latitude)) );
	}
		
}
