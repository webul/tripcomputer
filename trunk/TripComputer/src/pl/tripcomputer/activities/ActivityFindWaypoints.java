package pl.tripcomputer.activities;

import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.tripcomputer.R;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.Utils;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.http.ParamsGetWaypointList;
import pl.tripcomputer.webservice.AccessGetWaypointList;
import pl.tripcomputer.webservice.AccessGetWaypointList.WaypointListStatus;


public class ActivityFindWaypoints extends CommonActivity
{
	//fields
	private AccessGetWaypointList accessGetWaypointList = null;
	private static Location locCurrent = null; 
	
	//fields
	private LinearLayout layDistanceSelector = null;	
	private RadioButton rdbtnWptPublic = null;
	private RadioButton rdbtnWptPrivate = null;
	private SeekBar seekBarRange = null;
	private TextView labelRangeText = null; 

	private String[] vecFindDistance = new String[3];


	//methods
	public static boolean gpsLocationReady(CommonActivity parent)
	{	
		//get current location
	  final Location loc = ActivityMain.getLastLocation();
	  if (loc == null)
	  {
	  	UserAlert.show(parent, parent.getString(R.string.alert_msg_gps_no_fix));	  	
	  } else {
	  	if (loc.getAccuracy() < 1000)
	  	{
	  		locCurrent = new Location(loc);
	  		//open dialog
	  		return true;
	  	} else {
	  		UserAlert.show(parent, parent.getString(R.string.alert_msg_gps_accuracy_low));	  		
	  	}
	  }
	  return false;
	}
	
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);

	  requestWindowFeature(Window.FEATURE_NO_TITLE);
	  
	  setContentView(R.layout.activity_find_waypoints);
	  
	  accessGetWaypointList = new AccessGetWaypointList(this);
	  accessGetWaypointList.setStatusListener(eventListStatus);
	  
	  vecFindDistance[0] = getString(R.string.find_distance_0);
	  vecFindDistance[1] = getString(R.string.find_distance_1);
	  vecFindDistance[2] = getString(R.string.find_distance_2);
	  
	  layDistanceSelector = (LinearLayout)findViewById(R.id.layDistanceSelector);
	  
		rdbtnWptPublic = (RadioButton)findViewById(R.id.rdbtnWptPublic);
		rdbtnWptPublic.setOnCheckedChangeListener(eventChangePublic);
		
		rdbtnWptPrivate = (RadioButton)findViewById(R.id.rdbtnWptPrivate);
		rdbtnWptPrivate.setOnCheckedChangeListener(eventChangePublic);
		
		seekBarRange = (SeekBar)findViewById(R.id.seekBarRange);		
		seekBarRange.setOnSeekBarChangeListener(eventChangeRange);
		
		labelRangeText = (TextView)findViewById(R.id.labelRangeText);
		
		initialize();
	}
	
	private final CompoundButton.OnCheckedChangeListener eventChangePublic = new CompoundButton.OnCheckedChangeListener()
	{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			final boolean bPublic = rdbtnWptPublic.isChecked();
			layDistanceSelector.setVisibility(bPublic ? View.VISIBLE : View.GONE);			
		}
	};
	
	private final SeekBar.OnSeekBarChangeListener eventChangeRange = new SeekBar.OnSeekBarChangeListener()
	{
		public void onStopTrackingTouch(SeekBar seekBar)
		{
		}
		public void onStartTrackingTouch(SeekBar seekBar)
		{
		}
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			updateDistanceText();
		}
	}; 
	
	private void initialize()
	{
		rdbtnWptPrivate.setChecked(true);
		rdbtnWptPublic.setChecked(false);
		
		seekBarRange.setMax(ParamsGetWaypointList.RANGE_MAX);
		seekBarRange.setProgress(ParamsGetWaypointList.RANGE_MIN);
		
		updateDistanceText();
	}

	private String getDistanceText(int iValue)
	{
		return " (~" + Utils.getDistanceIntAsString(prefs, iValue) + ")";
	}
	
	private void updateDistanceText()
	{
		String sValue = "";
		
		if (seekBarRange.getProgress() == ParamsGetWaypointList.RANGE_MIN)
			sValue = vecFindDistance[0] + getDistanceText(7000);
		
		if (seekBarRange.getProgress() == ParamsGetWaypointList.RANGE_MIN + 1)
			sValue = vecFindDistance[1] + getDistanceText(17000);

		if (seekBarRange.getProgress() == ParamsGetWaypointList.RANGE_MAX)
			sValue = vecFindDistance[2] + getDistanceText(32000);
		
		labelRangeText.setText(sValue);
	}

	//REVERT button function: cancel uploading
	public boolean onClickedRevert(Bundle data)
	{		
		return true;
	}
	
	//DONE button function:
	public boolean onClickedDone(Bundle data)
	{
		if (locCurrent != null)
		{
			final ParamsGetWaypointList findParams = new ParamsGetWaypointList();
			
			findParams.bPublic = rdbtnWptPublic.isChecked();			
			findParams.dLon = locCurrent.getLongitude();
			findParams.dLat = locCurrent.getLatitude();
			findParams.iPageIndex = 0;
			findParams.iRangeType = seekBarRange.getProgress();					
		
			accessGetWaypointList.execute(findParams);			
		}				
		return false;
	}
	
	private WaypointListStatus eventListStatus = new WaypointListStatus()
	{
		public void eventListReady(ArrayList<DataItemWaypoint> items)
		{
			ActivityFindWaypoints.this.closeActivityWithResultOK();
			
			//show waypoints download dialog with list
			getMain().showWaypointsDownload(items, accessGetWaypointList.getParams());
		}
	};	
	
}
