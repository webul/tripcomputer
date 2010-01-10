package pl.tripcomputer.gps;

import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.data.tables.DataTableGeoPoints;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;


public class GpsLogger
{
	//fields
	public final static int MODE_NONE = 0;
	public final static int MODE_UPDATE = 1;
	public final static int MODE_ADD = 2;
	
	//fields
	private int iLocationSaveMode = MODE_NONE;
	
	//fields
	protected Context context = null;
  protected Database dataBase = null;
	private Location locationOld = null;	
	private final Boolean bLocationMutex = false;
		
	//fields
	private GpsLocation newGpsLocation = new GpsLocation();
	
	//fields
	private long lRecordingTrackId = -1;
	private long lNewPointId = -1; 

	
	//methods
	public GpsLogger(Context context, Database dataBase)
	{
		this.context = context;
		this.dataBase = dataBase;
	}
		
	public void setRecordingTrackId(long lTrackId)
	{
		synchronized(bLocationMutex)
		{
			lRecordingTrackId = lTrackId;
			locationOld = null;
		}
	}
	
	private long getRecordingTrackId()
	{
		long lValue = -1;
		synchronized(bLocationMutex)
		{
			lValue = lRecordingTrackId;
		}
		return lValue;
	}
	
	private void checkNewLocation(Location locationNew)
	{
		if (locationNew == null)
			return;
		
		iLocationSaveMode = MODE_NONE;		
		
		synchronized(bLocationMutex)
		{		
			if (locationOld == null)
			{
				locationOld = new Location(locationNew);
				iLocationSaveMode = MODE_ADD;
				return;
			}
	
			//add new location if distance changed
			if (locationOld.distanceTo(locationNew) > locationNew.getAccuracy())
			{
				locationOld = new Location(locationNew);
				iLocationSaveMode = MODE_ADD;
				return;
			} else {
				//update location if new accuracy is better
				if (locationNew.getAccuracy() < locationOld.getAccuracy())
				{
					locationOld = new Location(locationNew);
					iLocationSaveMode = MODE_UPDATE;
					return;
				}			
			}	
		}
		
		return;
	}	
		
	protected void saveLocation(Location locationNew)
	{
		if (locationNew != null)
		{
			//filter location and set save mode
			checkNewLocation(locationNew);

			//save location
			synchronized(bLocationMutex)
			{		
				if (locationOld != null)
				{
					if (getRecordingTrackId() != -1)
					{
						if (dataBase.isOpen() && dataBase.tableGeoPoints().exists())
						{
							if (iLocationSaveMode == MODE_ADD)
								saveLocation_ADD();
							
							if (iLocationSaveMode == MODE_UPDATE)
								saveLocation_UPDATE();																												
						}
					}
				}
			}
		}
	}

	private DataValues setDataValues(DataValues values)
	{
		final long lTrackId = getRecordingTrackId();
				
		synchronized(bLocationMutex)
		{			
			values.setValue(DataTableGeoPoints.field.TrackID, lTrackId);
			
			values.setValue(DataTableGeoPoints.field.Lon, locationOld.getLongitude());
			values.setValue(DataTableGeoPoints.field.Lat, locationOld.getLatitude());
			
			values.setValue(DataTableGeoPoints.field.Altitude, (int)locationOld.getAltitude());
			values.setValue(DataTableGeoPoints.field.TimeUTC, locationOld.getTime());
			
			values.setValue(DataTableGeoPoints.field.Accuracy, (int)locationOld.getAccuracy());
			values.setValue(DataTableGeoPoints.field.Speed, (int)locationOld.getSpeed());
			values.setValue(DataTableGeoPoints.field.Bearing, (int)locationOld.getBearing());
			
			values.setValueCurrentTime(DataTableGeoPoints.field.CreationTime);		
		}
		
		return values;
	}
	
	private void saveLocation_ADD()
	{
		final DataTableGeoPoints table = dataBase.tableGeoPoints();		
		final DataValues values = setDataValues(new DataValues(table));
		lNewPointId = table.dataInsert(values);
	}

	private void saveLocation_UPDATE()
	{
		final DataTableGeoPoints table = dataBase.tableGeoPoints();				
		if (lNewPointId != -1)
		{
			final Cursor inputCursor = table.getDataRecord(lNewPointId);			
			if (inputCursor != null)
			{
				final DataValues values = new DataValues(table, inputCursor);				
				table.dataUpdate(values, lNewPointId);
			}
		}
	}
	
	protected void sendNewLocationToParent(Location locationNew)
	{
		//send location data with Broadcast intent
		if (locationNew != null)
		{
			final long lPointID = (iLocationSaveMode == MODE_NONE) ? -1 : lNewPointId;

			newGpsLocation.set(locationNew, lPointID, iLocationSaveMode);			
			newGpsLocation.send(context);			
		}
	}

}
