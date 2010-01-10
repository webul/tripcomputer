package pl.tripcomputer.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;


public class WaypointListItem extends DataItemWaypoint
{
	//fields
	private static LayoutInflater inflater = null;

	private CommonActivity parent = null;
	
	private View itemView = null;
	
	private TextView textName = null;
	private TextView textDistance = null;
	private TextView textType = null;
	private TextView textItemPublic = null;
	private TextView textItemPrivate = null;
	private TextView textItemSent = null;
	
	
	//methods
	public WaypointListItem(CommonActivity parent, DataItemWaypoint item)
	{
		super(item);
		
		this.parent = parent;
		
		initialize(parent);
	}
	
	private void initialize(CommonActivity parent)
	{
		if (inflater == null)
			inflater = LayoutInflater.from(parent);
		
		if (inflater != null)
			itemView = (View)inflater.inflate(R.layout.listitem_waypoint, null);

		if (itemView != null)
		{
			textName = (TextView)itemView.findViewById(R.id.WaypointItemName);
			textDistance = (TextView)itemView.findViewById(R.id.WaypointItemDistance);
			textType = (TextView)itemView.findViewById(R.id.WaypointItemType);
			textItemPublic = (TextView)itemView.findViewById(R.id.WaypointItemPublic);
			textItemPrivate = (TextView)itemView.findViewById(R.id.WaypointItemPrivate);
			textItemSent = (TextView)itemView.findViewById(R.id.WaypointItemSent);
		}		
	}
	
	public View getView(Context context)
	{
		textName.setText(getName());
		
		if (isDistance())
		{
			textDistance.setVisibility(View.VISIBLE);
			textDistance.setText(getDistanceAsString(parent.getPrefs()));
		} else {
			textDistance.setVisibility(View.GONE);
			textDistance.setText("");			
		}
		
		textType.setText(getTypeAsString(parent));
		
		if (isPublic())
		{
			textItemPrivate.setVisibility(View.GONE);
			textItemPublic.setVisibility(View.VISIBLE);
		} else {
			textItemPublic.setVisibility(View.GONE);
			textItemPrivate.setVisibility(View.VISIBLE);			
		}
		
		if (isPublic())
		{
			textItemSent.setVisibility(View.GONE);
		} else {
			textItemSent.setVisibility(isSent() ? View.VISIBLE : View.GONE);
		}
	
		itemView.setTag(this);
		return itemView;
	}	
	
}
