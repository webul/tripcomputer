package pl.tripcomputer.loader;

import java.util.Observable;
import java.util.Observer;

import android.location.Location;
import android.location.LocationManager;

import pl.tripcomputer.Command;
import pl.tripcomputer.MainState;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.gps.GpsLocation;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.map.GeoPoint;
import pl.tripcomputer.map.Mercator;


public class DataStats extends Observable implements Observer
{
	//observer flag objects
	public final Boolean bUpdatedMode = false;
	public final Boolean bUpdatedLocation = false;

	//fields
	private final static int iAvgWalkSpeedMetersPerHour = 4500;
	private final static int iAvgBikeSpeedMetersPerHour = 15000;

	//fields
	private MainState state = null;
	private GpsLocationStatus gpsLocationStatus = null;

	//fields
	private final Boolean bDataAccessMutex = false;

  //stats objects
	private TrackItem trackItem = null;
	private WaypointItem waypointItem = null;
	private GpsLocation gpsLocation = new GpsLocation();
	private GeoPoint locationPoint = new GeoPoint();
	private int iAltitude = 0;
	
  //track items data
	private DataStatsTrackCollection tracksStats = new DataStatsTrackCollection();
	
  //nearest selected track point
	private GeoPoint locationNearest = null;
	private Boolean bLocationNearestMutex = false;

	//bearing helpers
	private Location locBearing1 = new Location(LocationManager.GPS_PROVIDER);
	private Location locBearing2 = new Location(LocationManager.GPS_PROVIDER);
	
	
	//methods
	public DataStats(ActivityMain parent, GpsLocationStatus gpsLocationStatus)
	{
		this.state = parent.getMainState();
		this.gpsLocationStatus = gpsLocationStatus;
		
		//watch state object for UI mode change
		state.addObserver(this);
	
		//watch location change
		gpsLocationStatus.addObserver(this);
	}
	
	public void update(Observable observable, Object data)
	{		
		if (observable == state)
		{
			if (data == MainState.UI_MODE_UPDATE)
				updateObjectsSelection();
		}		
		if (observable == gpsLocationStatus)
		{
			if (data == GpsLocationStatus.STATE_NEW_GPS_LOCATION)
				updateLocation();
		}
	}
	
	public boolean isLocationEnabled()
	{
		return gpsLocation.isEnabled();
	}

	private void updateLocation()
	{
		synchronized(bDataAccessMutex)
		{
			//get current location
			ActivityMain.loader.gpsLocationStatus().getCurrent(gpsLocation);
			
			if (gpsLocation.isEnabled())
			{
				locationPoint.wgsLon = gpsLocation.wgsLon;
				locationPoint.wgsLat = gpsLocation.wgsLat;
				iAltitude = gpsLocation.iAltitude;
			}
			
			//update nearest track point
			updateNearestTrackPoint();

			setChanged();
			notifyObservers(bUpdatedLocation);			
		}
	}
	
	private void updateObjectsSelection()
	{
		synchronized(bDataAccessMutex)
		{		
			//get selected objects
			trackItem = null;
			waypointItem = null;
	
			final long lTrackId = state.getSelectedTrackId();
			final long lWaypointId = state.getSelectedWaypointId();
	
			if (lTrackId != -1)
				trackItem = ActivityMain.loader.getSelectedTrack();
			
			if (lWaypointId != -1)
				waypointItem = ActivityMain.loader.getSelectedWaypoint();
						
			//update nearest track point
			updateNearestTrackPoint();
			
			setChanged();
			notifyObservers(bUpdatedMode);			
		}
	}

	private void updateNearestTrackPoint()
	{
		if (state.getCurrentUiMode() == Command.CMD_MODE_LOC_TO_SEL_TRACK)
		{			
			if (trackItem != null)
			{
				if (isLocationEnabled())
				{	
					synchronized(bLocationNearestMutex)
					{
						GeoPoint point = tracksStats.getNearestPoint(locationPoint, trackItem);
						if (point != null)
						{
							locationNearest = new GeoPoint(point);
							Mercator.ConvertToFlat(locationNearest);																				
						}						
					}					
				}
			}
		}		
	}
	
	//returns -1 if value not valid
	public int getTrackTimeInSeconds()
	{	
		int iValue = -1;
		synchronized(bDataAccessMutex)
		{
			if (trackItem != null)
			{			
				iValue = trackItem.dataTrack.getTotalTimeInSeconds();
			}
		}
		return iValue;
	}	
	
	//returns -1 if value not valid
	public double getTotalDistanceInMeters()
	{
		double dValue = -1;
		synchronized(bDataAccessMutex)
		{
			if (trackItem != null)
			{
				dValue = trackItem.getTotalDistanceInMeters();
			}
		}
		return dValue;		
	}

	//returns -1 if value not valid
	public double getDistanceToWaypointInMeters()
	{
		double dValue = -1;
		synchronized(bDataAccessMutex)
		{
			if (waypointItem != null)
			{						
				if (isLocationEnabled())
				{
					final GeoPoint targetPoint = waypointItem.getPosition();
					dValue = locationPoint.distance(targetPoint);					
				}
			}
		}
		return dValue;		
	}

	public double getDistanceToLocationInMeters(double dLon, double dLat)
	{
		double dValue = -1;
		synchronized(bDataAccessMutex)
		{
			if (isLocationEnabled())
			{
				final GeoPoint targetPoint = new GeoPoint(dLon, dLat);
				dValue = locationPoint.distance(targetPoint);
			}
		}
		return dValue;		
	}
	
	private int roundTimeUp(double dTime)
	{
		if ((dTime > 15) && (dTime < 60))
			dTime = 60;

		return (int)dTime;
	}
	
	//returns -1 if value not valid, time in seconds
	public int getWaypointArrivalTimebyWalk()
	{
		final float fMetersPerSecond = iAvgWalkSpeedMetersPerHour / 3600;
		
		final double dDistance = getDistanceToWaypointInMeters();
		if (dDistance == -1)
			return -1;

		return roundTimeUp(dDistance / fMetersPerSecond);
	}	

	//returns -1 if value not valid, time in seconds
	public int getWaypointArrivalTimebyBike()
	{
		final float fMetersPerSecond = iAvgBikeSpeedMetersPerHour / 3600;

		final double dDistance = getDistanceToWaypointInMeters();
		if (dDistance == -1)
			return -1;

		return roundTimeUp(dDistance / fMetersPerSecond);
	}

	//returns -1 if value not valid, time in seconds
	public int getLocationArrivalTimebyWalk(double dLon, double dLat)
	{
		final float fMetersPerSecond = iAvgWalkSpeedMetersPerHour / 3600;
		
		final double dDistance = getDistanceToLocationInMeters(dLon, dLat);
		if (dDistance == -1)
			return -1;

		return roundTimeUp(dDistance / fMetersPerSecond);
	}	

	//returns -1 if value not valid, time in seconds
	public int getLocationArrivalTimebyBike(double dLon, double dLat)
	{
		final float fMetersPerSecond = iAvgBikeSpeedMetersPerHour / 3600;

		final double dDistance = getDistanceToLocationInMeters(dLon, dLat);
		if (dDistance == -1)
			return -1;

		return roundTimeUp(dDistance / fMetersPerSecond);
	}
	
	//returns -1 if value not valid
	public int getTrackArrivalTimebyWalk()
	{
		final float fMetersPerSecond = iAvgWalkSpeedMetersPerHour / 3600;
		
		final double dDistance = getDistanceToTrackInMeters();
		if (dDistance == -1)
			return -1;

		return roundTimeUp(dDistance / fMetersPerSecond);
	}	
	
	//returns -1 if value not valid
	public int getTrackArrivalTimebyBike()
	{
		final float fMetersPerSecond = iAvgBikeSpeedMetersPerHour / 3600;
		
		final double dDistance = getDistanceToTrackInMeters();
		if (dDistance == -1)
			return -1;

		return roundTimeUp(dDistance / fMetersPerSecond);
	}	
	
	//returns -1 if value not valid
	public double getDistanceToTrackInMeters()
	{
		double dValue = -1;
		if (isLocationEnabled())
		{
			synchronized(bLocationNearestMutex)
			{
				if (locationNearest != null)
					dValue = locationPoint.distance(locationNearest);
			}					
		}
		return dValue;
	}
	
	public GeoPoint getTrackNearestPoint()
	{
		GeoPoint point = null;
		if (isLocationEnabled())
		{
			synchronized(bLocationNearestMutex)
			{
				point = locationNearest;
			}
		}
		return point;
	}
	
	public int getBearingToWaypoint()
	{
		int iValue = -1;
		{
			synchronized(bDataAccessMutex)
			{
				if (waypointItem != null)
				{						
					if (isLocationEnabled())
					{						
						final GeoPoint targetPoint = waypointItem.getPosition();
						if (targetPoint != null)
						{
							locBearing1.setLongitude(locationPoint.wgsLon);
							locBearing1.setLatitude(locationPoint.wgsLat);
							
							locBearing2.setLongitude(targetPoint.wgsLon);
							locBearing2.setLatitude(targetPoint.wgsLat);

							iValue = (int)locBearing1.bearingTo(locBearing2);
							
							if (iValue < 0)
							{
								iValue = 360 + iValue;
							}							
						}											
					}
				}
			}
		}				
		return iValue;
	}
	
	//returns track average speed in meters per second
	public float getTrackAverageSpeed(long lTrackId)
	{
		float fSpeed = 0;
		final TrackItem trackItem = ActivityMain.loader.getTrackItem(lTrackId);
		if (trackItem != null)
		{
			return getTrackAverageSpeed(trackItem);
		}		
		return fSpeed;
	}

	public float getTrackAverageSpeed(final TrackItem trackItem)
	{
		float fSpeed = 0;
		if (trackItem != null)
		{
			//get track time
			int iTime = 0;
			synchronized(trackItem.dataTrack)
			{				
				iTime = trackItem.dataTrack.getTotalTimeInSeconds();
			}
			//get distance
			if (iTime != 0)
			{
				//calc speed
				fSpeed = (float)(trackItem.getTotalDistanceInMeters() / (float)iTime);
			}
		}		
		return fSpeed;
	}
	
	//returns current track average speed or -1 if value not valid / track not selected
	public float getTrackAverageSpeed()
	{
		float fSpeed = -1;
		synchronized(bDataAccessMutex)
		{
			if (trackItem != null)
			{
				fSpeed = getTrackAverageSpeed(trackItem.getId());
			}
		}		
		return fSpeed;
	}
	
	public int getAltitude()
	{
		int iValue = -1;
		synchronized(bDataAccessMutex)
		{
			if (isLocationEnabled())
			{
				iValue = iAltitude;
			}
		}
		return iValue;
	}
	
}
