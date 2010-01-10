package pl.tripcomputer.gps;

import pl.tripcomputer.common.CommonActivity;
import android.location.Location;


public class GpsOneShotReader extends GpsReader
{
	//get current location each 10 minutes
	private final static int DEFAULT_UPDATE_PERIOD = 60 * 30;
	
	//fields
	private boolean bVibrated = false;
	
	
	//methods
	public GpsOneShotReader(CommonActivity parent)
	{
		super(parent, parent.getPrefs());
	}

	public void start()
	{
		super.start(DEFAULT_UPDATE_PERIOD);
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
	}

	public void restart()
	{
		stop();
		start();			
	}
	
}
