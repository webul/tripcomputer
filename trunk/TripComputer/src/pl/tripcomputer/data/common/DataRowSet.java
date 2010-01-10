package pl.tripcomputer.data.common;

import java.util.ArrayList;

import android.database.Cursor;


public abstract class DataRowSet<T>
{	
	//fields
	private DataTable table = null;
	
	
	//methods
	public DataRowSet(DataTable table)
	{
		this.table = table;
	}
	
	public T getRecord(long lRowId)
	{		
		final Cursor cr = table.getDataRecord(lRowId);
		if (cr != null)
		{
			final T item = getItem(cr);
			cr.close();
			return item;
		}
		return null;
	}
	
	public ArrayList<T> getRecords()
	{
		return getRecords(null, null);
	}

	public ArrayList<T> getRecords(String sWhere)
	{
		return getRecords(sWhere, null);
	}
	
	public ArrayList<T> getRecords(String sWhere, String sOrderBy)
	{
		final Cursor cr = table.getDataRecords(sWhere, sOrderBy);
		if (cr != null)
		{
			final ArrayList<T> items = new ArrayList<T>();			
			while (!cr.isAfterLast())
			{
				items.add(getItem(cr));				
				cr.moveToNext();
			}
			cr.close();
			return items;
		}				
		return null;
	}

	
	protected abstract T getItem(final Cursor cr);

}
