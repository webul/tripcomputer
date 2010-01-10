package pl.tripcomputer.data.tables;

import pl.tripcomputer.UserAlert;
import pl.tripcomputer.data.common.DataField;
import pl.tripcomputer.data.common.DataRowSet;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.data.items.DataItemGeoPoint;
import android.database.Cursor;


public class DataTableGeoPoints extends DataTable
{
	//generic collection class
	public class RowSet extends DataRowSet<DataItemGeoPoint>
	{
		public RowSet(DataTable table)
		{
			super(table);
		}
		protected DataItemGeoPoint getItem(Cursor cr)
		{
			return new DataItemGeoPoint(cr);
		}		
	}

	protected DataRowSet<DataItemGeoPoint> rows = null;
	
	
	//fields
	public final static String sTableName = "GeoPoints";

	public static class field
	{
		public static final int ID = 0;
		public static final int TrackID = 1;
		public static final int Lon = 2;
		public static final int Lat = 3;
		public static final int Altitude = 4;
		public static final int TimeUTC = 5;
		public static final int Accuracy = 6;
		public static final int Speed = 7;
		public static final int Bearing = 8;
		public static final int CreationTime = 9;
	};	
	
	private final static DataField[] vecTableDefinition =
	{
		new DataField(field.ID, DataTable.sID, DataField.TYPE_INT, false, true, false),
		new DataField(field.TrackID, "TrackID", DataField.TYPE_INT, false, false, false),
		new DataField(field.Lon, "Lon", DataField.TYPE_NUMERIC, false, false, true),
		new DataField(field.Lat, "Lat", DataField.TYPE_NUMERIC, false, false, true),
		new DataField(field.Altitude, "Altitude", DataField.TYPE_INT, false, false, false),
		new DataField(field.TimeUTC, "TimeUTC", DataField.TYPE_INT, false, false, false),
		new DataField(field.Accuracy, "Accuracy", DataField.TYPE_INT, true, false, false),
		new DataField(field.Speed, "Speed", DataField.TYPE_INT, true, false, false),
		new DataField(field.Bearing, "Bearing", DataField.TYPE_INT, true, false, false),
		new DataField(field.CreationTime, "CreationTime", DataField.TYPE_INT, false, false, false),		
	};


	//methods
	public DataTableGeoPoints(Database dataBase)
	{
		super(dataBase, sTableName, vecTableDefinition);
		rows = new RowSet(this);
	}

	public DataValues getNewValues(boolean bForInsert)
	{
		final DataValues values = new DataValues(this);
		
		if (bForInsert)
		{		
		}
		
		return values;
	}
	
	public int getTableVersion()
	{
		return 1;
	}
	
	public UserAlert.Result validateValues(DataValues values)
	{
		//all ok
		return null;
	}

	public DataItemGeoPoint getGeoPointItem(long lPointId)
	{		
		final Cursor cr = getDataRecord(lPointId);
		if (cr != null)
		{				
			DataItemGeoPoint item = new DataItemGeoPoint(cr);			
			cr.close();		
			return item;
		}
		return null;
	}
	
	public int getTrackPointCount(long lTrackId)
	{		
		int iCount = 0;		
		final String sWhere = String.format("%s = %d", getFieldName(field.TrackID), lTrackId);		
		final Cursor cr = getDataRecords(sWhere);
		if (cr != null)
		{
			iCount = cr.getCount();
			cr.close();
		}
		return iCount;
	}
	
}
