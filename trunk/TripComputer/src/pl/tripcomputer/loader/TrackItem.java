package pl.tripcomputer.loader;

import java.util.ArrayList;

import android.database.Cursor;

import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.data.items.DataItemTrack;
import pl.tripcomputer.data.tables.DataTableGeoPoints;
import pl.tripcomputer.data.tables.DataTableGeoPoints.field;
import pl.tripcomputer.layers.LayerTrack;
import pl.tripcomputer.map.*;


public class TrackItem extends LoaderItem
{
  //drawing layer
  private LayerTrack layer = null;

  //track data
	public DataItemTrack dataTrack = null;
	public ArrayList<GeoPoint> trackPoints = new ArrayList<GeoPoint>();
	private boolean bPointsAlreadyLoaded = false;

	
	//methods
  public TrackItem(CommonActivity parent, Screen screen, Database dataBase, long lItemId)
  {
  	super(parent, screen, dataBase, lItemId);
  	
  	this.layer = new LayerTrack(parent, screen, this);
  }

  public boolean reload()
  {
  	dataTrack = dataBase.tableTracks().getTrackItem(getId());  	
  	if (dataTrack != null)
  	{ 
  		//always reload points of open track
  		if (!bPointsAlreadyLoaded)
  		{
  			loadPoints();
  			bPointsAlreadyLoaded = dataTrack.isClosed();
  		}  		
  		return true;
  	}
  	return false;
  }
  
  public LayerTrack getLayer()
  {
  	return layer;
  }
  
  public boolean isSelected()
  {
  	return (state.getSelectedTrackId() == getId());
  }
  
  public boolean isVisible()
  {
  	return dataTrack.isVisible();
  }
  
  public boolean isRecording()
  {
  	return dataTrack.isRecording();
  }
  
  public boolean isClosed()
  {
  	return dataTrack.isClosed();
  }
  
  private void addPoints(String sWhere)
  {
  	final DataTableGeoPoints table = dataBase.tableGeoPoints();

		final String sOrderBy = table.getFieldName(field.ID) + " asc";  	
  	
		final Cursor cr = table.getDataRecords(sWhere, sOrderBy);
		if (cr != null)
		{
			while (!cr.isAfterLast())
			{
				final GeoPoint geoPoint = new GeoPoint(cr);

		  	synchronized(trackPoints)
		  	{
		  		trackPoints.add(geoPoint);
		  	}

				cr.moveToNext();
			}
			cr.close();
		}
		
		//prepare data to draw
  	synchronized(trackPoints)
  	{				
			if (!trackPoints.isEmpty())
			{
				Mercator.ConvertToFlat(trackPoints);				
			}
  	}
  }  
  
  private void loadPoints()
  {
  	final DataTableGeoPoints table = dataBase.tableGeoPoints();
  	
		final String sWhere = String.format("%s = %d", table.getFieldName(field.TrackID), getId());		

  	synchronized(trackPoints)
  	{
  		trackPoints.clear();
  	}
		
  	addPoints(sWhere);
  }

  private void updatePoints(long lStartingPointId)
  {
  	final DataTableGeoPoints table = dataBase.tableGeoPoints();
  	
		final String sWhere = String.format("(%s = %d) and (%s > %d)", 
				table.getFieldName(field.TrackID), getId(),
				table.getFieldName(field.ID), lStartingPointId);
		
  	addPoints(sWhere);
  }
      
  public long getTotalTime()
  {
		long lStartTime = 0;
    long lTotalTime = 0;
  	
  	synchronized(trackPoints)
  	{
  		for (GeoPoint point : trackPoints)
  		{
  			if (lStartTime == 0)
  				lStartTime = point.lTimeUTC;

  			lTotalTime += point.lTimeUTC;
  		}
  	}
  	
  	return lTotalTime - lStartTime;
  }

  public double getTotalDistanceInMeters()
  {
    double dTotalDistance = 0;

  	synchronized(trackPoints)
  	{
  		GeoPoint lastGeoPoint = null;
  		
  		for (GeoPoint point : trackPoints)
  		{
  			if (lastGeoPoint != null)
  				dTotalDistance += point.distance(lastGeoPoint);
				
				lastGeoPoint = point;
  		}  		
  	}  	  	
  	
  	return dTotalDistance;
  }
  
  public void updateTotalDistance()
  {
  	dataTrack.setDistance(getTotalDistanceInMeters());
  }
  
  public boolean isEmpty()
  {
  	boolean bValue = false;
  	synchronized(trackPoints)
  	{
  		bValue = trackPoints.isEmpty();
  	}
  	return bValue;
  }
    
  private long getLastPointId()
  {
  	long lID = -1;
  	synchronized(trackPoints)
  	{
  		for (GeoPoint point : trackPoints)
  		{
  			if (point.iID > lID)
  				lID = point.iID;  			
  		}  		
  	}  	  	
  	return lID;
  }

  public String getDescription()
  {
  	String sText = "";
  	
  	updateTotalDistance();
  	
  	sText += dataTrack.getName();
  	sText += " - ";
  	sText += dataTrack.getDistanceAsString(prefs);
  	
  	return sText;
  }
  
  public void updatePoints()
  {  	  	
  	final long lLastPointId = getLastPointId();
  	
 		updatePoints(lLastPointId);

  	//update screen
		layer.setUpdateDrawPath();
  }
  
  public ArrayList<GeoPoint> clonePoints()
  {
  	ArrayList<GeoPoint> pointsCopy = new ArrayList<GeoPoint>();   	
  	synchronized(trackPoints)
  	{
  		pointsCopy.addAll(trackPoints);
  	}
  	return pointsCopy;
  }

  public String getPointsListString()
  {
  	String s = "";
  	
  	synchronized(trackPoints)
  	{
  		for (GeoPoint point : trackPoints)
  		{
  			s += Double.toString(point.wgsLon) + "," + Double.toString(point.wgsLat) + "," + Integer.toString(point.iAltitude);
  			s += " ";
  		}
  	}
  	
  	return s.trim();
  }

  public String getStartPoint()
  {
  	String s = "";
  	
  	synchronized(trackPoints)
  	{
  		if (trackPoints.size() > 0)
  		{
  			final GeoPoint point = trackPoints.get(0);
  			s = Double.toString(point.wgsLon) + "," + Double.toString(point.wgsLat);  			
  		}
  	}
  	
  	return s;
  }

  public String getFinishPoint()
  {
  	String s = "";
  	
  	synchronized(trackPoints)
  	{
  		final int size = trackPoints.size();
  		if (size > 0)
  		{
  			final GeoPoint point = trackPoints.get(size - 1);
  			s = Double.toString(point.wgsLon) + "," + Double.toString(point.wgsLat);  			
  		}
  	}
  	
  	return s;
  }
  
}
