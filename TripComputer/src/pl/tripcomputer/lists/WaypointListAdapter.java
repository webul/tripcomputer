package pl.tripcomputer.lists;

import java.util.ArrayList;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class WaypointListAdapter extends BaseAdapter
{
	//fields
	protected Context mContext = null;
	private ArrayList<WaypointListItem> waypointItems = null;


	//methods
	public WaypointListAdapter(Context context, ArrayList<WaypointListItem> waypointItems)
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

	public long getItemId(int position)
	{
		return waypointItems.get(position).iID;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		WaypointListItem item = waypointItems.get(position);
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

}
