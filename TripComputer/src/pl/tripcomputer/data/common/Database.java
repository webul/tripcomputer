package pl.tripcomputer.data.common;

import pl.tripcomputer.UserAlert;
import pl.tripcomputer.data.tables.DataTableGeoPoints;
import pl.tripcomputer.data.tables.DataTableTracks;
import pl.tripcomputer.data.tables.DataTableVersion;
import pl.tripcomputer.data.tables.DataTableWaypoints;

import java.util.ArrayList;
import android.content.*;
import android.database.sqlite.*;
import android.database.*;


public class Database
{
  //fields
	private static final String dbName = "TripComputer.db";
		
	private boolean bCreateTables = false;
	private SQLiteDatabase dbSQL = null;

	private ArrayList<DataTable> vecTables = new ArrayList<DataTable>();
	
	//tables
	private DataTableVersion tableVersion = null;	
	private DataTableTracks tableTracks = null;
	private DataTableGeoPoints tableGeoPoints = null;
	private DataTableWaypoints tableWaypoints = null;	
	
	
	//methods
	public Database(Context context, boolean bCreateTables)
	{
		this.bCreateTables = bCreateTables;
				
		try
		{
			dbSQL = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
			
			initialize();
			
			createTables();
			
		} catch (SQLiteException e) {			
			dbSQL = null;
			UserAlert.show(context, UserAlert.Result.DB_ACCESS_ERROR);
		}
	}	
		
	public void close()
	{
		if (dbSQL != null)
			dbSQL.close();
	}
	
	public boolean isOpen()
	{		
		boolean bValue = false;
		synchronized(dbSQL)
		{
			bValue = dbSQL.isOpen();
		}
		return bValue;
	}
	
	public boolean isInTransaction()
	{
		boolean bValue = false;
		synchronized(dbSQL)
		{		
			bValue = dbSQL.inTransaction();
		}
		return bValue;
	}
		
	private void initialize()
	{
		tableVersion = new DataTableVersion(this);
		tableTracks = new DataTableTracks(this);
		tableGeoPoints = new DataTableGeoPoints(this);
		tableWaypoints = new DataTableWaypoints(this);

		vecTables.add(tableTracks);
		vecTables.add(tableGeoPoints);
		vecTables.add(tableWaypoints);
	}

	private void createTables()
	{
		if (bCreateTables)
		{
			//version table first
			tableVersion.createTable();
			
			for (DataTable table : vecTables)
				table.createTable();
		} else {
			//version table first
			tableVersion.checkExistence();
			
			for (DataTable table : vecTables)
				table.checkExistence();
		}
	}

	public DataTableVersion tableVersion()
	{
		return tableVersion;
	}
	
	public DataTableTracks tableTracks()
	{
		return tableTracks;
	}

	public DataTableGeoPoints tableGeoPoints()
	{
		return tableGeoPoints;
	}

	public DataTableWaypoints tableWaypoints()
	{
		return tableWaypoints;
	}
	
	public boolean tableExists(String sTableName)
	{
		boolean bResult = false;

		String sql = "select name from sqlite_master where type = 'table' and name = '%s'";		
		sql = String.format(sql, sTableName);

		Cursor cr = null;
		synchronized(dbSQL)
		{
			cr = dbSQL.rawQuery(sql, null);
		}
		
		if (cr != null)
		{
			if (cr.getCount() > 0)
				bResult = true;
			cr.close();
		}
		
		return bResult;
	}	
	
	public boolean executeSQL(String sql)
	{
		try
		{			
			synchronized(dbSQL)
			{
				dbSQL.execSQL(sql);
			}
			return true;
		} catch (SQLiteException e) {
			return false;
		}
	}	
	
	public long insert(String sTableName, ContentValues values)
	{
		long lNewRowId = -1;
		synchronized(dbSQL)
		{
			lNewRowId = dbSQL.insert(sTableName, null, values);
		}		
		return lNewRowId;
	}
	
	public int update(String sTableName, ContentValues values, String sWhere)
	{
		int iRowsUpdated = 0;
		synchronized(dbSQL)
		{
			iRowsUpdated = dbSQL.update(sTableName, values, sWhere, null);
		}		
		return iRowsUpdated;
	}
	
	public int delete(String sTableName, String sWhere)
	{	
		int iRowsDeleted = 0;
		synchronized(dbSQL)
		{
			iRowsDeleted = dbSQL.delete(sTableName, sWhere, null);
		}		
		return iRowsDeleted;
	}

	public Cursor query(String sSQL)
	{
		Cursor cr = null;
		synchronized(dbSQL)
		{
			cr = dbSQL.rawQuery(sSQL, null);			
		}
		return cr;
	}
	
	public void transactionBegin()
	{
		synchronized(dbSQL)
		{
			dbSQL.beginTransaction();
		}		
	}

	public void transactionCommit()
	{
		synchronized(dbSQL)
		{
			dbSQL.setTransactionSuccessful();
		}		
	}
	
	public void transactionEnd()
	{
		synchronized(dbSQL)
		{
			dbSQL.endTransaction();
		}		
	}
	
}
