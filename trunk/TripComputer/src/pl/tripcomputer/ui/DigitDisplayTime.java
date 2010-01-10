package pl.tripcomputer.ui;

import pl.tripcomputer.Command;
import pl.tripcomputer.Utils;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;


public class DigitDisplayTime extends DigitDisplay
{
	//max value
	private final static int MAX_VALUE_24_HOURS = 3600 * 24; 
	private final static int MAX_ALTITUDE = 20 * 1000;
	
	private final static int TIME_TYPE_BY_WALK = 0;
	private final static int TIME_TYPE_BY_BIKE = 1;
	private final static int TIME_TYPE_LAST = 2;
	
	private final Boolean bTimeTypeMutex = false;
	private int iShowTimeType = TIME_TYPE_BY_WALK;
	
	//fields
	private String sTypeTime = null;
	private String sTypeETAbyWalk = null;
	private String sTypeETAbyBike = null;
	private String sBearing = null;
	private String sTypeAltitude = null;
	
	
  //methods
	public DigitDisplayTime(CommonActivity parent, Screen screen)
	{
		super(parent, screen, "00:00", "", R.string.display_time);

		sTypeTime = parent.getString(R.string.display_time);
		sTypeETAbyWalk = parent.getString(R.string.display_time_eta_by_walk);
		sTypeETAbyBike = parent.getString(R.string.display_time_eta_by_bike);
		sBearing = parent.getString(R.string.display_bearing);
		sTypeAltitude = parent.getString(R.string.display_altitude);
		
		this.bAlwaysVisible = true;
	}

	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		super.surfaceSizeChanged(width, height, iScreenOrientation);
		
		//set position
		final int iPosX = width - (int)rtBounds.width() - (int)style.fBackgroundMargin;
		setPos(iPosX, (int)style.fBackgroundMargin);		
	}
	
	public void updateStyle()
	{
		super.updateStyle();
		
		style.rtBackgroundMarginCorrect.right = 12;
	}
	
	public String getZeroValue()
	{
		return "-";
	}
	
	public boolean setTime(int iTimeInSeconds)
	{	
		if (iTimeInSeconds < 0)
		{
			return false;
		}
		if (iTimeInSeconds > MAX_VALUE_24_HOURS)
		{
			return false;
		}
		
		String sValue = Utils.getTimeAsString(iTimeInSeconds);
		setValue(sValue, "");
		return true;
	}
	
	public boolean setBearing(int iBearing)
	{
		String sValue = Integer.toString(iBearing);

		setValue(sValue, sDegreeSymbol);
		
		return true;
	}
	
	public boolean setAltitude(int iAltitude)
	{
		if (iAltitude < 0)
		{
			return false;
		}
		
		if (iAltitude > MAX_ALTITUDE)
		{
			return false;
		}
		
		String sValue = Utils.getDistanceAsStringPreciseValue(prefs, iAltitude);		

		setValue(sValue, Utils.getDistancePreciseSuffix(prefs, iAltitude));
		
		return true;
	}
	
	public boolean updateValues()
	{
		final int iCurrentUiMode = state.getCurrentUiMode();
				
		//display mode TRACKS
		if (iCurrentUiMode == Command.CMD_MODE_TRACKS)
		{
			final int iTimeInSeconds = ActivityMain.loader.stats.getTrackTimeInSeconds();
			if (iTimeInSeconds != -1)
			{
				setType(sTypeTime);
				return setTime(iTimeInSeconds);
			}
		}
		
		//display mode COMPASS
		if (iCurrentUiMode == Command.CMD_MODE_COMPASS)
		{
			int iValue = ActivityMain.loader.stats.getBearingToWaypoint();
			if (iValue != -1)
			{
				setType(sBearing);
				return setBearing(iValue);
			}
		}
		
		//display mode TO SEL WPT
		if (iCurrentUiMode == Command.CMD_MODE_LOC_TO_SEL_WPT)
		{
			int iTimeInSeconds = -1;

			if (getTimeType() == TIME_TYPE_BY_WALK)
			{
				setType(sTypeETAbyWalk);
				iTimeInSeconds = ActivityMain.loader.stats.getWaypointArrivalTimebyWalk();
			}
			
			if (getTimeType() == TIME_TYPE_BY_BIKE)
			{
				setType(sTypeETAbyBike);
				iTimeInSeconds = ActivityMain.loader.stats.getWaypointArrivalTimebyBike();
			}

			if (iTimeInSeconds != -1)
			{
				return setTime(iTimeInSeconds);				
			}		
		}
		
		//display mode TO SEL TRACK
		if (iCurrentUiMode == Command.CMD_MODE_LOC_TO_SEL_TRACK)
		{
			int iTimeInSeconds = -1;

			if (getTimeType() == TIME_TYPE_BY_WALK)
			{
				setType(sTypeETAbyWalk);
				iTimeInSeconds = ActivityMain.loader.stats.getTrackArrivalTimebyWalk();
			}
			
			if (getTimeType() == TIME_TYPE_BY_BIKE)
			{
				setType(sTypeETAbyBike);
				iTimeInSeconds = ActivityMain.loader.stats.getTrackArrivalTimebyBike();
			}

			if (iTimeInSeconds != -1)
			{
				return setTime(iTimeInSeconds);				
			}		
		}
		
		//display mode INFO
		if (iCurrentUiMode == Command.CMD_MODE_INFO)
		{
			final int iAltitude = ActivityMain.loader.stats.getAltitude();			
			if (iAltitude != -1)
			{							
				setType(sTypeAltitude);
				return setAltitude(iAltitude);
			}
		}
		
		setType("");		
		return false;	
	}

	private void changeTimeType()
	{
		synchronized(bTimeTypeMutex)
		{
			iShowTimeType++;
			if (iShowTimeType >= TIME_TYPE_LAST)
				iShowTimeType = TIME_TYPE_BY_WALK;							
		}		
	}
	
	private int getTimeType()
	{
		int iValue = TIME_TYPE_BY_WALK;
		synchronized(bTimeTypeMutex)
		{
			iValue = iShowTimeType;
		}
		return iValue;
	}
	
	public void onClick()
	{
		super.onClick();		
		changeTimeType();
		updateValues();
	}	
	
}
