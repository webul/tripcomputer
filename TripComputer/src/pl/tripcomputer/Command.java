package pl.tripcomputer;

import java.util.ArrayList;
import pl.tripcomputer.R;
import android.content.Context;
import android.view.Menu;
import android.view.SubMenu;
import android.view.MenuItem;


public class Command
{
	//fields
	public int iID = CMD_NONE;
	public String sName = "";
	
	//commands list
	private static ArrayList<Command> commands = new ArrayList<Command>();
	

	//commands	
	public final static int CMD_NONE = 0;

	public final static int CMD_ZOOM_IN = 1;
	public final static int CMD_ZOOM_OUT = 2;
	public final static int CMD_ZOOM_LOCATION = 3;
	
	public final static int CMD_ADD_WAYPOINT = 4;
	public final static int CMD_SELECT_TRACK = 5;
	public final static int CMD_SELECT_WAYPOINT = 6;
	public final static int CMD_CHANGE_MODE = 7;

	public final static int CMD_TRACKS = 8;
	public final static int CMD_WAYPOINTS = 9;
	public final static int CMD_TRACK_DETAILS = 10;
	public final static int CMD_WAYPOINT_DETAILS = 11;
	public final static int CMD_SETTINGS = 12;
	
	public final static int CMD_NEW_TRACK = 13;
	public final static int CMD_TRACK_EDIT = 14;
	public final static int CMD_TRACK_DELETE = 15;
	public final static int CMD_TRACK_PAUSE = 16;
	public final static int CMD_TRACK_RESUME = 17;
	public final static int CMD_TRACK_STOP = 18;
	public final static int CMD_TRACK_SHOW = 19;
	public final static int CMD_TRACK_HIDE = 20;
	public final static int CMD_ABOUT = 21;
	public final static int CMD_TOOLS = 22;

	public final static int CMD_WAYPOINTS_SEARCH = 23;	
	public final static int CMD_SET_SERVICE_ACCESS = 24;	
	public final static int CMD_WAYPOINTS_DOWNLOAD = 25;	

	public final static int CMD_DATA_EXPORT_TRACK = 26;
		
	//other	
	public final static int CMD_WAYPOINT_EDIT = 50;
	public final static int CMD_WAYPOINT_UPLOAD = 51;
	public final static int CMD_WAYPOINT_DELETE = 52;
	
	//modes	
	public final static int CMD_MODE_TRACKS = 100;
	public final static int CMD_MODE_COMPASS = 101;	
	public final static int CMD_MODE_LOC_TO_SEL_WPT = 102;
	public final static int CMD_MODE_LOC_TO_SEL_TRACK = 103;
	public final static int CMD_MODE_INFO = 104;
	public final static int CMD_MODE_LAST = 105;
	
	//methods
	private Command(Context context, int iID, int idResName)
	{		
		this.iID = iID;
		sName = context.getString(idResName);		
	}
	
	public static void init(Context context)
	{
		commands.add(new Command(context, Command.CMD_ZOOM_IN, R.string.cmd_zoom_in));
		commands.add(new Command(context, Command.CMD_ZOOM_OUT, R.string.cmd_zoom_out));
		commands.add(new Command(context, Command.CMD_ZOOM_LOCATION, R.string.cmd_zoom_location));
		
		commands.add(new Command(context, Command.CMD_ADD_WAYPOINT, R.string.cmd_add_waypoint));
		commands.add(new Command(context, Command.CMD_SELECT_TRACK, R.string.cmd_select_track));
		commands.add(new Command(context, Command.CMD_SELECT_WAYPOINT, R.string.cmd_select_waypoint));
		commands.add(new Command(context, Command.CMD_CHANGE_MODE, R.string.cmd_change_mode));

		commands.add(new Command(context, Command.CMD_TRACKS, R.string.cmd_tracks));
		commands.add(new Command(context, Command.CMD_WAYPOINTS, R.string.cmd_waypoints));
		commands.add(new Command(context, Command.CMD_TRACK_DETAILS, R.string.cmd_track_details));
		commands.add(new Command(context, Command.CMD_WAYPOINT_DETAILS, R.string.cmd_waypoint_details));
		commands.add(new Command(context, Command.CMD_SETTINGS, R.string.cmd_settings));
		
		commands.add(new Command(context, Command.CMD_NEW_TRACK, R.string.cmd_new_track));
		commands.add(new Command(context, Command.CMD_TRACK_EDIT, R.string.cmd_track_edit));
		commands.add(new Command(context, Command.CMD_TRACK_DELETE, R.string.cmd_track_delete));
		commands.add(new Command(context, Command.CMD_TRACK_PAUSE, R.string.cmd_track_pause));
		commands.add(new Command(context, Command.CMD_TRACK_RESUME, R.string.cmd_track_resume));		
		commands.add(new Command(context, Command.CMD_TRACK_STOP, R.string.cmd_track_stop));

		commands.add(new Command(context, Command.CMD_TRACK_SHOW, R.string.cmd_track_show));
		commands.add(new Command(context, Command.CMD_TRACK_HIDE, R.string.cmd_track_hide));
		
		commands.add(new Command(context, Command.CMD_ABOUT, R.string.cmd_about));
		commands.add(new Command(context, Command.CMD_TOOLS, R.string.cmd_tools));
		
		commands.add(new Command(context, Command.CMD_WAYPOINTS_SEARCH, R.string.cmd_waypoints_search));
		commands.add(new Command(context, Command.CMD_WAYPOINTS_DOWNLOAD, R.string.cmd_waypoints_download));
		
		commands.add(new Command(context, Command.CMD_DATA_EXPORT_TRACK, R.string.cmd_data_export));
		
		commands.add(new Command(context, Command.CMD_SET_SERVICE_ACCESS, R.string.cmd_set_service_access));

		commands.add(new Command(context, Command.CMD_WAYPOINT_EDIT, R.string.cmd_waypoint_edit));
		commands.add(new Command(context, Command.CMD_WAYPOINT_UPLOAD, R.string.cmd_waypoint_upload));
		commands.add(new Command(context, Command.CMD_WAYPOINT_DELETE, R.string.cmd_waypoint_delete));
		
		commands.add(new Command(context, Command.CMD_MODE_TRACKS, R.string.cmd_mode_tracks));
		commands.add(new Command(context, Command.CMD_MODE_COMPASS, R.string.cmd_mode_compass));
		commands.add(new Command(context, Command.CMD_MODE_LOC_TO_SEL_WPT, R.string.cmd_mode_loc_to_sel_wpt));
		commands.add(new Command(context, Command.CMD_MODE_LOC_TO_SEL_TRACK, R.string.cmd_mode_loc_to_sel_track));
		commands.add(new Command(context, Command.CMD_MODE_INFO, R.string.cmd_mode_info));
		
	}
	
	public static Command get(int cmd_id)
	{
		for (int i = 0; i < commands.size(); i++)
		{
			Command command = commands.get(i);
			if (command != null)
				if (command.iID == cmd_id)
					return command;				
		}
		return null;
	}

	public static String getName(int cmd_id)
	{
		for (int i = 0; i < commands.size(); i++)
		{
			Command command = commands.get(i);
			if (command != null)
				if (command.iID == cmd_id)
					return command.sName;				
		}
		return "";
	}

	public static MenuItem addMenuItem(Menu menu, int cm_id, int icon_res_id)
	{
		MenuItem item = menu.add(0, cm_id, Menu.NONE, Command.getName(cm_id));
		if (item != null)			
			item.setIcon(icon_res_id);		
		return item;
	}

	public static SubMenu addSubMenuItem(Menu menu, int cm_id, int icon_res_id)
	{
		SubMenu item = menu.addSubMenu(0, cm_id, Menu.NONE, Command.getName(cm_id));
		if (item != null)			
			item.setIcon(icon_res_id);		
		return item;
	}
	
	public static MenuItem addMenuItem(Menu menu, int cm_id)
	{
		return menu.add(0, cm_id, Menu.NONE, Command.getName(cm_id));
	}

}
