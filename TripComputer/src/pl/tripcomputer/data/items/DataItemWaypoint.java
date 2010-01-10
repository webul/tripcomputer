package pl.tripcomputer.data.items;

import pl.tripcomputer.R;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataValues;
import pl.tripcomputer.data.tables.DataTableWaypoints;
import pl.tripcomputer.data.tables.DataTableWaypoints.field;
import pl.tripcomputer.layers.WaypointIcons;
import pl.tripcomputer.map.GeoPoint;
import pl.tripcomputer.map.Mercator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;


public class DataItemWaypoint extends DataItemGeoPoint
{
	public final static int MAX_TYPE_INDEX = 17;
	
	//fields
	protected String sName = "";
	protected String sDescription = "";
	protected int iType = 0;
	protected String sUID = "";
	protected boolean bPublic = false;
	protected boolean bSent = false;

	//fields		
	private static String[] vecTypes = null;
	
	private GeoPoint geoPoint = new GeoPoint();

	
	//methods
	public DataItemWaypoint()
	{
		super();		
	}
	
	public DataItemWaypoint(DataItemWaypoint item)
	{
		super(item);
		
		this.sName = item.sName;
		this.sDescription = item.sDescription;
		this.iType = item.iType;
		this.sUID = item.sUID;
		this.bPublic = item.bPublic;		
		this.bSent = item.bSent;
		
		geoPoint.wgsLon = lon;
		geoPoint.wgsLat = lat;
	}
	
	public DataItemWaypoint(final Cursor cr)
	{
		super(cr);
		
		this.sName = cr.getString(DataTableWaypoints.field.Name);
		this.sDescription = cr.getString(DataTableWaypoints.field.Description);
		
		this.iType = cr.getInt(DataTableWaypoints.field.Type);
		if (this.iType > MAX_TYPE_INDEX)
			this.iType = 0;		
				
		this.sUID = cr.getString(DataTableWaypoints.field.UID);
		this.bPublic = (cr.getInt(DataTableWaypoints.field.Public) == 1);
		this.bSent = (cr.getInt(DataTableWaypoints.field.Sent) == 1);

		geoPoint.wgsLon = lon;
		geoPoint.wgsLat = lat;
	}

	public String getName()
	{
		return sName;
	}

	public String getDescription()
	{
		return sDescription;
	}

	public String getTypeAsString(Context context)
	{
		if (vecTypes == null)
			vecTypes = context.getResources().getStringArray(R.array.waypoint_types);

		if (iType > MAX_TYPE_INDEX)
			return vecTypes[0];

		if (iType >= 0)
			return vecTypes[iType];
				
		return "";
	}
	
	public boolean isTypeSet()
	{
		if (iType > MAX_TYPE_INDEX)
			return false;
		
		return (iType != 0);	
	}
	
	public GeoPoint getPosition()
	{
		Mercator.ConvertToFlat(geoPoint);
		return geoPoint;
	}

	public Drawable getTypeIcon()
	{				
		return WaypointIcons.get(iType);
	}

	public int getType()
	{				
		return iType;
	}
	
	public String getUID()
	{	
		return sUID;
	}
	
	public void setPublic(boolean bPublic)
	{
		this.bPublic = bPublic;
	}
	
	public boolean isPublic()
	{
		return bPublic;
	}

	public void setSent(boolean bSent)
	{
		this.bSent = bSent;
	}
	
	public boolean isSent()
	{
		return bSent;
	}
	
	public DataValues getDataValues(DataTable table)
	{						
		final DataValues values = new DataValues(table);

		values.setValue(field.TrackID, lTrackId);
		values.setValue(field.Lon, lon);
		values.setValue(field.Lat, lat);
		values.setValue(field.Altitude, iAltitude);
		values.setValue(field.TimeUTC, lTimeUTC);
		values.setValue(field.Accuracy, iAccuracy);
		values.setValue(field.Speed, iSpeed);
		values.setValue(field.Sent, bSent);
		values.setValue(field.CreationTime, lCreationTime);
	
		values.setValue(field.Name, sName);
		values.setValue(field.Description, sDescription);
		values.setValue(field.Type, iType);
		values.setValue(field.Public, bPublic);
		values.setValue(field.UID, sUID);
		
		return values;
	}
	
}
