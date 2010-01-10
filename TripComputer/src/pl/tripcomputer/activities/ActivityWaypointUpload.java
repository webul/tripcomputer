package pl.tripcomputer.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.data.items.WebDataItemWaypoint;
import pl.tripcomputer.webservice.AccessSendWaypoint;
import pl.tripcomputer.webservice.AccessWaypointData;


public class ActivityWaypointUpload extends CommonActivity
{
	//fields
	private AccessWaypointData accessWaypointData = null;
	private AccessSendWaypoint accessSendWaypoint = null;
	
	private TextView labelWaypointName = null;
	private TextView labelWaypointStatus = null;
	private CheckBox chkWaypointPublic = null;
	private TextView labelPublicDesc = null;

	private String sMsgWptStatusUpdate = null;
	private String sMsgWptStatusUpdatePublic = null;
	private String sMsgWptStatusAdd = null;
	
	
	//methods	
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);

	  requestWindowFeature(Window.FEATURE_NO_TITLE);
	  
	  setContentView(R.layout.activity_waypoint_upload);

	  setData(dataBase.tableWaypoints());
	  
	  accessWaypointData = AccessWaypointData.get();
	  accessSendWaypoint = new AccessSendWaypoint(this);
	  
		sMsgWptStatusUpdate = getString(R.string.wptStatusUpdate);
		sMsgWptStatusUpdatePublic = getString(R.string.wptStatusUpdatePublic);
		sMsgWptStatusAdd = getString(R.string.wptStatusAdd);
	  
		labelWaypointName = (TextView)findViewById(R.id.labelWaypointName);
	  labelWaypointStatus = (TextView)findViewById(R.id.labelWaypointStatus);
	  labelPublicDesc = (TextView)findViewById(R.id.labelPublicDesc);
	  chkWaypointPublic = (CheckBox)findViewById(R.id.chkWaypointPublic);
	  
	  chkWaypointPublic.setOnCheckedChangeListener(new OnCheckedChangeListener()
	  {
			public void onCheckedChanged(CompoundButton chkBox, boolean bChecked)
			{
				labelPublicDesc.setVisibility(bChecked ? View.VISIBLE : View.GONE);				
			}
	  });	  
	}
  	
	protected void onStart()
	{
		super.onStart();

		initialize();
	}
		
	private void initialize()
	{
		labelWaypointName.setText(accessWaypointData.getDataItemWaypoint().getName());

		enableControls(false);		
		hideStatus();
		
		//get waypoint returned by server
		final WebDataItemWaypoint waypoint = accessWaypointData.getWebDataItemWaypoint();
		if (waypoint == null)
		{
			//waypoint not exists on serwer, will be added
			setStatus(sMsgWptStatusAdd);
			
			enableControls(true);
		} else {
			//waypoint exists on server					
			if (waypoint.isPublic())
			{
				//waypoint is public
				setStatus(sMsgWptStatusUpdatePublic);
				
				chkWaypointPublic.setChecked(true);						
			} else {
				//waypoint is private, can be updated
				setStatus(sMsgWptStatusUpdate);
				
				enableControls(true);
			}
		}
		
		activityButtons.setEnabledRevert(true);
	}
	
	private void enableControls(boolean bEnabled)
	{
		chkWaypointPublic.setEnabled(bEnabled);
		activityButtons.setEnabledOK(bEnabled);
		activityButtons.setEnabledRevert(bEnabled);
	}
	
	//DONE button function: upload data
	public boolean onClickedDone(Bundle data)
	{
		DataItemWaypoint itemWaypoint = new DataItemWaypoint(accessWaypointData.getDataItemWaypoint());
		
		itemWaypoint.setPublic(chkWaypointPublic.isChecked());
		
		accessSendWaypoint.execute(accessWaypointData.getAccessToken(), itemWaypoint);
		return false;
	}
	
	//REVERT button function: cancel uploading
	public boolean onClickedRevert(Bundle data)
	{
		return true;
	}	

	private void hideStatus()
	{
		labelWaypointStatus.setText("");
		labelWaypointStatus.setVisibility(View.INVISIBLE);		
	}
	
	private void setStatus(String sText)
	{
		labelWaypointStatus.setText(sText);
		labelWaypointStatus.setVisibility(View.VISIBLE);		
	}
	
}
