package pl.tripcomputer.gps;

import pl.tripcomputer.Preferences;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;


public class GpsActivityReader extends GpsReader
{
	//fields
	private String sMsg_LocatingPosition = null;
	
	private boolean bShowMsgOnce = false;
	private boolean bShowSettingsOnce = false;
	
	private boolean bVibrated = false;
	
	
	//methods
	public GpsActivityReader(ActivityMain parent, Preferences prefs)
	{
		super(parent, prefs);
		
		sMsg_LocatingPosition = context.getString(R.string.notify_msg_gps_is_locating);
		
		bShowMsgOnce = true;
		bShowSettingsOnce = true;
	}

	public void start()
	{
		prefs.load();
		super.start(prefs.iSecondsBetweenLocationUpdate);		
				
		if (isEnabled())
		{
			if (bShowMsgOnce)
			{
				Toast.makeText(context, sMsg_LocatingPosition, Toast.LENGTH_LONG).show();
				bShowMsgOnce = false;
			}
		} else {
			showLocationSettings();
		}
	}
	
	protected void showLocationSettings()
	{
		if (!isEnabled())
		{
			if (bShowSettingsOnce)
			{		
				bShowSettingsOnce = false;
								
				UserAlert.question(context, R.string.alert_question_enable_loc_service, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						if (which == DialogInterface.BUTTON_POSITIVE)
						{
							//show location settings
							context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
						}
					}
				});
				
			}
		}
	}
		
	protected void onGpsLocationChanged(Location location, boolean bAccuracyAcceptable)
	{
		//make vibration on first fix
		if (bAccuracyAcceptable)
		{
			if (!bVibrated)
			{
				vibrate();
				bVibrated = true;
			}			
		} else {
			bVibrated = false;			
		}
		
		//post location to global position object
		ActivityMain.loader.gpsLocationStatus().postLocation(location);
	}

	public void restart()
	{
		stop();
		start();			
	}
	
}
