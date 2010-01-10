package pl.tripcomputer.data.items;

import pl.tripcomputer.data.common.DataItem;
import pl.tripcomputer.data.tables.DataTableVersion;
import android.database.Cursor;


public class DataItemVersion extends DataItem
{
	//fields		
	private String sTableName = "";
	private int iVersion = 0;
	
	
	//methods
	public DataItemVersion(DataItemVersion item)
	{
		this.iID = item.iID;
		this.sTableName = item.sTableName;
		this.iVersion = item.iVersion;
	}
	
	public DataItemVersion(final Cursor cr)
	{
		this.iID = cr.getLong(DataTableVersion.field.ID);
		this.sTableName = cr.getString(DataTableVersion.field.TableName);
		this.iVersion = cr.getInt(DataTableVersion.field.Version);
	}
	
	public String getTableName()
	{
		return sTableName;
	}

	public int getVersion()
	{
		return iVersion;
	}
	
}
