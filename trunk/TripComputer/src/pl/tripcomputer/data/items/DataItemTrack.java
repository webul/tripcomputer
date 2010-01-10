package pl.tripcomputer.data.items;

import java.util.Calendar;

import pl.tripcomputer.Utils;
import pl.tripcomputer.R;
import pl.tripcomputer.data.common.DataItem;
import pl.tripcomputer.data.tables.DataTableGeoPoints;
import pl.tripcomputer.data.tables.DataTableTracks;
import android.content.Context;
import android.database.Cursor;


public class DataItemTrack extends DataItem
{
	//fields
	private static int TYPE_BIT_WALK = 0x01; 
	private static int TYPE_BIT_BIKE = 0x02; 
	
	//fields		
	private String sName = "";
	private String sDescription = "";
	private long lStartTime = 0;
	private long lResumeTime = 0;
	private long lTotalTime = 0;
	private boolean bRecording = false;
	private boolean bClosed = false;
	private String sUID = "";
	private int iType = 0;
	private boolean bVisible = false;
	private double dVisited = DataTableTracks.TRACK_NOT_VISITED; 
		
	
	//methods
	public DataItemTrack(DataItemTrack item)
	{
		this.iID = item.iID;
		this.sName = item.sName;
		this.sDescription = item.sDescription;
		this.lStartTime = item.lStartTime;
		this.lResumeTime = item.lResumeTime;
		this.lTotalTime = item.lTotalTime;
		this.bRecording = item.bRecording;
		this.bClosed = item.bClosed;
		this.sUID = item.sUID;
		this.iType = item.iType;
		this.bVisible = item.bVisible;
		this.dVisited = item.dVisited;
		
		this.dDistance = item.dDistance;
	}
	
	public DataItemTrack(final Cursor cr)
	{
		this.iID = cr.getLong(DataTableTracks.field.ID);
		this.sName = cr.getString(DataTableTracks.field.Name);
		this.sDescription = cr.getString(DataTableTracks.field.Description);
		this.lStartTime = cr.getLong(DataTableTracks.field.StartTime);
		this.lResumeTime = cr.getLong(DataTableTracks.field.ResumeTime);
		this.lTotalTime = cr.getLong(DataTableTracks.field.TotalTime);
		this.bRecording = (cr.getInt(DataTableTracks.field.Recording) == 1);
		this.bClosed = (cr.getInt(DataTableTracks.field.Closed) == 1);
		this.sUID = cr.getString(DataTableTracks.field.UID);
		this.iType = cr.getInt(DataTableTracks.field.Type);
		this.bVisible = (cr.getInt(DataTableTracks.field.Visible) == 1);		
		this.dVisited = cr.getDouble(DataTableTracks.field.Visited);
	}	

	public String getName()
	{
		return sName;
	}

	public int getType()
	{
		return iType;
	}

	public static int boolToType(boolean bWalk, boolean bBike)
	{
		int iType = 0;
		
		if (bWalk)
			iType |= TYPE_BIT_WALK; 
		if (bBike)
			iType |= TYPE_BIT_BIKE; 
		
		if (iType == 0)
			iType = TYPE_BIT_WALK;
			
		return iType;
	}

	public static boolean isTypeWalk(int iType)
	{
		return ((iType & TYPE_BIT_WALK) == TYPE_BIT_WALK);
	}

	public static boolean isTypeBike(int iType)
	{
		return ((iType & TYPE_BIT_BIKE) == TYPE_BIT_BIKE);
	}

	public static String trackTypeToString(Context context, int iType)
	{		
		String sType = "";
		
		if (DataItemTrack.isTypeWalk(iType))
			sType += context.getString(R.string.labelTrackTypeWalk);
		
		if (DataItemTrack.isTypeBike(iType))
			sType += ((sType.length() == 0)?"":", ") + context.getString(R.string.labelTrackTypeBike);
		
		return sType;
	}	
	
	public String getDescription()
	{
		return sDescription;
	}
	
	public boolean isRecording()
	{
		return bRecording;
	}

	public boolean isClosed()
	{
		return bClosed;
	}

	public boolean isVisible()
	{
		return bVisible;
	}
	
	public long getTotalTimePeriodFromResume()
	{
  	final long lTimeNowMs = Calendar.getInstance().getTimeInMillis();		
		return lTotalTime + (lTimeNowMs - lResumeTime);
	}
	
	public int getTotalTimeInSeconds()
	{		
		if (bRecording)
		{
			return (int)((float)getTotalTimePeriodFromResume() / 1000.0f);			
		} else {
			return (int)((float)lTotalTime / 1000.0f);
		}			
	}
	
	public String getTotalTimeAsString()
	{
		final int iTimeInSeconds = getTotalTimeInSeconds();		
		return Utils.getTimeAsString(iTimeInSeconds);
	}
	
	public String getStatus(Context context)
	{
		String sStatus = "";
		if (!bClosed)		
		{			
			sStatus = context.getResources().getString(bRecording ? R.string.track_status_recording : R.string.track_status_paused);
		}
		return sStatus;		
	}

	public int getPointCount()
	{
		if (dataBase == null)
			return 0;
		
		final DataTableGeoPoints table = dataBase.tableGeoPoints();
		return table.getTrackPointCount(iID);
	}

	public int getTotalPointCount()
	{
		if (dataBase == null)
			return 0;
		
		final DataTableGeoPoints table = dataBase.tableGeoPoints();
		return table.getCount();
	}

	public String getStartTimeAsString()
	{
		return Utils.getDateTimeAsString(lStartTime);
	}
	
}
