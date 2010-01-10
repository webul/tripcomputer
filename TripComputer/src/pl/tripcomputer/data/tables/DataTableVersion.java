package pl.tripcomputer.data.tables;

import android.database.Cursor;
import pl.tripcomputer.UserAlert.Result;
import pl.tripcomputer.data.common.DataField;
import pl.tripcomputer.data.common.DataRowSet;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.data.items.DataItemVersion;
import pl.tripcomputer.data.items.DataItemWaypoint;


public class DataTableVersion extends DataTable
{
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
	public final static String sTableName = "Version";

	public static class field
	{
		public static final int ID = 0;
		public static final int TableName = 1;
		public static final int Version = 2;
	};
	
	private final static DataField[] vecTableDefinition =
	{
		new DataField(field.ID, DataTable.sID, DataField.TYPE_INT, false, true, false),
		new DataField(field.TableName, "Lon", DataField.TYPE_TEXT, false, false, false),
		new DataField(field.Version, "Lat", DataField.TYPE_INT, false, false, false),
	};
	

	//methods
	public DataTableVersion(Database dataBase)
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
	
	public Result validateValues(DataValues values)
	{
		//all ok
		return null;
	}

	private DataItemVersion getVersionItem(String sExtTableName)
	{		
		final String sWhere = String.format("%s = \"%s\"", getFieldName(field.TableName), sExtTableName);
		
		final Cursor cr = getDataRecords(sWhere);
		if (cr != null)
		{
			DataItemVersion item = new DataItemVersion(cr);
			cr.close();
			return item;
		}
		
		return null;
	}
		
	public boolean setVersion(String sExtTableName, int iVersion)
	{
		boolean bSuccess = false;
		
		if (sExtTableName.equals(sTableName))
			return true;
		
		final DataItemVersion item = getVersionItem(sExtTableName);
		if (item == null)
		{	
			DataValues values = new DataValues(this);			
			values.setValue(field.TableName, sExtTableName);
			values.setValue(field.Version, iVersion);
			bSuccess = (dataInsert(values) != -1);
		} else {
			DataValues values = new DataValues(this);			
			values.setValue(field.TableName, sExtTableName);
			values.setValue(field.Version, iVersion);
			bSuccess = dataUpdate(values, item.iID);
		}
		
		return bSuccess;
	}
	
	public boolean isVersionChanged(String sExtTableName, int iVersion)
	{
		boolean bResult = false;		
		final DataItemVersion item = getVersionItem(sExtTableName);
		if (item != null)
		{		
			if (item.getVersion() != 0)
				if (item.getVersion() < iVersion)
					bResult = true;
		}
		return bResult;
	}

}
