package pl.tripcomputer.lists;

import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemTrack;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class TrackListItem extends DataItemTrack
{
	//fields
	private static LayoutInflater inflater = null;

	private CommonActivity parent = null;
	
	private View itemView = null;
	
	private TextView textName = null;
	private TextView textDistance = null;
	private TextView textTime = null;	
	private TextView textVisible = null;	
	private TextView textStatus = null;	
	
	
	//methods
	public TrackListItem(CommonActivity parent, DataItemTrack item)
	{
		super(item);
		initialize(parent);
		this.parent = parent;
	}
	
	private void initialize(CommonActivity parent)
	{
		if (inflater == null)
			inflater = LayoutInflater.from(parent);
		
		if (inflater != null)
			itemView = (View)inflater.inflate(R.layout.listitem_track, null);

		if (itemView != null)
		{
			textName = (TextView)itemView.findViewById(R.id.TrackItemName);
			textDistance = (TextView)itemView.findViewById(R.id.TrackItemDistance);
			textTime = (TextView)itemView.findViewById(R.id.TrackItemTime);
			textVisible = (TextView)itemView.findViewById(R.id.TrackItemVisible);
			textStatus = (TextView)itemView.findViewById(R.id.TrackItemStatus);
		}		
	}
	
	public View getView(Context context)
	{
		textName.setText(getName());
		textDistance.setText(getDistanceAsString(parent.getPrefs()));
		textTime.setText(getTotalTimeAsString());
		
		textVisible.setVisibility(isVisible() ? View.VISIBLE : View.GONE);
		textStatus.setText(getStatus(context));		
		
		itemView.setTag(this);
		return itemView;
	}	
		
}
