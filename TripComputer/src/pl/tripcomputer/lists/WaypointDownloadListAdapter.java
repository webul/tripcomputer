package pl.tripcomputer.lists;

import java.util.ArrayList;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class WaypointDownloadListAdapter extends BaseAdapter
{
	//fields
	protected Context mContext = null;
	private ArrayList<WaypointDownloadListItem> waypointItems = null;


	//methods
	public WaypointDownloadListAdapter(Context context, ArrayList<WaypointDownloadListItem> waypointItems)
	{
		this.mContext = context;
		this.waypointItems = waypointItems;
	}

	public int getCount()
	{
		return waypointItems.size();
	}

	public Object getItem(int position)
	{
		return waypointItems.get(position);
	}

	public WaypointDownloadListItem getWaypoint(int position)
	{
		return waypointItems.get(position);
	}

	public long getItemId(int position)
	{
		return waypointItems.get(position).iID;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		WaypointDownloadListItem item = waypointItems.get(position);
		if (item != null)
		{
			return item.getView(mContext);
		}
		return null;
	}

	public boolean areAllItemsEnabled()
	{
		return true;
	}

	public boolean isEnabled(int position)
	{
		return true;
	}
	
	public boolean hasStableIds()
	{
		return true;
	}
	
	public boolean isEmpty()
	{
		return (waypointItems.size() == 0);
	}

	public ArrayList<String> getSelectedItems()
	{
		final ArrayList<String> items = new ArrayList<String>();		
		for (int i = 0; i < getCount(); i++)
		{
			final WaypointDownloadListItem itemWpt = getWaypoint(i);
			
			if (itemWpt != null)
			{
				if (itemWpt.isCheckedToDownload())
					items.add(itemWpt.getUID());
			}						
		}
		return items;
	}
	
}
