package pl.tripcomputer.layers;

import java.util.ArrayList;

import pl.tripcomputer.R;
import pl.tripcomputer.data.items.DataItemWaypoint;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;


public class WaypointIcons 
{
	//fields
	public final static int ICON_WIDTH = 16;
	public final static int ICON_HEIGHT = 16;
	
	//fields
  private static ArrayList<Drawable> icons = new ArrayList<Drawable>();
  private static Drawable iconEmpty = null;
	
  
  //methods
  public static void load(Context context)
  {
  	final Resources res = context.getResources();
  	
  	iconEmpty = res.getDrawable(R.drawable.wpt_type_other);
  	
  	//empty (not drawable) icon, at index 0
  	icons.add(iconEmpty);
  	
  	//icons for waypoint type
  	icons.add(res.getDrawable(R.drawable.wpt_type_home));
  	icons.add(res.getDrawable(R.drawable.wpt_type_camp));
  	icons.add(res.getDrawable(R.drawable.wpt_type_river));
  	icons.add(res.getDrawable(R.drawable.wpt_type_lake));
  	icons.add(res.getDrawable(R.drawable.wpt_type_forest)); //forest
  	icons.add(res.getDrawable(R.drawable.wpt_type_forest)); //tree
  	icons.add(res.getDrawable(R.drawable.wpt_type_forest)); //park
  	icons.add(res.getDrawable(R.drawable.wpt_type_crossing));
  	icons.add(res.getDrawable(R.drawable.wpt_type_bridge));
  	icons.add(res.getDrawable(R.drawable.wpt_type_road));
  	icons.add(res.getDrawable(R.drawable.wpt_type_tower));
  	icons.add(res.getDrawable(R.drawable.wpt_type_fishing));
  	icons.add(res.getDrawable(R.drawable.wpt_type_car));
  	icons.add(res.getDrawable(R.drawable.wpt_type_parking));
  	icons.add(res.getDrawable(R.drawable.wpt_type_medical));
  	icons.add(res.getDrawable(R.drawable.wpt_type_church));
  	icons.add(res.getDrawable(R.drawable.wpt_type_box));
  	
  }
  
  public static Drawable get(int index)
  {
		if (index > DataItemWaypoint.MAX_TYPE_INDEX)
			return iconEmpty;
  	
  	if (index < (icons.size()))
  		return icons.get(index);
  	  	
  	return iconEmpty;
  }

}
