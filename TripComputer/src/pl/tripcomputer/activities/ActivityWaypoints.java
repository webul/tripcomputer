package pl.tripcomputer.activities;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import pl.tripcomputer.Command;
import pl.tripcomputer.CommandData;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.lists.WaypointListAdapter;
import pl.tripcomputer.lists.WaypointListItem;
import pl.tripcomputer.loader.WaypointItem;


public class ActivityWaypoints extends CommonActivity
{
	//fields
  private ListView list = null;
  private LinearLayout layEmpty = null;
  private WaypointListAdapter adapterWaypointList = null;
  private DataItemWaypoint itemSelected = null;  
  private Location locCurrent = null;
      
  
	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_waypoints);	  

	  setSubTitle(R.string.title_waypoints);

	  list = (ListView)this.findViewById(R.id.list);
	  layEmpty = (LinearLayout)this.findViewById(R.id.layEmpty);
	  	  
	  //initialize list
	  list.setOnItemClickListener(eventListItemClicked);
	  
	  //context menu
	  this.registerForContextMenu(list);

	  //fill list
	  reloadList();
	}

	private void reloadList()
	{	
		//get current location
		locCurrent = null;
	  final Location loc = ActivityMain.getLastLocation();
	  if (loc != null)
	  	locCurrent = new Location(loc);
		
	  //create list
	  ArrayList<WaypointListItem> listData = new ArrayList<WaypointListItem>();

	  final ArrayList<Long> listIDs = ActivityMain.loader.getWaypointList();
	  if (list != null)
	  {
		  for (Long lWaypointId : listIDs)
		  {
		  	final WaypointItem waypointItem = ActivityMain.loader.getWaypointItem(lWaypointId);
				if (waypointItem != null)
				{
					synchronized(waypointItem.dataWaypoint)
					{
						waypointItem.updateDistance(locCurrent);
						listData.add(new WaypointListItem(this, waypointItem.dataWaypoint));
					}
				}
		  }
	  }
		
	  adapterWaypointList = new WaypointListAdapter(this, listData);
	  list.setAdapter(adapterWaypointList);
	  updateView();
	}

	public void updateView()
	{
		if (adapterWaypointList.isEmpty())
		{
			layEmpty.setVisibility(View.VISIBLE);
		  list.setVisibility(View.GONE);
		} else {
		  layEmpty.setVisibility(View.GONE);
		  list.setVisibility(View.VISIBLE);
		}
	}
		
	public AdapterView.OnItemClickListener eventListItemClicked = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> adp, View view, int position, long id)
		{
	    final WaypointListItem item = (WaypointListItem)adapterWaypointList.getItem(position);
	    if (item != null)
	    {
    		CommandData cmdData = new CommandData(CommandData.MODE_VIEW, item.iID);
    		main.runCommand(Command.CMD_WAYPOINT_DETAILS, cmdData);
	    }
		}
	};
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);

		boolean bItemIsPublic = false;
		
		itemSelected = null;		
		final int iItemIndex = getItemIndexSelectedForContextMenu(menuInfo);
		if (iItemIndex != -1)
		{
			itemSelected = (DataItemWaypoint)adapterWaypointList.getItem(iItemIndex);
			if (itemSelected != null)
			{
				bItemIsPublic = itemSelected.isPublic();
				
		    menu.setHeaderTitle(itemSelected.getName());		    
			}
		}		

		//public item can not be edited
		if (!bItemIsPublic)
			Command.addMenuItem(menu, Command.CMD_WAYPOINT_EDIT, 0);
		
		//public item can not be send
		if (!bItemIsPublic)
			Command.addMenuItem(menu, Command.CMD_WAYPOINT_UPLOAD, 0);
		
		Command.addMenuItem(menu, Command.CMD_WAYPOINT_DELETE, 0);
	}

	public boolean onContextItemSelected(MenuItem item)
	{
		final int iSelectedCMD = item.getItemId();
		
		if (iSelectedCMD == Command.CMD_WAYPOINT_DELETE)
		{
			UserAlert.question(this, R.string.alert_question_delete_waypoint, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					if (which == DialogInterface.BUTTON_POSITIVE)
					{
						CommandData cmdData = new CommandData(CommandData.MODE_NONE, itemSelected.iID);						
						if (main.runCommand(Command.CMD_WAYPOINT_DELETE, cmdData))
							reloadList();
					}
				}
			});
			return true;
		}
		
		CommandData cmdData = new CommandData(CommandData.MODE_NONE, itemSelected.iID);		
		if (main.runCommand(item.getItemId(), cmdData))
			reloadList();
		
		return true;
	}
	
	protected void onRequestedActivityResultOK(int requestCode, Intent data)
	{
		if (requestCode == Command.CMD_ADD_WAYPOINT)
		{
			reloadList();
		}
		if (requestCode == Command.CMD_WAYPOINT_EDIT)
		{
			reloadList();
		}
		if (requestCode == Command.CMD_WAYPOINT_DETAILS)
		{
			reloadList();
		}
		if (requestCode == Command.CMD_WAYPOINT_UPLOAD)
		{
			reloadList();
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Command.addMenuItem(menu, Command.CMD_ADD_WAYPOINT, R.drawable.ic_menu_add);		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		final int iSelectedCMD = item.getItemId();
		CommandData data = new CommandData(CommandData.MODE_NONE);
		return main.runCommand(iSelectedCMD, data);
	}

}
