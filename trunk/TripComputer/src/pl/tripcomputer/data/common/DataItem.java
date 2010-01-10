package pl.tripcomputer.data.common;

import pl.tripcomputer.Preferences;
import pl.tripcomputer.Utils;


public abstract class DataItem
{
	//fields
	protected static Database dataBase = null;
	
	//fields
	public long iID = 0;
	
	//temp field for calculating distance 
	protected double dDistance = -1;	
	
	
	//methods
	public static void init(Database dataBase)
	{
		//required for ListItems, for displaying data in activities
		DataItem.dataBase = dataBase;
	}
	
	public void setDistance(double dDistance)
	{
		this.dDistance = dDistance;
	}

	public void clearDistance()
	{
		this.dDistance = -1;
	}
	
	public boolean isDistance()
	{
		return (dDistance != -1);
	}
	
	public String getDistanceAsString(Preferences prefs)
	{
		if (dDistance == -1)
			return "";
		else
			return Utils.getDistanceAsString(prefs, dDistance);
	}
			
}
