package pl.tripcomputer.lists;

import java.util.ArrayList;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class TrackListAdapter extends BaseAdapter
{
	//fields
	protected Context mContext = null;
	private ArrayList<TrackListItem> trackItems = null;


	//methods
	public TrackListAdapter(Context context, ArrayList<TrackListItem> trackItems)
	{
		this.mContext = context;
		this.trackItems = trackItems;
	}

	public int getCount()
	{
		return trackItems.size();
	}

	public Object getItem(int position)
	{
		return trackItems.get(position);
	}

	public long getItemId(int position)
	{
		return trackItems.get(position).iID;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		TrackListItem item = trackItems.get(position);
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
		return (trackItems.size() == 0);
	}
	
}
