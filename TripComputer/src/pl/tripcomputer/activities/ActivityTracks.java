package pl.tripcomputer.activities;

import java.util.ArrayList;

import pl.tripcomputer.Command;
import pl.tripcomputer.CommandData;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemTrack;
import pl.tripcomputer.lists.TrackListAdapter;
import pl.tripcomputer.lists.TrackListItem;
import pl.tripcomputer.loader.TrackItem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;


public class ActivityTracks extends CommonActivity
{
	//fields
  private ListView list = null;
  private LinearLayout layEmpty = null;
  private TrackListAdapter adapterTrackList = null;
  private DataItemTrack itemSelected = null; 
      
  
	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_tracks);	  

	  setSubTitle(R.string.title_tracks);
	  	  
	  list = (ListView)this.findViewById(R.id.list);
	  layEmpty = (LinearLayout)this.findViewById(R.id.layEmpty);
	  	  
	  //initialize tracks list
	  list.setOnItemClickListener(eventListItemClicked);
	  
	  //context menu
	  this.registerForContextMenu(list);

	  //fill list
	  reloadList();
	}

	private void reloadList()
	{		
	  ArrayList<TrackListItem> listData = new ArrayList<TrackListItem>();
	  	  
	  final ArrayList<Long> listIDs = ActivityMain.loader.getTrackList();
	  if (list != null)
	  {
		  for (Long lTrackId : listIDs)
		  {
		  	final TrackItem trackItem = ActivityMain.loader.getTrackItem(lTrackId);
				if (trackItem != null)
				{
					synchronized(trackItem.dataTrack)
					{
						trackItem.updateTotalDistance();
						listData.add(new TrackListItem(this, trackItem.dataTrack));
					}
				}
		  }
	  }
		
	  adapterTrackList = new TrackListAdapter(this, listData);	  
	  list.setAdapter(adapterTrackList);
	  updateView();
	}

	public void updateView()
	{
		if (adapterTrackList.isEmpty())
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
	    final TrackListItem item = (TrackListItem)adapterTrackList.getItem(position);
	    if (item != null)
	    {
    		CommandData cmdData = new CommandData(CommandData.MODE_VIEW, item.iID);
    		main.runCommand(Command.CMD_TRACK_DETAILS, cmdData);
	    }
		}		
	};
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);

		boolean bMenuItemDelete = false;
		boolean bMenuItemPause = false;
		boolean bMenuItemResume = false;
		
		itemSelected = null;		
		final int iItemIndex = getItemIndexSelectedForContextMenu(menuInfo);
		if (iItemIndex != -1)
		{
			itemSelected = (DataItemTrack)adapterTrackList.getItem(iItemIndex);
			if (itemSelected != null)
			{
		    menu.setHeaderTitle(itemSelected.getName());
		 
				if (itemSelected.isClosed())
				{
					bMenuItemDelete = true;
				} else {
					if (itemSelected.isRecording())
					{
						bMenuItemPause = true;
					} else {
						bMenuItemResume = true;
					}
				}				
			}
		}		
				
		Command.addMenuItem(menu, Command.CMD_TRACK_EDIT, 0);
		
		if (bMenuItemDelete)
			Command.addMenuItem(menu, Command.CMD_DATA_EXPORT_TRACK, 0);
				
		if (bMenuItemDelete)
			Command.addMenuItem(menu, Command.CMD_TRACK_DELETE, 0);
				
		if (bMenuItemPause)
			Command.addMenuItem(menu, Command.CMD_TRACK_PAUSE, 0);
		
		if (bMenuItemResume)
			Command.addMenuItem(menu, Command.CMD_TRACK_RESUME, 0);

		if (bMenuItemPause || bMenuItemResume)
			Command.addMenuItem(menu, Command.CMD_TRACK_STOP, 0);
		
	}

	public boolean onContextItemSelected(MenuItem item)
	{
		final int iSelectedCMD = item.getItemId();
		
		if (iSelectedCMD == Command.CMD_TRACK_DELETE)
		{
			UserAlert.question(this, R.string.alert_question_delete_track, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					if (which == DialogInterface.BUTTON_POSITIVE)
					{
						CommandData cmdData = new CommandData(CommandData.MODE_NONE, itemSelected.iID);						
						if (main.runCommand(Command.CMD_TRACK_DELETE, cmdData))
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
		if (requestCode == Command.CMD_NEW_TRACK)
		{
			reloadList();
		}
		if (requestCode == Command.CMD_TRACK_EDIT)
		{
			reloadList();
		}
		if (requestCode == Command.CMD_TRACK_DETAILS)
		{
			reloadList();
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Command.addMenuItem(menu, Command.CMD_NEW_TRACK, R.drawable.ic_menu_add);		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		final int iSelectedCMD = item.getItemId();
		CommandData data = new CommandData(CommandData.MODE_NONE);
		return main.runCommand(iSelectedCMD, data);
	}
			
}
