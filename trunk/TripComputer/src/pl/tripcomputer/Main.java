package pl.tripcomputer;

import java.util.ArrayList;

import pl.tripcomputer.activities.ActivityAbout;
import pl.tripcomputer.activities.ActivityAddWaypoint;
import pl.tripcomputer.activities.ActivityExport;
import pl.tripcomputer.activities.ActivityFindWaypoints;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.activities.ActivityNewTrack;
import pl.tripcomputer.activities.ActivityServiceAccess;
import pl.tripcomputer.activities.ActivitySettings;
import pl.tripcomputer.activities.ActivityTrackDetails;
import pl.tripcomputer.activities.ActivityTracks;
import pl.tripcomputer.activities.ActivityWaypointDetails;
import pl.tripcomputer.activities.ActivityWaypointUpload;
import pl.tripcomputer.activities.ActivityWaypoints;
import pl.tripcomputer.activities.ActivityWaypointsDownload;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.http.ParamsGetWaypointList;
import pl.tripcomputer.ui.StatusMessage;
import pl.tripcomputer.webservice.AccessWaypointData;


public class Main
{
  //fields
	public final static String WEB_PAGE = "http://vetch.magot.pl/tripcomputer/";
			
  //fields
	private CommonActivity mParent = null;
	private int iLastCmd = Command.CMD_NONE;

  
  //methods
	public Main(CommonActivity parent)
	{
		mParent = parent;
	}
  	
  public synchronized int getLastCommand()
  {
  	return iLastCmd;
  }

	public synchronized boolean runCommand(int cmd_id)
	{
		return runCommand(cmd_id, new CommandData(CommandData.MODE_NONE));
	}
  
	public synchronized boolean runCommand(int cmd_id, CommandData data)
	{
		Command cmd = Command.get(cmd_id);
		if (cmd != null)
			return runCommand(cmd, data);
		return false;		
	}
	
	public synchronized boolean runCommand(Command cmd, CommandData data)
	{
		iLastCmd = cmd.iID;
		
		switch (cmd.iID)
		{		
		case Command.CMD_ZOOM_IN: return cmd_Zoom(cmd, data);
		case Command.CMD_ZOOM_OUT: return cmd_Zoom(cmd, data);
		case Command.CMD_ZOOM_LOCATION: return cmd_Zoom(cmd, data);
		
		case Command.CMD_ADD_WAYPOINT: return cmd_AddWaypoint(cmd, data);
		case Command.CMD_SELECT_TRACK: return cmd_SelectTrack(cmd, data);
		case Command.CMD_SELECT_WAYPOINT: return cmd_SelectWaypoint(cmd, data);
		case Command.CMD_CHANGE_MODE: return cmd_ChangeMode(cmd, data);
		
		case Command.CMD_TRACKS: return cmd_Tracks(cmd, data);
		case Command.CMD_WAYPOINTS: return cmd_Waypoints(cmd, data);
		case Command.CMD_TRACK_DETAILS: return cmd_TrackDetails(cmd, data);
		case Command.CMD_WAYPOINT_DETAILS: return cmd_WaypointDetails(cmd, data);
		case Command.CMD_SETTINGS: return cmd_Settings(cmd, data);

		case Command.CMD_NEW_TRACK: return cmd_NewTrack(cmd, data);
		case Command.CMD_TRACK_EDIT: return cmd_TrackEdit(cmd, data);
		case Command.CMD_TRACK_DELETE: return cmd_TrackDelete(cmd, data);
		case Command.CMD_TRACK_PAUSE: return cmd_TrackPause(cmd, data);
		case Command.CMD_TRACK_RESUME: return cmd_TrackResume(cmd, data);
		case Command.CMD_TRACK_STOP: return cmd_TrackStop(cmd, data);
		
		case Command.CMD_TRACK_SHOW: return cmd_TrackShow(cmd, data);
		case Command.CMD_TRACK_HIDE: return cmd_TrackHide(cmd, data);

		case Command.CMD_ABOUT: return cmd_About(cmd, data);

		case Command.CMD_WAYPOINT_EDIT: return cmd_WaypointEdit(cmd, data);
		case Command.CMD_WAYPOINT_UPLOAD: return cmd_WaypointUpload(cmd, data);
		case Command.CMD_WAYPOINT_DELETE: return cmd_WaypointDelete(cmd, data);

		case Command.CMD_WAYPOINTS_SEARCH: return cmd_WaypointsSearch(cmd, data);		
		case Command.CMD_SET_SERVICE_ACCESS: return cmd_SetServiceAccess(cmd, data);
		case Command.CMD_WAYPOINTS_DOWNLOAD: return cmd_WaypointsDownload(cmd, data);
		
		case Command.CMD_DATA_EXPORT_TRACK: return cmd_DataExportTrack(cmd, data);
		}

		StatusMessage.showText(cmd.sName);
		
		return false;		
	}
	
	//COMMANDS
	//--------------------------------------
	private boolean cmd_Zoom(Command cmd, CommandData data)
	{		
		if (cmd.iID == Command.CMD_ZOOM_IN)
		{
			ActivityMain.loader.viewPortZoomIn();
			StatusMessage.showText(cmd.sName);			
			return true;
		}
		if (cmd.iID == Command.CMD_ZOOM_OUT)
		{
			ActivityMain.loader.viewPortZoomOut();
			StatusMessage.showText(cmd.sName);			
			return true;
		}
		if (cmd.iID == Command.CMD_ZOOM_LOCATION)
		{
			ActivityMain.loader.switchLocationState();
			return true;
		}
		return false;
	}

	private boolean cmd_AddWaypoint(Command cmd, CommandData data)
	{		
		data = CommandData.setModeInsert(data);
		mParent.showActivity(ActivityAddWaypoint.class, cmd.iID, data);		
		return true;
	}

	private boolean cmd_NewTrack(Command cmd, CommandData data)
	{		
		data = CommandData.setModeInsert(data);
		mParent.showActivity(ActivityNewTrack.class, cmd.iID, data);
		return true;
	}
	
	private boolean cmd_SelectTrack(Command cmd, CommandData data)
	{
		ActivityMain.loader.selectNextTrack();
		return true;
	}

	private boolean cmd_SelectWaypoint(Command cmd, CommandData data)
	{
		ActivityMain.loader.selectNextWaypoint();
		return true;
	}

	private boolean cmd_ChangeMode(Command cmd, CommandData data)
	{
		mParent.getMainState().changeUiMode();
		
		final int iCurrentMode = mParent.getMainState().getCurrentUiMode();
		final Command cmdMode = Command.get(iCurrentMode);		
		if (cmdMode != null)
		{
			StatusMessage.showText(cmdMode.sName);
			return true;
		}
		
		return false;		
	}
	
	private boolean cmd_TrackEdit(Command cmd, CommandData data)
	{
		data = CommandData.setModeEdit(data);
		mParent.showActivity(ActivityNewTrack.class, cmd.iID, data);
		return true;
	}

	private boolean cmd_TrackDelete(Command cmd, CommandData data)
	{			
		final long lRowId = data.getRowId();
		if (lRowId == -1)
		{
			UserAlert.show(mParent, UserAlert.Result.MORE_DATA_REQUIRED);
			return false;
		}
		
		return mParent.getDatabase().tableTracks().deleteTrack(lRowId);
	}

	private boolean cmd_TrackPause(Command cmd, CommandData data)
	{
		final long lRowId = data.getRowId();
		if (lRowId == -1)
		{
			UserAlert.show(mParent, UserAlert.Result.MORE_DATA_REQUIRED);
			return false;
		}

		return mParent.getDatabase().tableTracks().pauseRecording(lRowId, false);
	}

	private boolean cmd_TrackResume(Command cmd, CommandData data)
	{
		final long lRowId = data.getRowId();
		if (lRowId == -1)
		{
			UserAlert.show(mParent, UserAlert.Result.MORE_DATA_REQUIRED);
			return false;
		}

		return mParent.getDatabase().tableTracks().resumeRecording(lRowId);
	}
		
	private boolean cmd_TrackStop(Command cmd, CommandData data)
	{
		final long lRowId = data.getRowId();
		if (lRowId == -1)
		{
			UserAlert.show(mParent, UserAlert.Result.MORE_DATA_REQUIRED);
			return false;
		}

		return mParent.getDatabase().tableTracks().pauseRecording(lRowId, true);
	}
	
	private boolean cmd_TrackShow(Command cmd, CommandData data)
	{
		final long lRowId = data.getRowId();
		if (lRowId == -1)
		{
			UserAlert.show(mParent, UserAlert.Result.MORE_DATA_REQUIRED);
			return false;
		}

		return mParent.getDatabase().tableTracks().setVisibility(lRowId, true);
	}

	private boolean cmd_TrackHide(Command cmd, CommandData data)
	{
		final long lRowId = data.getRowId();
		if (lRowId == -1)
		{
			UserAlert.show(mParent, UserAlert.Result.MORE_DATA_REQUIRED);
			return false;
		}

		return mParent.getDatabase().tableTracks().setVisibility(lRowId, false);
	}
	
	private boolean cmd_Tracks(Command cmd, CommandData data)
	{
		mParent.showActivity(ActivityTracks.class, cmd.iID, data);
		return true;
	}

	private boolean cmd_Waypoints(Command cmd, CommandData data)
	{		
		mParent.showActivity(ActivityWaypoints.class, cmd.iID, data);
		return false;
	}

	private boolean cmd_TrackDetails(Command cmd, CommandData data)
	{				
		mParent.showActivity(ActivityTrackDetails.class, cmd.iID, data);
		return true;		
	}

	private boolean cmd_WaypointDetails(Command cmd, CommandData data)
	{
		mParent.showActivity(ActivityWaypointDetails.class, cmd.iID, data);
		return false;		
	}
	
	private boolean cmd_Settings(Command cmd, CommandData data)
	{
		mParent.showActivity(ActivitySettings.class, cmd.iID, data);
		return false;		
	}

	private boolean cmd_About(Command cmd, CommandData data)
	{
		mParent.showActivity(ActivityAbout.class, cmd.iID, data);
		return false;		
	}

	private boolean cmd_WaypointEdit(Command cmd, CommandData data)
	{	
		data = CommandData.setModeEdit(data);
		mParent.showActivity(ActivityAddWaypoint.class, cmd.iID, data);
		return true;
	}

	private boolean cmd_WaypointDelete(Command cmd, CommandData data)
	{			
		final long lRowId = data.getRowId();
		if (lRowId == -1)
		{
			UserAlert.show(mParent, UserAlert.Result.MORE_DATA_REQUIRED);
			return false;
		}
		
		return mParent.getDatabase().tableWaypoints().deleteWaypoint(lRowId);
	}

	private boolean cmd_SetServiceAccess(Command cmd, CommandData data)
	{
		mParent.showActivity(ActivityServiceAccess.class, cmd.iID, data);		
		return false;
	}

	//show service access config dialog
	public void showServiceAccess()
	{
		mParent.showActivity(ActivityServiceAccess.class, Command.CMD_SET_SERVICE_ACCESS, new CommandData(CommandData.MODE_NONE));		
	}
	
	public void showWaypointUpload()
	{
		mParent.showActivity(ActivityWaypointUpload.class, Command.CMD_WAYPOINT_UPLOAD, new CommandData(CommandData.MODE_NONE));
	}
	
	public void showWaypointsDownload(ArrayList<DataItemWaypoint> items, ParamsGetWaypointList params)
	{
		ActivityWaypointsDownload.itemsWaypoints = items;
		ActivityWaypointsDownload.findParams = new ParamsGetWaypointList(params);
		
		mParent.showActivity(ActivityWaypointsDownload.class, Command.CMD_WAYPOINTS_DOWNLOAD, new CommandData(CommandData.MODE_NONE));		
	}
	
	private boolean cmd_WaypointUpload(Command cmd, CommandData data)
	{		
		AccessWaypointData.execute(mParent, cmd, data);		
		return false;
	}

	private boolean cmd_WaypointsSearch(Command cmd, CommandData data)
	{
		//get current location
		if (ActivityFindWaypoints.gpsLocationReady(mParent))
		{
			mParent.showActivity(ActivityFindWaypoints.class, Command.CMD_WAYPOINTS_SEARCH, new CommandData(CommandData.MODE_NONE));
		}
		return false;
	}
	
	private boolean cmd_WaypointsDownload(Command cmd, CommandData data)
	{
		mParent.showActivity(ActivityWaypointsDownload.class, Command.CMD_WAYPOINTS_DOWNLOAD, new CommandData(CommandData.MODE_NONE));		
		return false;
	}

	private boolean cmd_DataExportTrack(Command cmd, CommandData data)
	{
		if (data != null)
			ActivityExport.setExportTypeTrack(data);
		
		mParent.showActivity(ActivityExport.class, Command.CMD_DATA_EXPORT_TRACK, data);
		return false;
	}

}
