package pl.tripcomputer;

import java.util.ArrayList;

import pl.tripcomputer.gps.GpsReader;

import android.content.Context;
import android.content.SharedPreferences;


public class Preferences
{
	//fields
	private static final String PREFS_NAME = "TripComputerSettings";
	private Context context = null;
	private SharedPreferences prefs = null;	
	
	//fields
	public static int MEASURE_MODE_USA = 0;
	public static int MEASURE_MODE_EURO = 1;

	public static int DEFAULT_UPDATE_PERIOD = 15;
	public static int DEFAULT_LOCATION_ACCURACY = (int)GpsReader.MINIMUM_ACCURACY;
	
	
	//fields
	public int iMeasureMode = MEASURE_MODE_EURO;
	public int iSecondsBetweenLocationUpdate = DEFAULT_UPDATE_PERIOD;	
	public boolean bShowGrid = true;
	public boolean bLocationEditDecimal = true;
	public String sEmailAddress = "";
	public String sAccessCode = "";
	public int iLocationAccuracy = DEFAULT_LOCATION_ACCURACY;

	//fields
	private static ArrayList<Integer> vecUpdatePeriodSeconds = new ArrayList<Integer>();
	private static ArrayList<Integer> vecLocationAccuracy = new ArrayList<Integer>();
	
	static
	{
		//init update period vector
		vecUpdatePeriodSeconds.add(0);
		vecUpdatePeriodSeconds.add(2);
		vecUpdatePeriodSeconds.add(5);
		vecUpdatePeriodSeconds.add(10);
		vecUpdatePeriodSeconds.add(15);
		vecUpdatePeriodSeconds.add(30);
		vecUpdatePeriodSeconds.add(45);
		vecUpdatePeriodSeconds.add(60);	
		
		//init location accuracy vector
		vecLocationAccuracy.add(10);
		vecLocationAccuracy.add(20);
		vecLocationAccuracy.add(50);
		vecLocationAccuracy.add(100);
		vecLocationAccuracy.add(200);		
	};

	
	//methods	
  public Preferences(Context context)
  {
  	this.context = context;
  	load();
  }
  
	public void load()
	{
  	prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_WORLD_READABLE);
  	
		iMeasureMode = prefs.getInt("iMeasureMode", MEASURE_MODE_EURO);
		
		iSecondsBetweenLocationUpdate = prefs.getInt("iSecondsBetweenLocationUpdate", DEFAULT_UPDATE_PERIOD);
		iLocationAccuracy = prefs.getInt("iLocationAccuracy", DEFAULT_LOCATION_ACCURACY);
		
		bShowGrid = prefs.getBoolean("bShowGrid", true);
		bLocationEditDecimal = prefs.getBoolean("bLocationEditDecimal", true);
		
		sEmailAddress = prefs.getString("sEmailAddress", "");
		sAccessCode = prefs.getString("sAccessCode", "");		
	}
	  
	public boolean save()
	{
		try
		{
			SharedPreferences.Editor editor = prefs.edit();
			
			editor.putInt("iMeasureMode", iMeasureMode);

			editor.putInt("iSecondsBetweenLocationUpdate", iSecondsBetweenLocationUpdate);
			editor.putInt("iLocationAccuracy", iLocationAccuracy);
			
			editor.putBoolean("bShowGrid", bShowGrid);
			editor.putBoolean("bLocationEditDecimal", bLocationEditDecimal);
			
			editor.putString("sEmailAddress", sEmailAddress);
			editor.putString("sAccessCode", sAccessCode);
				
	    editor.commit();
	    
	    return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public int getUpdatePeriodItemIndex()
	{
		return vecUpdatePeriodSeconds.indexOf(new Integer(iSecondsBetweenLocationUpdate));
	}
	
	public void setUpdatePeriodItemIndex(int index)
	{
		final int iValue = vecUpdatePeriodSeconds.get(index);		
		iSecondsBetweenLocationUpdate = iValue;
	}
	
	public int getLocationAccuracyIndex()
	{
		return vecLocationAccuracy.indexOf(new Integer(iLocationAccuracy));		
	}

	public void setLocationAccuracyIndex(int index)
	{
		final int iValue = vecLocationAccuracy.get(index);		
		iLocationAccuracy = iValue;
	}
	
}
