package pl.tripcomputer;

import java.util.Set;

import android.os.Bundle;


public class CommandData
{
	//fields
	private final static String KEY_ROWID = "KEY_ROWID"; 
	private final static String KEY_MODE = "KEY_MODE";
		
	public final static int MODE_NONE = 0; 
	public final static int MODE_VIEW = 1; 
	public final static int MODE_EDIT = 2; 
	public final static int MODE_INSERT = 3;
	
	//fields
	private Bundle data = null;


	//methods
	public CommandData(final int iMode)
	{
		this.data = new Bundle();		
		data.putInt(KEY_MODE, iMode);
		data.putLong(KEY_ROWID, -1);
	}
	
	public CommandData(final int iMode, final long lRowID)
	{
		this.data = new Bundle();		
		data.putInt(KEY_MODE, iMode);
		data.putLong(KEY_ROWID, lRowID);
	}

	public CommandData(Bundle data)
	{
		if (data == null)
		{
			this.data = new Bundle();
		} else {
			this.data = data;
		}
	}

	public static CommandData setModeEdit(CommandData data)
	{
		if (data == null)
		{
			return new CommandData(MODE_EDIT);
		} else { 
			data.data.putInt(KEY_MODE, MODE_EDIT);
			return data;
		}
	}

	public static CommandData setModeInsert(CommandData data)
	{
		if (data == null)
		{
			return new CommandData(MODE_INSERT);
		} else { 
			data.data.putInt(KEY_MODE, MODE_INSERT);
			return data;
		}
	}

	public static CommandData setModeView(CommandData data)
	{
		if (data == null)
		{
			return new CommandData(MODE_VIEW);
		} else { 
			data.data.putInt(KEY_MODE, MODE_VIEW);
			return data;
		}
	}
	
	public Bundle get()
	{
		return data;
	}
	
	public void setRowId(long lRowID)
	{
		data.putLong(KEY_ROWID, lRowID);
	}
	
	public long getRowId()
	{
		if (data.containsKey(KEY_ROWID))
			return data.getLong(KEY_ROWID);
		else
			return -1;
	}
	
	public void setModeEdit()
	{
		data.putInt(KEY_MODE, MODE_EDIT);
	}

	public void setModeInsert()
	{
		data.putInt(KEY_MODE, MODE_INSERT);
	}

	public void setModeView()
	{
		data.putInt(KEY_MODE, MODE_VIEW);
	}
	
	public boolean isViewMode()
	{
		if (data.containsKey(KEY_MODE))
			return (data.getInt(KEY_MODE) == MODE_VIEW);
		return false;
	}

	public boolean isEditMode()
	{
		if (data.containsKey(KEY_MODE))
			return (data.getInt(KEY_MODE) == MODE_EDIT);
		return false;
	}

	public boolean isInsertMode()
	{
		if (data.containsKey(KEY_MODE))
			return (data.getInt(KEY_MODE) == MODE_INSERT);
		return false;
	}
	
	public String toString()
	{
		final Set<String> keys = data.keySet();
		
		String items = "";
		for (String sKey : keys)
		{
			items += sKey + ": " + data.get(sKey) + "\n";						
		}
		
		return items;		
	}

	public void setValue(String sKey, int iValue)
	{
		data.putInt(sKey, iValue);		
	}

	public void setValue(String sKey, boolean bValue)
	{
		data.putBoolean(sKey, bValue);
	}

	public int getValueInt(String sKey)
	{
		if (data.containsKey(sKey))
			return data.getInt(sKey);
		else
			return -1;
	}
	
}
