package pl.tripcomputer.common;

import pl.tripcomputer.CommandData;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataValues;
import android.database.Cursor;


public class CommonActivityTable
{
	//data transfer interface from/to controls
	public interface DataControlsTransfer
	{		
		//implement to reading values from controls for table row update
		public DataValues getControlValuesForUpdate(DataTable table);
		
		//implement to set controls values from current row for view/edit 
		public void setControlValuesForView(DataValues values);
	}
	
	//fields
	private CommonActivity parent = null;
  private DataTable dataTable = null;
  private CommandData cmdStartData = null;
	

	//methods
	public CommonActivityTable(CommonActivity parent)
	{
		this.parent = parent;
	}
	
	public void setData(DataTable table, CommandData cmdData)
	{
		this.dataTable = table;
		this.cmdStartData = cmdData;
	}
	
	private DataValues getValuesForOperation(DataTable table)
	{
		try
		{
			final DataValues values = parent.getControlValuesForUpdate(table);
			if (values == null)
			{
				UserAlert.show(parent.mContext, UserAlert.Result.DATA_VALUES_ERROR);				
			}			
			return values;
		} catch (Exception e) {
			UserAlert.show(parent.mContext, UserAlert.Result.WRONG_VALUES);			
		}
		return null;
	}

	public long getRowIdForOperation()
	{
		final long lRowId = cmdStartData.getRowId();
		if (lRowId == -1)
		{
			UserAlert.show(parent.mContext, UserAlert.Result.MORE_DATA_REQUIRED);
		}
		return lRowId;
	}
	
	public long dataInsert()
	{
		final DataTable table = dataTable;
				
		if (table != null)
		{
			final DataValues values = getValuesForOperation(table);
			if (values != null)
			{
				if (values.valuesComplete())
				{
					final UserAlert.Result validationResult = table.validateValues(values);
					if (validationResult == null)
					{					
						final long lRowID = table.dataInsert(values);
						if (lRowID == -1)
						{
							UserAlert.show(parent.mContext, UserAlert.Result.DATA_INSERT_ERROR);
						} else {
							return lRowID;
						}
					} else {
						UserAlert.show(parent.mContext, validationResult);
					}
				} else {
					UserAlert.show(parent.mContext, UserAlert.Result.ENTER_ALL_DATA);
				}
			}
		}
		return -1;
	}	

	public boolean dataUpdate()
	{
		final DataTable table = dataTable;
		
		if (table != null)
		{			
			final DataValues values = getValuesForOperation(table);
			if (values != null)
			{
				final long lRowId = getRowIdForOperation();				
				if (lRowId != -1)
				{
					if (values.valuesComplete())
					{
						final UserAlert.Result validationResult = table.validateValues(values);
						if (validationResult == null)
						{							
							if (table.dataUpdate(values, lRowId))
							{
								return true;
							} else {
								UserAlert.show(parent.mContext, UserAlert.Result.DATA_UPDATE_ERROR);
							}
						} else {
							UserAlert.show(parent.mContext, validationResult);							
						}
					} else {
						UserAlert.show(parent.mContext, UserAlert.Result.ENTER_ALL_DATA);
					}
				}
			}
		}
		return false;
	}	
	
	public boolean getDataForView()
	{
		final DataTable table = dataTable;
		
		if (table != null)
		{
			final long lRowId = getRowIdForOperation();
			if (lRowId != -1)
			{
				final Cursor inputCursor = table.getDataRecord(lRowId);
				
				if (inputCursor == null)
				{
					UserAlert.show(parent.mContext, UserAlert.Result.DATA_VALUES_ERROR);
					return false;
				}
				
				final DataValues values = new DataValues(table, inputCursor);
				
				parent.setControlValuesForView(values);
				
				return true;
			}
		}
		return false;
	}
	
}
