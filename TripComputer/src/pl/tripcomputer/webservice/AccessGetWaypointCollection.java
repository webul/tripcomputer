package pl.tripcomputer.webservice;

import java.util.ArrayList;

import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemWaypoint;


public class AccessGetWaypointCollection extends WebServiceAccess
{
	//event interface
	public static abstract class WaypointCollectionStatus
	{
		public abstract void eventCollectionReady(final ArrayList<DataItemWaypoint> items);		
	}

	private WaypointCollectionStatus status = null;
	
	private ArrayList<String> itemsWaypoints = null;
	
	
	//methods
	public AccessGetWaypointCollection(CommonActivity parent)
	{
		super(parent);
	}

	public void setStatusListener(WaypointCollectionStatus status)
	{
		this.status = status;
	}
	
	public void execute(ArrayList<String> itemsWaypoints)
	{			
		if (serviceAccessNotSet())
			return;
		
		this.itemsWaypoints = itemsWaypoints;
		requestWebOperation();
	}

	public void eventAccessReady()
	{
		opGetWaypointCollection.execute(this, itemsWaypoints);
	}

	//web operation
	private final OpGetWaypointCollection opGetWaypointCollection = new OpGetWaypointCollection()
	{
		public void eventOperationFinish(boolean bSuccees)
		{
			if (bSuccees)
			{
				final ArrayList<DataItemWaypoint> items = getItems();
				if (items != null)
				{
					if (!items.isEmpty())
					{
						if (status != null)
							status.eventCollectionReady(items);
					}
				}
			}
		}		
	};
	
}
