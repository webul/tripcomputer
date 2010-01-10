package pl.tripcomputer.data.common;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.Command;
import pl.tripcomputer.UserAlert;
import android.database.Cursor;


public abstract class DataTable extends Observable
{
	//fields
	public static final String sID = "_ID";

	public static class field
	{
		public static final int ID = 0;
	}
	
	
	//fields
	protected Database dataBase = null;
	protected String sTableName = null;
	protected DataField[] vecTableDefinition = null;	

	private boolean bTableExists = false;

	
	//check for correct values before update
	public abstract UserAlert.Result validateValues(DataValues values);
	
	//get code current version of table structure
	public abstract int getTableVersion();

	//get default values for insert
	public abstract DataValues getNewValues(boolean bForInsert);

	
	//methods
	public DataTable(Database dataBase, String sTableName, DataField[] vecTableDefinition)
	{
		this.dataBase = dataBase;
		this.sTableName = sTableName;
		this.vecTableDefinition = vecTableDefinition;
	}

	public String getTableName()
	{
		return sTableName;
	}
	
	public String getFieldName(int id)
	{
		return vecTableDefinition[id].sFieldName;		
	}
	
	private String getSqlTableDefinition()
	{
		String def = "CREATE TABLE " + sTableName + " (";
		for (int i = 0; i < vecTableDefinition.length; i++)
		{
			final DataField field = vecTableDefinition[i];			
			def += field.getColumnDefinition();
			if (i < (vecTableDefinition.length - 1))
				def += ", ";
		}
		def += ")";
		return def;
	}
	
	public boolean createTable()
	{
		if (dataBase.tableExists(sTableName))
		{
			bTableExists = true;
		} else {
			final String sql = getSqlTableDefinition();
			bTableExists = dataBase.executeSQL(sql);
			
			if (bTableExists)
				dataBase.tableVersion().setVersion(sTableName, getTableVersion());
		}

		return bTableExists;
	}	

	public boolean checkExistence()
	{
		if (dataBase.tableExists(sTableName))
		{
			bTableExists = true;
		}
		return bTableExists;
	}	
	
	public boolean exists()
	{
		return bTableExists;
	}	
	
  public long dataInsert(DataValues values)
  {  	
		final long lNewRowId = dataBase.insert(sTableName, values.get()); 
		return lNewRowId;
  }
  
	public boolean dataUpdate(DataValues values, long lRowId)
	{
		final String sWhere = String.format("_ID = %d", lRowId);
 		final int iRowsAffected = dataBase.update(sTableName, values.get(), sWhere);
 		return (iRowsAffected > 0);  		
	}

	public boolean dataDelete(long lRowId)
	{
		final String sWhere = String.format("_ID = %d", lRowId);
 		final int iRowsAffected = dataBase.delete(sTableName, sWhere);
 		return (iRowsAffected > 0);
	}

	public boolean dataDelete(String sWhere)
	{
 		final int iRowsAffected = dataBase.delete(sTableName, sWhere);
 		return (iRowsAffected > 0);
	}
			
	public boolean isRecord(long lRecordId)
	{		
		final Cursor cr = getDataRecord(lRecordId);
		if (cr != null)
		{
			cr.close();		
			return true;
		}
		return false;
	}
	
	public ArrayList<Long> getRowsIdList(String sWhere, String sOrderBy)
	{
		ArrayList<Long> list = new ArrayList<Long>(); 

		final Cursor cr = getDataRecords(sWhere, sOrderBy);
		if (cr != null)
		{
			while (!cr.isAfterLast())
			{
				final Long lID = new Long(cr.getLong(field.ID));
				list.add(lID);
				cr.moveToNext();
			}
			cr.close();
		}
		
		return list;
	}
	
	public Cursor getDataRecords()
	{
		return getDataRecords(null, null);
	}

	public Cursor getDataRecords(String sWhere)
	{
		return getDataRecords(sWhere, null);
	}
		
	public Cursor getDataRecord(long lRowId)
	{
		Cursor cr = null;  		
		if (exists())
		{
			final String sql = String.format("select * from %s where _ID = %d", sTableName, lRowId);				
			cr = dataBase.query(sql);
			if (cr != null)
			{
				if (cr.getCount() == 0)
				{
					cr.close();
					cr = null;
				} else {
					cr.moveToFirst();
				}
			}				
		}  		
		return cr;
	}	
	
	public Cursor getDataRecords(String sWhere, String sOrderBy)
	{
		String sWhereString = "";
		if (sWhere != null)
			sWhereString = "where " + sWhere;
		
		String sOrderByString = "order by _ID";
		if (sOrderBy != null)
			sOrderByString = "order by " + sOrderBy;
		
		Cursor cr = null;
		if (exists())
		{				
			final String sql = String.format("select * from %s %s %s", sTableName, sWhereString, sOrderByString);
			cr = dataBase.query(sql);			
			if (cr != null)
			{
				if (cr.getCount() == 0)
				{
					cr.close();
					cr = null;
				} else {
					cr.moveToFirst();
				}
			}				
  	}
		return cr;			
	}	
	
	public boolean isEmpty()
	{
		int iCount = 0;
		if (exists())
		{				  		
  		final String sql = String.format("select * from %s", sTableName);
  		final Cursor cr = dataBase.query(sql);
  		if (cr != null)
  		{
  			iCount = cr.getCount();
 				cr.close();
  		}
		}
		return (iCount == 0);
	}

	public int getCount(String sWhere)
	{
		String sWhereString = "";
		if (sWhere != null)
			sWhereString = "where " + sWhere;
		
		int iCount = 0;
		if (exists())
		{				  		
  		final String sql = String.format("select * from %s %s", sTableName, sWhereString);
  		final Cursor cr = dataBase.query(sql);
  		if (cr != null)
  		{
  			iCount = cr.getCount();
 				cr.close();
  		}
		}
		return iCount;
	}
	
	public int getCount()
	{
		return getCount(null);
	}

	//sends data operation to observers
	public void sendOperation(long lRowId, int iDataOperation)
	{	
		sendOperation(lRowId, iDataOperation, Command.CMD_NONE, null);
	}
	
	//sends data operation to observers with command and trackname
	public void sendOperation(long lRowId, int iDataOperation, int iCommand, String sTrackName)
	{	
		//do not notify observers in middle of transaction
		if (dataBase.isInTransaction())
			return;
		
		DataTableOperation op = new DataTableOperation();
		op.lRowId = lRowId;
		op.iOperation = iDataOperation;
		op.iCommand = iCommand;
		op.sTrackName = sTrackName;
		
		setChanged();	
		notifyObservers(op);
	}

	public void updateObserver(Observer observer)
	{
	  deleteObserver(observer);
	  addObserver(observer);		
	}
	
}
