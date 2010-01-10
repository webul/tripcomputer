package pl.tripcomputer.loader;

import pl.tripcomputer.MainState;
import pl.tripcomputer.Preferences;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.layers.Layer;
import pl.tripcomputer.map.Screen;


public abstract class LoaderItem
{
	//fields
	protected CommonActivity parent = null;
	protected Screen screen = null;
	protected Database dataBase = null;
	protected MainState state = null;
	protected Preferences prefs = null;
	
  //common fields
	private long lItemId = -1;

	
	//methods
  public LoaderItem(CommonActivity parent, Screen screen, Database dataBase, long lItemId)
  {
  	this.parent = parent;
  	this.screen = screen;
  	this.dataBase = dataBase;
  	this.state = parent.getMainState();  	
  	this.prefs = parent.getPrefs();
  	
  	this.lItemId = lItemId;
  }
    
  public long getId()
  {
  	return lItemId;
  }  

  public abstract boolean reload();
  public abstract boolean isSelected();  
  public abstract boolean isVisible();
  public abstract Layer getLayer();
  public abstract String getDescription();

}
