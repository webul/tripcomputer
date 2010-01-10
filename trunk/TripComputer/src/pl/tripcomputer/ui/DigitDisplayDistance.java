package pl.tripcomputer.ui;

import pl.tripcomputer.Command;
import pl.tripcomputer.Utils;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.compass.CompassRotation;
import pl.tripcomputer.map.Screen;


public class DigitDisplayDistance extends DigitDisplay
{
	//max value
	private int MAX_VALUE_KM = 999 * 1000;

	//fields
	private String sTypeDistance = null;
	private String sTypeDistanceToTrack = null;
	private String sTypeDistanceToWaypoint = null;
	private String sTypeAzimuth = null;
	private String sTypeAvgSpeed = null;

	
  //methods
	public DigitDisplayDistance(CommonActivity parent, Screen screen)
	{
		super(parent, screen, "000.00", "ww", R.string.display_distance);
		
		sTypeDistance = parent.getString(R.string.display_distance);
		sTypeDistanceToTrack = parent.getString(R.string.display_distance_to_track);
		sTypeDistanceToWaypoint = parent.getString(R.string.display_distance_to_waypoint);
		sTypeAzimuth = parent.getString(R.string.display_azimuth);
		sTypeAvgSpeed = parent.getString(R.string.display_avg_speed);
		
		this.bAlwaysVisible = true;
	}
	
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		super.surfaceSizeChanged(width, height, iScreenOrientation);
		
		//set position
		setPos((int)style.fBackgroundMargin, (int)style.fBackgroundMargin);
	}

	public void updateStyle()
	{
		super.updateStyle();
		
		style.rtBackgroundMarginCorrect.left = -12;
	}
	
	public String getZeroValue()
	{
		return "-";
	}	
	
	public boolean setDistance(double dDistance)
	{
		if (dDistance < 0)
		{
			return false;
		}
		
		if (dDistance > MAX_VALUE_KM)
		{
			return false;
		}
		
		String sValue = Utils.getDistanceAsStringPreciseValue(prefs, dDistance);
		
		setValue(sValue, Utils.getDistancePreciseSuffix(prefs, dDistance));
		
		return true;
	}

	public boolean setAzimuth()
	{
		String sValue = Integer.toString((int)CompassRotation.getAzimuth());
		
		setValue(sValue, sDegreeSymbol);
				
		return true;
	}

	public boolean setSpeed(float fSpeed)
	{
		final String sAverageSpeed = Utils.getSpeedAsStringValue(prefs, fSpeed);
		
		setValue(sAverageSpeed, Utils.getSpeedSuffix(prefs));
				
		return true;
	}
	
	public void updateObjectsState()
	{
		super.updateObjectsState();
		
		final int iCurrentUiMode = state.getCurrentUiMode();
		
		if (iCurrentUiMode == Command.CMD_MODE_COMPASS)
		{
			setAzimuth();
		}
	}
	
	public boolean updateValues()
	{
		final int iCurrentUiMode = state.getCurrentUiMode();
				
		//display mode TRACKS
		if (iCurrentUiMode == Command.CMD_MODE_TRACKS)
		{
			final double dDistance = ActivityMain.loader.stats.getTotalDistanceInMeters();
			if (dDistance != -1)
			{
				setType(sTypeDistance);
				return setDistance(dDistance);
			}
		}

		//display mode COMPASS
		if (iCurrentUiMode == Command.CMD_MODE_COMPASS)
		{
			setType(sTypeAzimuth);
			return true;
		}

		//display mode TO SEL TRACK
		if (iCurrentUiMode == Command.CMD_MODE_LOC_TO_SEL_TRACK)
		{
			final double dDistance = ActivityMain.loader.stats.getDistanceToTrackInMeters();
			if (dDistance != -1)
			{
				setType(sTypeDistanceToTrack);
				return setDistance(dDistance);
			}
		}

		//display mode TO SEL WPT
		if (iCurrentUiMode == Command.CMD_MODE_LOC_TO_SEL_WPT)
		{
			final double dDistance = ActivityMain.loader.stats.getDistanceToWaypointInMeters();
			if (dDistance != -1)
			{				
				setType(sTypeDistanceToWaypoint);
				return setDistance(dDistance);
			}
		}

		//display mode INFO
		if (iCurrentUiMode == Command.CMD_MODE_INFO)
		{
			final float fAvgSpeed = ActivityMain.loader.stats.getTrackAverageSpeed();
			if (fAvgSpeed != -1)
			{
				setType(sTypeAvgSpeed);
				return setSpeed(fAvgSpeed);
			}			
		}
		
		setType("");
		return false;	
	}
	
}
