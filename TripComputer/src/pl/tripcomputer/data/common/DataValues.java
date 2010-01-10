package pl.tripcomputer.data.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentValues;
import android.database.Cursor;


public class DataValues
{
	//fields
	private static TimeZone timeZone = TimeZone.getDefault();
	private SimpleDateFormat dateFormat = new SimpleDateFormat();
	
	//fields
	private DataTable table = null;
	private Cursor inputCursor = null;
	private ContentValues values = new ContentValues();
	
	
	//constructor for insert/update mode (output values)
	public DataValues(DataTable table)
	{
		this.table = table;
	}

	//constructor for edit mode (input values)
	public DataValues(DataTable table, Cursor inputCursor)
	{
		this.table = table;
		this.inputCursor = inputCursor;
		
		getValuesFromCursor();
	}
	
	public ContentValues get()
	{
		return values;
	}
	
	public void clearValues()
	{
		values.clear();
	}
	
	public ContentValues getValues()
	{
		return values;
	}
	
	public void setValue(int fieldID, String sValue)
	{
		values.put(table.getFieldName(fieldID), sValue.trim());
	}

	public void setValue(int fieldID, int iValue)
	{
		values.put(table.getFieldName(fieldID), iValue);
	}
	
	public void setValue(int fieldID, long iValue)
	{
		values.put(table.getFieldName(fieldID), iValue);
	}

	public void setValue(int fieldID, double dValue)
	{
		values.put(table.getFieldName(fieldID), dValue);
	}

	public void setValue(int fieldID, boolean bValue)
	{
		values.put(table.getFieldName(fieldID), bValue);
	}

	public void setValueCurrentTime(int fieldID)
	{
		final Calendar datetime = Calendar.getInstance();
		values.put(table.getFieldName(fieldID), datetime.getTimeInMillis());
	}
		
	//returns true if all required values are present in collection
	public boolean valuesComplete()
	{
		for (int i = 0; i < table.vecTableDefinition.length; i++)
		{
			final DataField field = table.vecTableDefinition[i];
			if (field.bForEdit && (!field.bCanBeNull))
			{
				if (!values.containsKey(field.sFieldName))
					return false;
				
				final String sValue = values.get(field.sFieldName).toString().trim();
				if (sValue.length() == 0)
					return false;
			}
		}		
		return true;
	}

	private void getValuesFromCursor()
	{
		for (int i = 0; i < table.vecTableDefinition.length; i++)
		{
			final DataField field = table.vecTableDefinition[i];
			
			if (field.bPrimaryKey)
				continue;						
			
			final int iColumn = field.getIndex();
			
			if (field.FieldType == DataField.TYPE_INT)
			{				
				values.put(field.sFieldName, inputCursor.getLong(iColumn));
			}		
			if (field.FieldType == DataField.TYPE_BOOL)
			{
				values.put(field.sFieldName, (boolean)(inputCursor.getInt(iColumn) == 1));
			}
			if (field.FieldType == DataField.TYPE_NUMERIC)
			{
				values.put(field.sFieldName, inputCursor.getDouble(iColumn));
			}
			if (field.FieldType == DataField.TYPE_TEXT)
			{
				values.put(field.sFieldName, inputCursor.getString(iColumn));
			}
		}		
	}	
	
	public String getString(int fieldID)
	{
		return values.getAsString(table.getFieldName(fieldID));		
	}

	public int getInteger(int fieldID)
	{
		return values.getAsInteger(table.getFieldName(fieldID));		
	}
	
	public long getLong(int fieldID)
	{
		return values.getAsLong(table.getFieldName(fieldID));		
	}

	public boolean getBool(int fieldID)
	{
		return values.getAsBoolean(table.getFieldName(fieldID));		
	}

	public double getDouble(int fieldID)
	{
		return values.getAsDouble(table.getFieldName(fieldID));		
	}
	
	public long getDateTime(int fieldID)
	{
		return getLong(fieldID) + timeZone.getRawOffset();
	}

	public Date getDateTimeAsDate(int fieldID)
	{
		final long lTimeMs = getLong(fieldID) + timeZone.getRawOffset();				
		return new Date(lTimeMs);
	}
	
	public String getDateTimeAsString(int fieldID)
	{
		final long lTimeMs = getLong(fieldID) + timeZone.getRawOffset();				
		return dateFormat.format(new Date(lTimeMs));
	}
	
}
