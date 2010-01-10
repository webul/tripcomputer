package pl.tripcomputer.activities;

import java.util.Observable;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import pl.tripcomputer.Preferences;
import pl.tripcomputer.StateBundle;
import pl.tripcomputer.Utils;
import pl.tripcomputer.WaitDialog;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.custom.EditsGeoPos;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataTableOperation;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.data.tables.DataTableWaypoints;
import pl.tripcomputer.gps.GpsOneShotReader;
import pl.tripcomputer.gps.GpsReader;


public class ActivityAddWaypoint extends CommonActivity
{
	//fields
  private EditText edName = null;
  private Spinner spinType = null;  
  private EditText edDescription = null;
  
	private EditsGeoPos editsGeoPos = null;
  
  private EditText edLocationAlt = null;
  private TextView labelAltitudeSuffix = null;
  
  private Button btnLocationGet = null;

	private String[] vecTypes = null;

	private WaitDialog dlgWait = null;
	private String sProgressGPS = null;
	private GpsOneShotReader gpsReader = null;		
	
	
	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_add_waypoint);
	  
    editsGeoPos = (EditsGeoPos)findViewById(R.id.EditsGeoPos);	  
	  
	  setData(dataBase.tableWaypoints());
	  
	  if (cmdStartData.isEditMode())
	  	setSubTitle(R.string.title_waypointedit);
	  
	  if (cmdStartData.isInsertMode())
	  	setSubTitle(R.string.title_addwaypoint);
	  
	  sProgressGPS = this.getString(R.string.progress_text_gps);
	  
		vecTypes = mContext.getResources().getStringArray(R.array.waypoint_types);

	  edName = (EditText)this.findViewById(R.id.EditWaypointName);
	  spinType = (Spinner)this.findViewById(R.id.spinWaypointType);
	  
	  edDescription = (EditText)this.findViewById(R.id.EditWaypointDescription);	 
	  edLocationAlt = (EditText)this.findViewById(R.id.EditWaypointLocation_alt);
	  labelAltitudeSuffix = (TextView)this.findViewById(R.id.LabelAltitudeSuffix);
	  
	  btnLocationGet = (Button)this.findViewById(R.id.btnLocationGet);
	  
	  setItemsForSpinner(spinType, vecTypes);
	  
	  spinType.setOnItemSelectedListener(eventTypeSelected);
	  
	  btnLocationGet.setOnClickListener(onClickGetLocation);
	  
		edLocationAlt.setText("");

	  gpsReader = new GpsOneShotReader(this);	  
		gpsReader.addObserver(ActivityAddWaypoint.this);		
	}	
		
	protected void onStart()
	{
		prefs.load();
		
		editsGeoPos.setDecimalMode(prefs.bLocationEditDecimal);
		
		labelAltitudeSuffix.setText(Utils.getDistancePreciseSuffix(prefs, 1));
		
		super.onStart();
	}
	
	protected void onStop()
	{
		gpsReader.forceStop();
		
		super.onStop();
	}
	
	public DataValues getControlValuesForUpdate(DataTable table)
	{
		final boolean bForInsert = cmdStartData.isInsertMode();		
		final DataValues values = table.getNewValues(bForInsert);
		
		final String sName = edName.getText().toString();		
		
		int iTypeIndex = spinType.getSelectedItemPosition();
		if (iTypeIndex == Spinner.INVALID_POSITION)
			iTypeIndex = 0;
		
		final String sDescription = edDescription.getText().toString();
		
		final Double dLocationLon = editsGeoPos.getLongitude();
		final Double dLocationLat = editsGeoPos.getLatitude();
		
		values.setValue(DataTableWaypoints.field.Name, sName);
		values.setValue(DataTableWaypoints.field.Type, iTypeIndex);		
		values.setValue(DataTableWaypoints.field.Description, sDescription);
		values.setValue(DataTableWaypoints.field.Lon, (dLocationLon == null) ? 0 : dLocationLon);
		values.setValue(DataTableWaypoints.field.Lat, (dLocationLat == null) ? 0 : dLocationLat);
		values.setValue(DataTableWaypoints.field.Altitude, getAltitude());

		return values;
	}
	
	public void setControlValuesForView(DataValues values)
	{
		String sName = values.getString(DataTableWaypoints.field.Name);
		
		int iType = values.getInteger(DataTableWaypoints.field.Type);
		
		String sDescription = values.getString(DataTableWaypoints.field.Description);
		String sLocationLon = values.getString(DataTableWaypoints.field.Lon);
		String sLocationLat = values.getString(DataTableWaypoints.field.Lat);
		
		int iAltitude = values.getInteger(DataTableWaypoints.field.Altitude);

		if (savedState.isStartupState())
		{
			sName = savedState.getString(DataTableWaypoints.field.Name);			
			iType = savedState.getInt(DataTableWaypoints.field.Type);			
			sDescription = savedState.getString(DataTableWaypoints.field.Description);
			sLocationLon = savedState.getString(DataTableWaypoints.field.Lon);
			sLocationLat = savedState.getString(DataTableWaypoints.field.Lat);
			iAltitude = savedState.getInt(DataTableWaypoints.field.Altitude);
		}

		//correct type index
		if (iType == -1)
			iType = 0;
		if (iType > DataItemWaypoint.MAX_TYPE_INDEX)
			iType = 0;
		
		edName.setText(sName);
		spinType.setSelection(iType);		
		edDescription.setText(sDescription);
		
		editsGeoPos.setLongitude(Double.parseDouble(sLocationLon));
		editsGeoPos.setLatitude(Double.parseDouble(sLocationLat));

		setAltitude(iAltitude);
	}
	
	public boolean onClickedDone(Bundle data)
	{
		//insert new record
	  if (cmdStartData.isInsertMode())
	  	return insertNewWaypoint();
	  
	  //update old record
	  if (cmdStartData.isEditMode())
	  	return updateWaypoint();
	  
	  return false;
	}
	
	public boolean onClickedRevert(Bundle data)
	{
		return true;
	}	
	
	public boolean insertNewWaypoint()
	{
	  final long lNewRowId = activityTable.dataInsert();
	  
  	if (lNewRowId == -1)
  	{
  		return false;
  	} else { 		
  		//update observers
 			dataBase.tableWaypoints().sendOperation(lNewRowId, DataTableOperation.OP_INSERT);
  		return true;
  	}
	}

	public boolean updateWaypoint()
	{
		final long lRowId = activityTable.getRowIdForOperation();
  	final boolean bSuccess = (activityTable.dataUpdate());
  	if (bSuccess)
  	{
  		dataBase.tableWaypoints().sendOperation(lRowId, DataTableOperation.OP_UPDATE);
  	}  	
  	return bSuccess;
	}

	protected void onSaveInstanceState(Bundle outState)
	{		
		StateBundle state = new StateBundle(dataBase.tableWaypoints(), outState);
		
		state.set(DataTableWaypoints.field.Name, edName);
		state.setPosition(DataTableWaypoints.field.Type, spinType);	
		state.set(DataTableWaypoints.field.Description, edDescription);
		
		final Double dLon = editsGeoPos.getLongitude();
		final Double dLat = editsGeoPos.getLatitude();
		
		state.set(DataTableWaypoints.field.Lon, (dLon == null) ? "0" : Double.toString(dLon));
		state.set(DataTableWaypoints.field.Lat, (dLat == null) ? "0" : Double.toString(dLat));

		state.set(DataTableWaypoints.field.Altitude, getAltitude());

		super.onSaveInstanceState(outState);
	}
	
	public AdapterView.OnItemSelectedListener eventTypeSelected = new AdapterView.OnItemSelectedListener()
	{
		public void onItemSelected(AdapterView<?> adp, View view, int position, long id)
		{
		}
		public void onNothingSelected(AdapterView<?> arg0)
		{
		}	
	};

	private View.OnClickListener onClickGetLocation = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			//show wait dialog
			dlgWait = new WaitDialog(ActivityAddWaypoint.this);
			dlgWait.show(sProgressGPS);

			btnLocationGet.setEnabled(false);
			
			editsGeoPos.setEnabled(false);
			
			edLocationAlt.setEnabled(false);
			
			gpsReader.restart();
		}		
	};
	
	//override Observer method
	public void update(Observable observable, Object data)
	{		
		super.update(observable, data);
		
		//watch for tracks data table changes
		if (observable == gpsReader)
		{
			if (data == GpsReader.LOCATION_READY)
			{
				updateLocationData();

				btnLocationGet.setEnabled(true);
				
				editsGeoPos.setEnabled(true);
				
				edLocationAlt.setEnabled(true);

				//hide wait dialog
				if (dlgWait != null)
					dlgWait.hide();				
			}
		}
	}

	private void updateLocationData()
	{
	  final Location loc = gpsReader.getLastLocation();
	  if (loc != null)
	  {
	  	editsGeoPos.setLongitude(loc.getLongitude());
			editsGeoPos.setLatitude(loc.getLatitude());
			
			setAltitude((int)loc.getAltitude());
			
			gpsReader.stop();			
	  }		
	}
	
	private void setAltitude(int iAltitude)
	{
		final String sValue = Utils.getDistanceAsStringPreciseValue(prefs, iAltitude);		
		edLocationAlt.setText(sValue);
	}
	
	private int getAltitude()
	{
		int iAltitude = Integer.parseInt(edLocationAlt.getText().toString());
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)
			iAltitude = (int)Utils.footsToMeters(iAltitude);

		return iAltitude;
	}
	
}

