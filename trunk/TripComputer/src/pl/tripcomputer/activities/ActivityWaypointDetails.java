package pl.tripcomputer.activities;

import pl.tripcomputer.Utils;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.data.tables.DataTableWaypoints;
import android.os.Bundle;
import android.widget.TextView;


public class ActivityWaypointDetails extends CommonActivity
{
	//fields
  private TextView infoWaypointName = null;
  private TextView infoWaypointDescription = null;
  private TextView infoWaypointType = null;
  private TextView infoWaypointLocation = null;
  private TextView infoWaypointETA = null;

	private String[] vecTypes = null;
  private String sLocationAlt = null; 
  private String sDetailsETA = null;
  
  
	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_waypoint_details);

	  setData(dataBase.tableWaypoints());
	  
	  if (cmdStartData.isViewMode())
	  	setSubTitle(R.string.title_waypointdetails);

		vecTypes = mContext.getResources().getStringArray(R.array.waypoint_types);
	  
	  infoWaypointName = (TextView)this.findViewById(R.id.InfoWaypointName);
	  infoWaypointDescription = (TextView)this.findViewById(R.id.InfoWaypointDescription);
	  infoWaypointType = (TextView)this.findViewById(R.id.InfoWaypointType);
	  infoWaypointLocation = (TextView)this.findViewById(R.id.InfoWaypointLocation);
	  infoWaypointETA = (TextView)this.findViewById(R.id.InfoWaypointETA);
	  
	  sLocationAlt = getString(R.string.labelWaypointLocationDetails_alt);
	  sDetailsETA = getString(R.string.labelWaypointDetailsETA);
	}
	
	public void setControlValuesForView(DataValues values)
	{
		//Waypoint name
		infoWaypointName.setText(values.getString(DataTableWaypoints.field.Name));
		
		//Waypoint desc
		String sDescription = values.getString(DataTableWaypoints.field.Description);
		if ((sDescription == null) || (sDescription.length() == 0))
			sDescription = getResString(R.string.labelNoText);
		
		infoWaypointDescription.setText(sDescription);
		
		//Waypoint type
		int iType = values.getInteger(DataTableWaypoints.field.Type);		
		if (iType > DataItemWaypoint.MAX_TYPE_INDEX)
			iType = 0;
			
		infoWaypointType.setText(vecTypes[iType]);
		
		//Waypoint location
		final String sLon = Utils.locLonToString(values.getDouble(DataTableWaypoints.field.Lon));
		final String sLat = Utils.locLatToString(values.getDouble(DataTableWaypoints.field.Lat));

		final int iAltitude = values.getInteger(DataTableWaypoints.field.Altitude);		
		
		String sLocation = "";
		sLocation += sLat;
		sLocation += "\n";
		sLocation += sLon;
		sLocation += "\n";		
		sLocation += sLocationAlt + ": " + Utils.getDistanceAsStringPrecise(prefs, iAltitude); 
						
		infoWaypointLocation.setText(sLocation);
		
		//Waypoint ETA
		final double dLon = values.getDouble(DataTableWaypoints.field.Lon);
		final double dLat = values.getDouble(DataTableWaypoints.field.Lat);
		
		final String sETAbyWalk = Utils.getTimeAsString(ActivityMain.loader.stats.getLocationArrivalTimebyWalk(dLon, dLat));
		final String sETAbyBike = Utils.getTimeAsString(ActivityMain.loader.stats.getLocationArrivalTimebyBike(dLon, dLat));
				
		infoWaypointETA.setText(String.format(sDetailsETA, sETAbyWalk, sETAbyBike));
	}
	
	public boolean onClickedRevert(Bundle data)
	{
		return true;		
	}

}
