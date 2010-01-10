package pl.tripcomputer;

import pl.tripcomputer.data.common.DataTable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;


public class StateBundle
{
	//fields
	private Bundle state = null;
	private DataTable table = null;
	
	
	//methods
	public StateBundle(DataTable table, Bundle outState)
	{
		this.table = table;
		this.state = outState;
	}

	public StateBundle(Bundle startupState)
	{
		if (startupState != null)
		{
			this.state = (Bundle)startupState.clone();
		}
	}
	
	public void setDataTable(DataTable table)
	{
		this.table = table;
	}
	
	public void set(int iFieldId, String sValue)
	{
		if ((state != null) && (table != null))
			state.putString(table.getFieldName(iFieldId), sValue);
	}

	public void set(int iFieldId, int iValue)
	{
		if ((state != null) && (table != null))
			state.putInt(table.getFieldName(iFieldId), iValue);
	}
	
	public void set(int iFieldId, EditText ed)
	{
		set(iFieldId, ed.getText().toString());
	}

	public void setPosition(int iFieldId, Spinner sp)
	{
		int index = sp.getSelectedItemPosition();
		if (index == Spinner.INVALID_POSITION)
			index = 0;
		set(iFieldId, index);
	}
		
	public boolean isStartupState()
	{
		return ((state != null) && (table != null));
	}
	
	public String getString(int iFieldId)
	{
		if ((state != null) && (table != null))
			state.getString(table.getFieldName(iFieldId));		
		return "";
	}

	public int getInt(int iFieldId)
	{
		if ((state != null) && (table != null))
			state.getInt(table.getFieldName(iFieldId));		
		return -1;
	}
	
}
