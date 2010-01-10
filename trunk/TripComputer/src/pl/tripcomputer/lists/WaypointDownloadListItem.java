package pl.tripcomputer.lists;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;


public class WaypointDownloadListItem extends DataItemWaypoint
{
	//fields
	private static LayoutInflater inflater = null;

	private CommonActivity parent = null;
	
	private View itemView = null;
	private boolean bMoreItemsButton = false;
	
	private CheckBox chkToDownload = null;
	private TextView textName = null;
	private TextView textDistance = null;
	private TextView textType = null;
	
	private Button btnNextPage = null;
	
	
	//methods
	public WaypointDownloadListItem(CommonActivity parent, DataItemWaypoint item, Location locCurrent)
	{
		super(item);

		this.parent = parent;
				
		if (locCurrent != null)
		{
			setDistance(distance(locCurrent.getLongitude(), locCurrent.getLatitude()));
		}		
		
		initialize(parent);
	}

	public WaypointDownloadListItem(CommonActivity parent)
	{
		super();

		this.parent = parent;
				
		bMoreItemsButton = true;
		
		initialize(parent);
	}
	
	private void initialize(CommonActivity parent)
	{
		if (inflater == null)
			inflater = LayoutInflater.from(parent);
		
		if (inflater != null)
		{
			if (bMoreItemsButton)
			{
				itemView = (View)inflater.inflate(R.layout.listitem_next_page, null);
				if (itemView != null)
				{
					btnNextPage = (Button)itemView.findViewById(R.id.ButtonNextPage);
				}
			} else {
				itemView = (View)inflater.inflate(R.layout.listitem_waypoint_download, null);
				if (itemView != null)
				{
					chkToDownload = (CheckBox)itemView.findViewById(R.id.chkToDownload);
					textName = (TextView)itemView.findViewById(R.id.WaypointItemName);
					textDistance = (TextView)itemView.findViewById(R.id.WaypointItemDistance);
					textType = (TextView)itemView.findViewById(R.id.WaypointItemType);
				}
			}
		}
	}
	
	public View getView(Context context)
	{
		if (bMoreItemsButton)
		{			
			
		} else {
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
		}
	
		itemView.setTag(this);		
		return itemView;
	}		
	
	public boolean isCheckedToDownload()
	{
		return chkToDownload.isChecked();		
	}
	
	public void setMoreItemsOnClickEvent(View.OnClickListener event)
	{
		btnNextPage.setOnClickListener(event);
	}

}
