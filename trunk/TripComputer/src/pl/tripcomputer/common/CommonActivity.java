package pl.tripcomputer.common;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.CommandData;
import pl.tripcomputer.Main;
import pl.tripcomputer.MainState;
import pl.tripcomputer.Preferences;
import pl.tripcomputer.StateBundle;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.R;
import pl.tripcomputer.data.common.DataItem;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataTableOperation;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.data.tables.DataTableTracks;
import pl.tripcomputer.data.tables.DataTableWaypoints;
import pl.tripcomputer.service.ServiceCommand;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public abstract class CommonActivity
	extends Activity
	implements
		CommonActivityTable.DataControlsTransfer,
		CommonActivityButtons.ButtonEvents,
		Observer
{
	//fields
	public final static int REQUEST_CODE_DEFAULT = 0;
	public final static String ACTIVITIES_PACKAGE = ".activities.";
	
	//common data
  protected Context mContext = null;  
  protected String sAppTitle = null;
    
  //data
  protected Database dataBase = null;
  protected MainState state = null;
  protected Main main = null;
  protected Preferences prefs = null;
  
  //start command data for this activity
  protected CommandData cmdStartData = null;

  //bundle data state returned from create
  protected StateBundle savedState = null;
  
  //activity table helper for data edit
  protected CommonActivityTable activityTable = null;
    
  //activity common buttons helper
  protected CommonActivityButtons activityButtons = null;
  
	
	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  
	  //common reference
	  mContext = this;
	  	  
	  //main objects
	  dataBase = new Database(this, true);
	  state = new MainState(this, dataBase);
	  main = new Main(this);
	  prefs = new Preferences(this);
		
		//command ui data
	  cmdStartData = getCommandDataFromIntent();
	  
	  //activity state
	  savedState = new StateBundle(savedInstanceState);
		
	  //common helpers
	  activityTable = new CommonActivityTable(this);
	  activityButtons = new CommonActivityButtons(this);
	  
	  //main title
	  sAppTitle = getResString(R.string.app_name);	  
	}	
	
	protected void onStart()
	{
	  //load updated settings
	  prefs.load();
	  
	  //initialize global types
		DataItem.init(dataBase);

	  //watch for state change in loader collection
		dataBase.tableTracks().updateObserver(ActivityMain.loader.tracks());
		dataBase.tableWaypoints().updateObserver(ActivityMain.loader.waypoints());
	  
	  //watch for state change in this activity
		dataBase.tableTracks().updateObserver(this);
		dataBase.tableWaypoints().updateObserver(this);
		
		//update current main state
		state.update();
		
		//default buttons
		activityButtons.initialize();

		//get data to view/edit
	  if (cmdStartData.isViewMode() || cmdStartData.isEditMode())
	  {	  	
	  	activityTable.getDataForView();
	  }
	  
		super.onStart();
	}
	
	protected void onDestroy()
	{
		dataBase.close();
		
		super.onDestroy();
	}
	
	public void setData(DataTable table)
	{
	  activityTable.setData(table, cmdStartData);
	  savedState.setDataTable(table);
	}
	
	public Main getMain()
	{
		return main;
	}
	
	public Database getDatabase()
	{
		return dataBase;
	}

	public MainState getMainState()
	{
		return state;
	}

	public Preferences getPrefs()
	{
		return prefs;
	}
	
	public String getResString(int res_string_id)
	{
		return mContext.getResources().getString(res_string_id);
	}
	
	public void setItemsForSpinner(Spinner spinner, String[] vecItems)
	{
	  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vecItems);	  
	  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	  spinner.setAdapter(adapter);
	}
	
	public void setSubTitle(int res_string_id)
	{
		setSubTitle(getResString(res_string_id));
	}
		
	public void setSubTitle(String sSubTitle)
	{
		if (sSubTitle == null)
			this.setTitle(sAppTitle);
		if (sSubTitle.length() == 0)
			this.setTitle(sAppTitle);
			
		this.setTitle(sAppTitle + " - " + sSubTitle);
	}

	private CommandData getCommandDataFromIntent()
	{
	  Intent it = getIntent();
	  if (it != null)
	  {
	  	Bundle data = it.getExtras();
	  	if (data != null)
	  	{	  			  		
	  		return new CommandData(data);
	  	}
	  }
	  return new CommandData(CommandData.MODE_NONE);
	}
		
	public void showActivity(Class<?> classToShow, int iRequestCode, CommandData data)
	{
		final String sPackage = getPackageName();

		Intent it = new Intent(Intent.ACTION_DEFAULT);
		
		it.setClassName(this, sPackage + ACTIVITIES_PACKAGE + classToShow.getSimpleName());			

		if (data != null)
			it.putExtras(data.get());

		this.startActivityForResult(it, iRequestCode);
	}
	
	public void showActivity(Class<?> classToShow, CommandData data)
	{
		showActivity(classToShow, REQUEST_CODE_DEFAULT, data);
	}

	public void showActivity(Class<?> classToShow)
	{
		showActivity(classToShow, REQUEST_CODE_DEFAULT, null);
	}
	
	public void closeActivityWithResult(int iResultCode, Bundle data)
	{
		Intent it = new Intent(Intent.ACTION_DEFAULT);
		
		if (data != null)
			it.putExtras(data);
		
    setResult(iResultCode, it);
    finish();			
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_CANCELED)
		{			
			onRequestedActivityResultCancel(requestCode, data);
		}
		if (resultCode == Activity.RESULT_OK)
		{
			onRequestedActivityResultOK(requestCode, data);
		}
		if (resultCode == Activity.RESULT_FIRST_USER + 1)
		{
		}
	}

	public int getItemIndexSelectedForContextMenu(ContextMenuInfo menuInfo)
	{
		AdapterView.AdapterContextMenuInfo info;
    try
    {
    	info = (AdapterView.AdapterContextMenuInfo)menuInfo;    	
    	return info.position;
    } catch (ClassCastException e) {
    	return -1;
    }
	}
	
	
	//override to get ok result from started activity
	protected void onRequestedActivityResultOK(int requestCode, Intent data)
	{
	}
	
	//override to get cancel result from started activity
	protected void onRequestedActivityResultCancel(int requestCode, Intent data)
	{
	}
	
	//ActivityButtons interface method
	public boolean onClickedDone(Bundle data)
	{	
		return true;
	}
	
	//ActivityButtons interface method
	public boolean onClickedRevert(Bundle data)
	{
		return true;
	}

	public void closeActivityWithResultOK()
	{
		closeActivityWithResultOK(new Bundle());
	}

	public void closeActivityWithResultCancel()
	{
		closeActivityWithResultCancel(new Bundle());
	}
	
	//ActivityButtons interface method
	public void closeActivityWithResultOK(Bundle data)
	{
		closeActivityWithResult(Activity.RESULT_OK, data);
	}

	//ActivityButtons interface method
	public void closeActivityWithResultCancel(Bundle data)
	{
		closeActivityWithResult(Activity.RESULT_CANCELED, data);		
	}
	
	//ActivityTable interface method; override to reading values from controls for update
	public DataValues getControlValuesForUpdate(DataTable table)
	{
		return null;
	}
	
	//ActivityTable interface method; override to set controls values from current row for view/edit 
	public void setControlValuesForView(DataValues values)
	{
	}
	
	public void update(Observable observable, Object data)
	{
		DataTableOperation op = null;		
		if (data instanceof DataTableOperation)
			op = (DataTableOperation)data;
		
		//watch for tracks data table changes
		if (observable instanceof DataTableTracks)
		{						
			if (op != null)
			{				
				//on track update
				if (op.iOperation == DataTableOperation.OP_UPDATE)
				{
					//reload state
					state.update();

					//update service state
					ServiceCommand.send(op.iCommand, op.lRowId, op.sTrackName);					
				}
				//on track insert
				if (op.iOperation == DataTableOperation.OP_INSERT)
				{
					//reload state
					state.update();					
				}
				//on track delete
				if (op.iOperation == DataTableOperation.OP_DELETE)
				{
					//reload state
					state.update();
				}
				
				//send event to main
				if (!(this instanceof ActivityMain))
					ActivityMain.onChangeTableState(op, DataTableTracks.sTableName);
			}
		}
		
		//watch for waypoints table changes
		if (observable instanceof DataTableWaypoints)
		{
			if (op != null)
			{
				//on track update
				if (op.iOperation == DataTableOperation.OP_UPDATE)
				{					
				}
				
				//send event to main
				if (!(this instanceof ActivityMain))
					ActivityMain.onChangeTableState(op, DataTableWaypoints.sTableName);
			}
		}		
	}
	
}
