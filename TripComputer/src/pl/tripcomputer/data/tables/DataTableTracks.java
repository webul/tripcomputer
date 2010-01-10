package pl.tripcomputer.data.tables;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

import pl.tripcomputer.UserAlert;
import pl.tripcomputer.data.common.DataField;
import pl.tripcomputer.data.common.DataRowSet;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataTableOperation;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.data.items.DataItemTrack;
import pl.tripcomputer.service.ServiceCommand;

import android.database.Cursor;


public class DataTableTracks extends DataTable
{
	//generic collection class
	public class RowSet extends DataRowSet<DataItemTrack>
	{
		public RowSet(DataTable table)
		{
			super(table);
		}
		protected DataItemTrack getItem(Cursor cr)
		{
			return new DataItemTrack(cr);
		}		
	}

	protected DataRowSet<DataItemTrack> rows = null;
	
	
	//fields
	public final static String sTableName = "Tracks";
	
	//distance of track visited. -1 for new private track, 0 for others track
	public final static double TRACK_NOT_VISITED = 0;
	public final static double TRACK_VISITED = -1;
	

	public static class field
	{
		public static final int ID = 0;
		public static final int Name = 1;
		public static final int Description = 2;
		public static final int StartTime = 3;
		public static final int ResumeTime = 4;
		public static final int TotalTime = 5;
		public static final int Recording = 6;
		public static final int Closed = 7;		
		public static final int UID = 8;
		public static final int Type = 9;
		public static final int Visible = 10;
		public static final int Visited = 11;		
	};	
	
	private final static DataField[] vecTableDefinition =
	{
		new DataField(field.ID, DataTable.sID, DataField.TYPE_INT, false, true, false),
		new DataField(field.Name, "Name", DataField.TYPE_TEXT, false, false, true),
		new DataField(field.Description, "Description", DataField.TYPE_TEXT, true, false, true),
		new DataField(field.StartTime, "StartTime", DataField.TYPE_INT, false, false, false),
		new DataField(field.ResumeTime, "ResumeTime", DataField.TYPE_INT, false, false, false),
		new DataField(field.TotalTime, "TotalTime", DataField.TYPE_INT, false, false, false),
		new DataField(field.Recording, "Recording", DataField.TYPE_BOOL, false, false, false),
		new DataField(field.Closed, "Closed", DataField.TYPE_BOOL, false, false, false),		
		new DataField(field.UID, "UID", DataField.TYPE_TEXT, false, false, false),
		new DataField(field.Type, "Type", DataField.TYPE_INT, false, false, true),
		new DataField(field.Visible, "Visible", DataField.TYPE_BOOL, false, false, false),
		new DataField(field.Visited, "Visited", DataField.TYPE_NUMERIC, false, false, false),
	};
	

	//methods
	public DataTableTracks(Database dataBase)
	{
		super(dataBase, sTableName, vecTableDefinition);
		rows = new RowSet(this);
	}

	public DataValues getNewValues(boolean bForInsert)
	{
		final DataValues values = new DataValues(this);
		
		if (bForInsert)
		{		
			final long lTimeNowMs = Calendar.getInstance().getTimeInMillis();
			
			values.setValue(field.StartTime, lTimeNowMs);
			values.setValue(field.ResumeTime, lTimeNowMs);
			values.setValue(field.TotalTime, 0);
			
			values.setValue(field.Recording, false);
			values.setValue(field.Closed, false);
			
			values.setValue(field.UID, UUID.randomUUID().toString());
			values.setValue(field.Type, (int)0);
			values.setValue(field.Visible, true);
			values.setValue(field.Visited, TRACK_VISITED);		
		}
		
		return values;
	}
	
	public int getTableVersion()
	{
		return 1;
	}
	
	public UserAlert.Result validateValues(DataValues values)
	{
		//all ok
		return null;
	}
	
	public DataItemTrack getTrackItem(long lTrackId)
	{		
		final Cursor cr = getDataRecord(lTrackId);
		if (cr != null)
		{				
			DataItemTrack item = new DataItemTrack(cr);			
			cr.close();
			return item;
		}
		return null;
	}
	
	//updates field TotalTime with time period from now to last resume 
	public boolean pauseRecording(long lTrackId, boolean bCloseTrack)
	{
		final DataItemTrack item = getTrackItem(lTrackId);
		if (!item.isClosed())
		{			
			DataValues values = new DataValues(this);
		
			if (item.isRecording())
			{
				values.setValue(field.TotalTime, item.getTotalTimePeriodFromResume());				
			}
			
			if (bCloseTrack)
			{
				values.setValue(field.Closed, true);
			}

			values.setValue(field.Recording, false);
			
			if (dataUpdate(values, lTrackId))
			{
		  	sendOperation(lTrackId, DataTableOperation.OP_UPDATE, ServiceCommand.CMD_TRACK_RECORDING_PAUSE, item.getName());				
				return true;
			}			
		}
		return false;
	}
		
	//updates field ResumeTime with current time
	public boolean resumeRecording(long lTrackId)
	{		
		final DataItemTrack item = getTrackItem(lTrackId);
		if (!item.isClosed())
		{			
			DataValues values = new DataValues(this);

	  	final long lTimeNowMs = Calendar.getInstance().getTimeInMillis();

			values.setValue(field.Recording, true);
			values.setValue(field.ResumeTime, lTimeNowMs);

			if (dataUpdate(values, lTrackId))
			{
		  	sendOperation(lTrackId, DataTableOperation.OP_UPDATE, ServiceCommand.CMD_TRACK_RECORDING_RESUME, item.getName());		  				
				return true;				
			}
		}
		return false;
	}
	
	//returns track item that can be resumed or paused
	public DataItemTrack getOpenTrackItem()
	{
		final String sWhere = String.format("%s = 0", getFieldName(field.Closed));
		
		final Cursor cr = getDataRecords(sWhere);
		if (cr != null)
		{
			DataItemTrack item = new DataItemTrack(cr);
			cr.close();
			return item;
		}
		
		return null;
	}
	
	public boolean isTrackClosed(long lTrackId)
	{
		final DataItemTrack item = getTrackItem(lTrackId);
		return (item.isClosed());
	}

	public boolean isTrackRecording(long lTrackId)
	{
		final DataItemTrack item = getTrackItem(lTrackId);
		if (!item.isClosed())
			return item.isRecording();
		return false;
	}
	
	public boolean closeOpenTrack()
	{
		boolean bOpenedTrackClosed = false;
		
		final DataItemTrack item = getOpenTrackItem();
		if (item == null)
		{	  		
			bOpenedTrackClosed = true;	  		
		} else {
			if (pauseRecording(item.iID, true))
			{
  			bOpenedTrackClosed = true;	    	  	
			}
		}
		
		return bOpenedTrackClosed;
	}

	public boolean deleteTrack(long lTrackId)
	{
		final DataTableGeoPoints tableGeoPoints = dataBase.tableGeoPoints();
		
		boolean bSuccess = false;
		
		dataBase.transactionBegin();
	  try
	  {
			String sWhere = tableGeoPoints.getFieldName(DataTableGeoPoints.field.TrackID) + " = " + Long.toString(lTrackId);
			
			boolean bPointsDeleted = false;
			if (tableGeoPoints.getCount(sWhere) == 0)
			{
				bPointsDeleted = true;
			} else {
				bPointsDeleted = tableGeoPoints.dataDelete(sWhere);
			}
			
			if (bPointsDeleted)
			{
				if (dataDelete(lTrackId))
				{
					dataBase.transactionCommit();
					bSuccess = true;					
				}
			}
			
	  } finally {
	  	dataBase.transactionEnd();
	  }
	  
	  if (bSuccess)
	  {
	  	sendOperation(lTrackId, DataTableOperation.OP_DELETE);
	  }
	  
		return bSuccess;
	}
	
	public boolean setVisibility(long lTrackId, boolean bTrackVisible)
	{
		boolean bSuccess = false;
		
		final DataItemTrack item = getTrackItem(lTrackId);
		if (item != null)
		{
			DataValues values = new DataValues(this);
							
			values.setValue(field.Visible, bTrackVisible);
				
			bSuccess = dataUpdate(values, lTrackId);
			
			if (bSuccess)
			{
				sendOperation(lTrackId, DataTableOperation.OP_UPDATE);
			}	  	 		
		}
		return bSuccess;
	}
	
	private ArrayList<Long> getVisibleTracks()
	{
		ArrayList<Long> list = new ArrayList<Long>(); 
		
		final String sOrderBy = getFieldName(field.StartTime) + " desc";
		
		final ArrayList<DataItemTrack> items = rows.getRecords(null, sOrderBy);		
		if (items != null)
		{
			for (DataItemTrack dataTrackItem : items)
			{
				if (dataTrackItem.isVisible())
				{
					list.add(dataTrackItem.iID);
				}
			}
		}
		
		return list;
	}
	
	public ArrayList<Long> getVisibleTracksRotated(long lFirstTrackId)
	{
		final ArrayList<Long> list = getVisibleTracks();
		
		//get selected track position
		int iListIndex = list.indexOf(lFirstTrackId);
		if (iListIndex != -1)
		{
			//shift visible tracks list, selected track to zero position
			Collections.rotate(list, -iListIndex);			
		}
		
		return list;
	}	
	
	public void sendOperationForNewTrack(long lOldTrackId, long lNewTrackId)
	{
		final DataItemTrack itemOld = getTrackItem(lOldTrackId);
		final DataItemTrack itemNew = getTrackItem(lNewTrackId);
		
		if (itemOld != null)
		{		
			//pause old track
			sendOperation(itemOld.iID, DataTableOperation.OP_UPDATE, ServiceCommand.CMD_TRACK_RECORDING_PAUSE, itemOld.getName());
		}
		
		if (itemNew != null)
		{	
			//insert new track
			sendOperation(itemNew.iID, DataTableOperation.OP_INSERT);
			
			//resume recording of new track
			sendOperation(itemNew.iID, DataTableOperation.OP_UPDATE, ServiceCommand.CMD_TRACK_RECORDING_RESUME, itemNew.getName());
		}
	}
	
}
