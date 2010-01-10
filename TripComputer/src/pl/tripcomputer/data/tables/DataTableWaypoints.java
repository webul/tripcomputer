package pl.tripcomputer.data.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import pl.tripcomputer.UserAlert;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.common.DataField;
import pl.tripcomputer.data.common.DataRowSet;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataTableOperation;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.data.items.DataItemWaypoint;
import android.database.Cursor;


public class DataTableWaypoints extends DataTable
{
	//limits
	private final static int NAME_SIZE_LIMIT = 32;
	private final static int DESC_SIZE_LIMIT = 400;
	
	
	//generic collection class
	public class RowSet extends DataRowSet<DataItemWaypoint>
	{
		public RowSet(DataTable table)
		{
			super(table);
		}
		protected DataItemWaypoint getItem(Cursor cr)
		{
			return new DataItemWaypoint(cr);
		}
	}

	protected DataRowSet<DataItemWaypoint> rows = null;
	
	
	//fields
	public final static String sTableName = "Waypoints";

	public static class field
	{
		//DataItemGeoPoint
		public static final int ID = 0;
		public static final int TrackID = 1;
		public static final int Lon = 2;
		public static final int Lat = 3;
		public static final int Altitude = 4;
		public static final int TimeUTC = 5;
		public static final int Accuracy = 6; //not used in waypoint, can be changed for future use
		public static final int Speed = 7; //not used in waypoint, can be changed for future use
		public static final int Sent = 8; //changed from Bearing in DataTableGeoPoints
		public static final int CreationTime = 9;
		//DataItemWaypoint
		public static final int Name = 10;
		public static final int Description = 11;
		public static final int Type = 12;
		public static final int Public = 13;
		public static final int UID = 14;
	};
	
	private final static DataField[] vecTableDefinition =
	{
		//DataItemGeoPoint
		new DataField(field.ID, DataTable.sID, DataField.TYPE_INT, false, true, false),
		new DataField(field.TrackID, "TrackID", DataField.TYPE_INT, false, false, false),
		new DataField(field.Lon, "Lon", DataField.TYPE_NUMERIC, false, false, true),
		new DataField(field.Lat, "Lat", DataField.TYPE_NUMERIC, false, false, true),
		new DataField(field.Altitude, "Altitude", DataField.TYPE_INT, false, false, true),
		new DataField(field.TimeUTC, "TimeUTC", DataField.TYPE_INT, false, false, false),
		new DataField(field.Accuracy, "Accuracy", DataField.TYPE_INT, true, false, false),
		new DataField(field.Speed, "Speed", DataField.TYPE_INT, true, false, false),
		new DataField(field.Sent, "Bearing", DataField.TYPE_BOOL, false, false, false),
		new DataField(field.CreationTime, "CreationTime", DataField.TYPE_INT, false, false, false),
		
		//DataItemWaypoint		
		new DataField(field.Name, "Name", DataField.TYPE_TEXT, false, false, true),
		new DataField(field.Description, "Description", DataField.TYPE_TEXT, true, false, true),
		new DataField(field.Type, "Type", DataField.TYPE_INT, false, false, true),
		new DataField(field.Public, "PhotoID", DataField.TYPE_BOOL, false, false, false), //field function changed
		new DataField(field.UID, "UID", DataField.TYPE_TEXT, false, false, false),
	};
	

	//methods
	public DataTableWaypoints(Database dataBase)
	{
		super(dataBase, sTableName, vecTableDefinition);
		rows = new RowSet(this);
	}

	public DataValues getNewValues(boolean bForInsert)
	{
		final DataValues values = new DataValues(this);
		
		if (bForInsert)
		{			
			values.setValue(DataTableGeoPoints.field.TrackID, -1);

			values.setValue(field.Altitude, 0);
			values.setValue(field.TimeUTC, 0);
			values.setValue(field.Accuracy, 0);
			values.setValue(field.Speed, 0);
			values.setValueCurrentTime(field.CreationTime);		

			values.setValue(field.UID, UUID.randomUUID().toString());
			values.setValue(field.Public, false);
			values.setValue(field.Sent, false);
		}
		
		return values;
	}
	
	public int getTableVersion()
	{
		return 1;
	}
	
	public UserAlert.Result validateValues(DataValues values)
	{
		//check name
		final String sName = values.getString(field.Name);
		if (sName.length() > NAME_SIZE_LIMIT)
			return UserAlert.Result.NAME_TOO_LONG;

		//check desc
		final String sDescription = values.getString(field.Description);
		if (sDescription.length() > DESC_SIZE_LIMIT)
			return UserAlert.Result.DESC_TOO_LONG;

		//check location
		final double dLon = values.getDouble(field.Lon);
		final double dLat = values.getDouble(field.Lat);
					
		if ((dLon < -180) || (dLon > 180))
			return UserAlert.Result.LON_OUT_OF_RANGE;
		
		if ((dLat < -90) || (dLat > 90))
			return UserAlert.Result.LAT_OUT_OF_RANGE;						
		
		//all ok
		return null;
	}	

	public DataItemWaypoint getWaypointItem(long lWaypointId)
	{		
		final Cursor cr = getDataRecord(lWaypointId);
		if (cr != null)
		{
			DataItemWaypoint item = new DataItemWaypoint(cr);			
			cr.close();
			return item;
		}
		return null;
	}
	
	private ArrayList<Long> getVisibleWaypoints()
	{
		ArrayList<Long> list = new ArrayList<Long>(); 
		
		final String sOrderBy = getFieldName(field.CreationTime) + " desc";
		
		final ArrayList<DataItemWaypoint> items = rows.getRecords(null, sOrderBy);		
		if (items != null)
		{
			for (DataItemWaypoint dataWaypointItem : items)
			{
				list.add(dataWaypointItem.iID);
			}
		}
		
		return list;
	}
	
	public ArrayList<Long> getVisibleWaypointsRotated(long lFirstWaypointId)
	{
		final ArrayList<Long> list = getVisibleWaypoints();
		
		//get selected item position
		int iListIndex = list.indexOf(lFirstWaypointId);
		if (iListIndex != -1)
		{
			//shift items list, selected item to zero position
			Collections.rotate(list, -iListIndex);			
		}
		
		return list;
	}	
	
	public boolean deleteWaypoint(long lWaypointId)
	{
		if (dataDelete(lWaypointId))
		{
	  	sendOperation(lWaypointId, DataTableOperation.OP_DELETE);
			return true;
		}	  
		return false;
	}

	public boolean setUploadStatus(long lWaypointId, boolean bPublic, boolean bSent)
	{
		boolean bSuccess = false;		
		final DataItemWaypoint item = getWaypointItem(lWaypointId);
		if (item != null)
		{
			DataValues values = new DataValues(this);
							
			if (bPublic)
				values.setValue(field.Public, true);
			
			values.setValue(field.Sent, bSent);
				
			bSuccess = dataUpdate(values, lWaypointId);
			
			if (bSuccess)
			{
		  	sendOperation(lWaypointId, DataTableOperation.OP_UPDATE);
			}	  	 		
		}
		return bSuccess;
	}
	
	public long addWaypoint(CommonActivity parent, DataItemWaypoint item)
	{
		final DataValues values = item.getDataValues(this);			
		if (values.valuesComplete())
		{
			final UserAlert.Result validationResult = this.validateValues(values);
			if (validationResult == null)
			{
				final long lRowID = this.dataInsert(values);
				if (lRowID == -1)
				{
					UserAlert.show(parent, UserAlert.Result.DATA_INSERT_ERROR);
				} else {
					return lRowID;
				}
			} else {
				UserAlert.show(parent, validationResult);
			}
		} else {
			UserAlert.show(parent, UserAlert.Result.ENTER_ALL_DATA);
		}
		return -1;
	}
	
}
