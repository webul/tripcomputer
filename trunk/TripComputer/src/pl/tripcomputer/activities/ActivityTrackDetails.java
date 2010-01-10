package pl.tripcomputer.activities;

import pl.tripcomputer.Command;
import pl.tripcomputer.CommandData;
import pl.tripcomputer.R;
import pl.tripcomputer.Utils;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.items.DataItemTrack;
import pl.tripcomputer.data.tables.DataTableTracks;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ActivityTrackDetails extends CommonActivity
{
	//fields
  private TextView infoTrackName = null;
  private TextView infoTrackDescription = null;
  private TextView infoTrackType = null;
  private TextView infoTrackStartTime = null;
  private TextView infoTrackSummary = null;
  
  private Button btnShow = null;
  private Button btnHide = null;

  
	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_track_details);

	  setData(dataBase.tableTracks());
	  
	  if (cmdStartData.isViewMode())
	  	setSubTitle(R.string.title_trackdetails);

	  infoTrackName = (TextView)this.findViewById(R.id.InfoTrackName);
	  infoTrackDescription = (TextView)this.findViewById(R.id.InfoTrackDescription);
	  infoTrackType = (TextView)this.findViewById(R.id.InfoTrackType);
	  infoTrackStartTime = (TextView)this.findViewById(R.id.InfoTrackStartTime);
	  infoTrackSummary = (TextView)this.findViewById(R.id.InfoTrackSummary);
	  
	  btnShow = (Button)this.findViewById(R.id.ButtonShow);
	  btnShow.setOnClickListener(eventButtonShowClicked);	  	

  	btnHide = (Button)this.findViewById(R.id.ButtonHide);
  	btnHide.setOnClickListener(eventButtonHideClicked);	  	
	}
	
	public void setControlValuesForView(DataValues values)
	{
		//get track id
		final long lTrackId = activityTable.getRowIdForOperation();
		
		//track name
		infoTrackName.setText(values.getString(DataTableTracks.field.Name));
		
		//track desc
		String sDescription = values.getString(DataTableTracks.field.Description);
		if ((sDescription == null) || (sDescription.length() == 0))
			sDescription = getResString(R.string.labelNoText);			
		
		infoTrackDescription.setText(sDescription);
		
		//track type
		final int iType = values.getInteger(DataTableTracks.field.Type);		
		final String sType = DataItemTrack.trackTypeToString(this, iType);		
		infoTrackType.setText(sType);
		
		//track start time	
		final String sStartTime = values.getDateTimeAsString(DataTableTracks.field.StartTime);
		infoTrackStartTime.setText(sStartTime);

		//track summary
		String sSummary = getResString(R.string.labelNoText);
		
		final float fAvgSpeed = ActivityMain.loader.stats.getTrackAverageSpeed(lTrackId);
		if (fAvgSpeed != 0)
		{
			final String sAverageSpeed = Utils.getSpeedAsString(prefs, fAvgSpeed);			
			sSummary = getResString(R.string.labelTrackSummaryAvgSpeed) + ": " + sAverageSpeed;
		}
		
		infoTrackSummary.setText(sSummary);
				
		//is track closed ?
		final boolean bTrackClosed = values.getBool(DataTableTracks.field.Closed);
		if (bTrackClosed)
		{
			//is track visible ?
			final boolean bTrackVisible = values.getBool(DataTableTracks.field.Visible);
			updateButtons(bTrackVisible);			
		}	else {
			btnShow.setVisibility(View.GONE);
			btnHide.setVisibility(View.GONE);			
		}
	}
	
	public boolean onClickedRevert(Bundle data)
	{
		return true;		
	}
	
	private void updateButtons(boolean bTrackVisible)
	{
		if (bTrackVisible)
		{
			btnShow.setVisibility(View.GONE);
			btnHide.setVisibility(View.VISIBLE);
		} else {
			btnShow.setVisibility(View.VISIBLE);
			btnHide.setVisibility(View.GONE);			
		}
	}
	
	private View.OnClickListener eventButtonShowClicked = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			final long lRowId = cmdStartData.getRowId();
			CommandData cmdData = new CommandData(CommandData.MODE_NONE, lRowId);						
			if (main.runCommand(Command.CMD_TRACK_SHOW, cmdData))
			{
				closeActivityWithResultOK();
			}
		}
	};

	private View.OnClickListener eventButtonHideClicked = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			final long lRowId = cmdStartData.getRowId();
			CommandData cmdData = new CommandData(CommandData.MODE_NONE, lRowId);						
			if (main.runCommand(Command.CMD_TRACK_HIDE, cmdData))
			{
				closeActivityWithResultOK();
			}
		}		
	};

}
