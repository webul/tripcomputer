package pl.tripcomputer.activities;

import pl.tripcomputer.StateBundle;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataTableOperation;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.items.DataItemTrack;
import pl.tripcomputer.data.tables.DataTableTracks;
import android.os.Bundle;
import android.widget.*;


public class ActivityNewTrack extends CommonActivity
{
	//fields
  private EditText edName = null;
  private EditText edDescription = null;
  private CheckBox chkTrackTypeWalk = null;
  private CheckBox chkTrackTypeBike = null;
  
  
	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_new_track);
	  
	  setData(dataBase.tableTracks());
	  
	  if (cmdStartData.isEditMode())
	  	setSubTitle(R.string.title_trackedit);
	  
	  if (cmdStartData.isInsertMode())
	  	setSubTitle(R.string.title_newtrack);	  

	  edName = (EditText)this.findViewById(R.id.EditTrackName);
	  edDescription = (EditText)this.findViewById(R.id.EditTrackDescription);	  
	  chkTrackTypeWalk = (CheckBox)this.findViewById(R.id.chkTrackTypeWalk);
	  chkTrackTypeBike = (CheckBox)this.findViewById(R.id.chkTrackTypeBike);
	}
		
	public DataValues getControlValuesForUpdate(DataTable table)
	{
		final boolean bForInsert = cmdStartData.isInsertMode();
		final DataValues values = table.getNewValues(bForInsert);
		
		final String sTrackName = edName.getText().toString();
		final String sTrackDescription = edDescription.getText().toString();
		final int iType = DataItemTrack.boolToType(chkTrackTypeWalk.isChecked(), chkTrackTypeBike.isChecked());
		
		values.setValue(DataTableTracks.field.Name, sTrackName);
		values.setValue(DataTableTracks.field.Description, sTrackDescription);
		values.setValue(DataTableTracks.field.Type, iType);
			  
		return values;
	}

	public void setControlValuesForView(DataValues values)
	{
		String sName = values.getString(DataTableTracks.field.Name);
		String sDescription = values.getString(DataTableTracks.field.Description);
		int iType = values.getInteger(DataTableTracks.field.Type);

		if (savedState.isStartupState())
		{
			sName = savedState.getString(DataTableTracks.field.Name);
			sDescription = savedState.getString(DataTableTracks.field.Description);
			iType = savedState.getInt(DataTableTracks.field.Type);
		}
		
		edName.setText(sName);
		edDescription.setText(sDescription);

		chkTrackTypeWalk.setChecked(DataItemTrack.isTypeWalk(iType));
		chkTrackTypeBike.setChecked(DataItemTrack.isTypeBike(iType));
	}
	
	public boolean onClickedDone(Bundle data)
	{
		//insert new record
	  if (cmdStartData.isInsertMode())
	  	return insertNewTrack();
	  
	  //update old record
	  if (cmdStartData.isEditMode())
	  	return updateTrack();
	  
	  return false;
	}
	
	public boolean onClickedRevert(Bundle data)
	{
		return true;		
	}
	
	public boolean insertNewTrack()
	{
		boolean bSuccess = false;
		
		long lOldTrackId = -1;
		long lNewTrackId = -1;

		final DataItemTrack itemOpenTrack = dataBase.tableTracks().getOpenTrackItem();
		if (itemOpenTrack != null)
			lOldTrackId = itemOpenTrack.iID;
						
		dataBase.transactionBegin();
	  try
	  {	  	
	  	if (dataBase.tableTracks().closeOpenTrack())
	  	{
	  		lNewTrackId = activityTable.dataInsert();
		  	if (lNewTrackId != -1)
		  	{
		  		if (dataBase.tableTracks().resumeRecording(lNewTrackId))
		  		{
		  			dataBase.transactionCommit();
			      bSuccess = true;
		  		} else {
						UserAlert.show(this, UserAlert.Result.RESUME_RECORDING_ERROR);
		  		}
		  	}
	  	} else {
				UserAlert.show(this, UserAlert.Result.CLOSE_TRACK_ERROR);
	  	}	  
	  } finally {
	  	dataBase.transactionEnd();
	  }
	  
		//update observers
		if (bSuccess)
		{
			dataBase.tableTracks().sendOperationForNewTrack(lOldTrackId, lNewTrackId);
		}
	  	 
	  return bSuccess;
	}

	public boolean updateTrack()
	{
		final boolean bSuccess = activityTable.dataUpdate();
		
		if (bSuccess)
		{
			final long lRowId = cmdStartData.getRowId();
			dataBase.tableTracks().sendOperation(lRowId, DataTableOperation.OP_UPDATE);
		}
		
		return bSuccess;
	}

	protected void onSaveInstanceState(Bundle outState)
	{		
		StateBundle state = new StateBundle(dataBase.tableTracks(), outState);
		
		state.set(DataTableTracks.field.Name, edName);
		state.set(DataTableTracks.field.Description, edDescription);
				
		super.onSaveInstanceState(outState);
	}
	
}
