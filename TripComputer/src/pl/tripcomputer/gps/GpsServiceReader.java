package pl.tripcomputer.gps;

import pl.tripcomputer.Preferences;
import android.content.Context;
import android.location.Location;


public class GpsServiceReader extends GpsReader
{
	//fields
	private GpsLogger logger = null;
		
	
	//methods
	public GpsServiceReader(Context context, Preferences prefs, GpsLogger logger)
	{
		super(context, prefs);
		this.logger = logger;
	}
		
	public void start()
	{
		prefs.load();
		super.start(prefs.iSecondsBetweenLocationUpdate);
	}
		
	protected void onGpsLocationChanged(Location location, boolean bAccuracyAcceptable)
	{
		if (bAccuracyAcceptable)
		{
			logger.saveLocation(location);
		}
		logger.sendNewLocationToParent(location);		
	}

}
