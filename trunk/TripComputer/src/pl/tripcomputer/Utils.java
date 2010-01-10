package pl.tripcomputer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;


public class Utils
{
	//fields
	private final static int MAX_TIME_VALUE_24_HOURS = 3600 * 24; 
	
	//fields
	public static double ONE_FOOT_IN_METERS = 0.3048f;
	public static double ONE_MILE_IN_METERS = 1609.344f;
	public static double ONE_MILE_IN_FOOTS = 5280f;

	//fields
	private static TimeZone timeZone = TimeZone.getDefault();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat();
	
	//fields
	private static DecimalFormat fmtDistanceKiloUnit = new DecimalFormat("0.00");
	private static DecimalFormat fmtDistanceUnit  = new DecimalFormat("0");
	
	private static String sEuroDistanceUnit = "m";
	private static String sEuroDistanceKiloUnit = "km";

	private static String sUSADistanceUnit = "ft";
	private static String sUSADistanceKiloUnit = "mi";
	
	private static DecimalFormat fmtDistanceKiloMetersInt = new DecimalFormat("0 km");
	private static DecimalFormat fmtDistanceKiloMeters = new DecimalFormat("0.00 km");
	private static DecimalFormat fmtDistanceMeters = new DecimalFormat("0 m");

	private static DecimalFormat fmtDistanceMilesInt = new DecimalFormat("0 mi");
	private static DecimalFormat fmtDistanceMiles = new DecimalFormat("0.00 mi");
	private static DecimalFormat fmtDistanceFoots = new DecimalFormat("0 ft");
	
	private static DecimalFormat fmtEuroSpeed = new DecimalFormat("0.0 km/h");
	private static DecimalFormat fmtUSASpeed = new DecimalFormat("0.0 mph");
	private static DecimalFormat fmtSpeedValue = new DecimalFormat("0.0");
	
	private static String sEuroSpeedUnit = "km/h";
	private static String sUSASpeedUnit = "mph";
	
	private static String fmtTime = "%d:%02d";
	public static String fmtTimeZero = "0:00";


	//methods
	public static double metersToMiles(double fMeters)
	{
		return fMeters / ONE_MILE_IN_METERS;		
	}

	public static double metersToFoots(double fMeters)
	{
		return fMeters / ONE_FOOT_IN_METERS;		
	}

	public static double milesToMeters(double fMiles)
	{
		return fMiles * ONE_MILE_IN_METERS;
	}
	
	public static double footsToMeters(double fFoots)
	{
		return fFoots * ONE_FOOT_IN_METERS;
	}

	//convert distance in meters
	public static String getDistanceAsString(Preferences prefs, double dDistance)
	{
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)
		{
			return fmtDistanceMiles.format(metersToMiles(dDistance));
		}

		//default mode euro
		return fmtDistanceKiloMeters.format(dDistance / 1000);
	}

	//convert distance in meters
	public static String getDistanceIntAsString(Preferences prefs, int iDistance)
	{
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)
		{
			return fmtDistanceMilesInt.format(metersToMiles(iDistance));
		}

		//default mode euro
		return fmtDistanceKiloMetersInt.format(iDistance / 1000);
	}
	
	//convert distance in meters
	public static String getDistanceAsStringPrecise(Preferences prefs, double dDistance)
	{
		String sValue = "";
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_EURO)
		{		
			if (dDistance > 1000)
			{
				sValue = fmtDistanceKiloMeters.format(dDistance / 1000);			
			} else {
				sValue = fmtDistanceMeters.format(dDistance); 			
			}
			return sValue;
		}
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)
		{
			if (dDistance > ONE_MILE_IN_METERS)
			{
				sValue = fmtDistanceMiles.format(metersToMiles(dDistance));			
			} else {
				sValue = fmtDistanceFoots.format(metersToFoots(dDistance)); 			
			}
			return sValue;
		}
		
		return sValue;
	}

	//convert distance in meters to value
	public static String getDistanceAsStringPreciseValue(Preferences prefs, double dDistance)
	{
		String sValue = "";
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_EURO)
		{		
			if (dDistance > 1000)
			{
				sValue = fmtDistanceKiloUnit.format(dDistance / 1000);			
			} else {
				sValue = fmtDistanceUnit.format(dDistance);
			}
			return sValue;
		}
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)
		{
			if (dDistance > ONE_MILE_IN_METERS)
			{
				sValue = fmtDistanceKiloUnit.format(metersToMiles(dDistance));			
			} else {
				sValue = fmtDistanceUnit.format(metersToFoots(dDistance)); 			
			}
			return sValue;
		}
		
		return sValue;
	}
	
	//get distance suffix
	public static String getDistancePreciseSuffix(Preferences prefs, double dDistance)
	{
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_EURO)
		{		
			if (dDistance > 1000)
			{
				return sEuroDistanceKiloUnit;
			} else {
				return sEuroDistanceUnit;
			}
		}
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)
		{			
			if (dDistance > ONE_MILE_IN_METERS)
			{
				return sUSADistanceKiloUnit;
			} else {
				return sUSADistanceUnit;
			}
		}
		
		return "";
	}
	
	public static String getTimeAsString(int iSeconds)
	{
		if (iSeconds < 0)
		{
			return "";
		}
		if (iSeconds > MAX_TIME_VALUE_24_HOURS)
		{
			return "";
		}
		
		final int iHours = (int)((float)iSeconds / (float)3600.0f);
		final int iMinutes = (int)(((float)iSeconds / (float)60.0f) % 60);
	
		return String.format(fmtTime, iHours, iMinutes);
	}

	public static String getAppVersion(Context context)
	{
		//get version from manifest
		final PackageManager packageManager = context.getPackageManager();
		if (packageManager != null)
		{
			PackageInfo packageInfo = null;
			try
			{				
				packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				if (packageInfo != null)
				{
					return packageInfo.versionName;
				}
			} catch (NameNotFoundException e) {
			}			
		}
		return "";
	}	
	
	public static float convertSpeedToKmH(float fSpeedInMetersPerSecond)
	{
		return fSpeedInMetersPerSecond * 3.6f;
	}
	
	public static float convertSpeedToMpH(float fSpeedInMetersPerSecond)
	{
		return fSpeedInMetersPerSecond * 2.2369f;
	}
	
	//returns speed value converted with suffix
	public static String getSpeedAsString(Preferences prefs, float fSpeed)
	{
		String sValue = "";

		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_EURO)
			sValue = fmtEuroSpeed.format(convertSpeedToKmH(fSpeed));
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)
			sValue = fmtUSASpeed.format(convertSpeedToMpH(fSpeed));			
	
		return sValue;
	}

	//returns speed value converted without suffix
	public static String getSpeedAsStringValue(Preferences prefs, float fSpeed)
	{
		String sValue = "";

		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_EURO)
			sValue = fmtSpeedValue.format(convertSpeedToKmH(fSpeed));
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)
			sValue = fmtSpeedValue.format(convertSpeedToMpH(fSpeed));			
	
		return sValue;
	}
	
	//returns speed suffix
	public static String getSpeedSuffix(Preferences prefs)
	{
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_EURO)
		{		
			return sEuroSpeedUnit;
		}
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)
		{		
			return sUSASpeedUnit;
		}
		
		return "";
	}
	
	public static String getDateTimeAsString(long lTimeInMs)
	{
		final long lTimeMs = lTimeInMs + timeZone.getRawOffset();
		return dateFormat.format(new Date(lTimeMs));
	}
	
	public static String locLonToString(double dLon)
	{
		String sLon = Location.convert(dLon, Location.FORMAT_SECONDS);
		sLon += (dLon >= 0) ? " E" : " W";
		return sLon;
	}

	public static String locLatToString(double dLat)
	{
		String sLat = Location.convert(dLat, Location.FORMAT_SECONDS);
		sLat += (dLat >= 0) ? " N" : " S";
		return sLat;
	}

	//returns iso based language id's: pl, en, etc.. 
	public static String getLanguageId(Context context)
	{
		final Resources res = context.getResources();
		if (res != null)
		{
			final Configuration config = res.getConfiguration();
			if (config != null)
			{
				return config.locale.getLanguage();				
			}			
		}
		return "";
	}
	
}
