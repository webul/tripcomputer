package pl.tripcomputer.loader;

import java.util.ArrayList;
import java.util.HashMap;

import pl.tripcomputer.map.GeoPoint;


public class DataStatsTrackCollection
{
	//track data item
	private class TrackPointsData
	{
		public ArrayList<GeoPoint> points = null;

		public TrackPointsData(TrackItem item)
		{
			setPoints(item);
		}
		
		public void setPoints(TrackItem item)
		{		
			//get points copy
			if (points == null)
			{		
				points = item.clonePoints();
			} else {			
				//get new points copy each time
				if (!item.isClosed())
				{
					points = item.clonePoints();
				}						
			}
		}		
	};
	
	//track items map. Key - lTrackId
	private Boolean bMapMutex = false;
	private HashMap<Long, TrackPointsData> mapTracksPoints = new HashMap<Long, TrackPointsData>(); 
	
	
	//methods
	private TrackPointsData getTrackPoints(TrackItem trackItem)
	{	
		final Long lTrackId = trackItem.getId();
		
		TrackPointsData data = null; 
			
		synchronized(bMapMutex)
		{
			//get track points data, create one if not exists
			data = mapTracksPoints.get(lTrackId);		
			if (data == null)
			{
				data = new TrackPointsData(trackItem);
				mapTracksPoints.put(lTrackId, data);			
			} else {
				data.setPoints(trackItem);
			}
		}
		
		return data;
	}
		
	protected GeoPoint getNearestPoint(GeoPoint location, TrackItem trackItem)	
	{
		if (trackItem != null)
		{		
			final TrackPointsData data = getTrackPoints(trackItem);		
			if (data != null)
			{
				return getNearestPoint(location, data);
			}
		}
		return null;
	}
	
	private GeoPoint getNearestPoint(GeoPoint location, TrackPointsData data)
	{
		GeoPoint targetPoint = null;
		
		if (location == null)
			return null;
		
		//thousand km
		double dMaxDistance = 1000 * 1000;
		
		synchronized(bMapMutex)
		{
			if (data == null)
				return null;
			
			if (data.points.isEmpty())
				return null;

			if (data.points.size() > 0)
				targetPoint = data.points.get(0);
			
			for (GeoPoint point : data.points)
			{
				final double dDistance = location.distance(point);
				
				if (dDistance < dMaxDistance)
				{
					targetPoint = point;
					dMaxDistance = dDistance;
				}												
			}
		}

		return targetPoint;
	}
	
}
