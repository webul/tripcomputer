package pl.tripcomputer.loader;

import android.location.Location;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.data.items.DataItemWaypoint;
import pl.tripcomputer.layers.LayerWaypoint;
import pl.tripcomputer.map.GeoPoint;
import pl.tripcomputer.map.Screen;


public class WaypointItem extends LoaderItem
{
  //drawing layer
  private LayerWaypoint layer = null;

  //track data
	public DataItemWaypoint dataWaypoint = null;
	
	
	//methods
  public WaypointItem(CommonActivity parent, Screen screen, Database dataBase, long lItemId)
  {
  	super(parent, screen, dataBase, lItemId);
  	
  	this.layer = new LayerWaypoint(parent, screen, this);  	
  }
	
	public boolean reload()
	{
		dataWaypoint = dataBase.tableWaypoints().getWaypointItem(getId());  	
  	if (dataWaypoint != null)
  	{
  		layer.init(dataWaypoint);
  		return true;
  	}
  	return false;
	}

  public LayerWaypoint getLayer()
  {
  	return layer;
  }
	
  public boolean isSelected()
  {
  	return (state.getSelectedWaypointId() == getId());
  }

	public boolean isVisible()
	{
		return true;
	}
	
  public String getDescription()
  {
  	String sText = "";
  	  	
  	sText += dataWaypoint.getName();
  	  	
  	return sText;
  }

  public void updateDistance(Location locCurrent)
  {
  	if (locCurrent == null)
  	{
  		dataWaypoint.clearDistance();  		
  	} else {
    	final double wgsLon = locCurrent.getLongitude();
    	final double wgsLat = locCurrent.getLatitude();

    	final double dDistance = dataWaypoint.distance(wgsLon, wgsLat);
    	
    	dataWaypoint.setDistance(dDistance);  		
  	}
  }
  
  public GeoPoint getPosition()
  {
  	return dataWaypoint.getPosition();
  }
  
}
