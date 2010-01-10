package pl.tripcomputer.loader;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.graphics.Canvas;
import android.graphics.Rect;

import pl.tripcomputer.MainState;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.data.common.DataTable;
import pl.tripcomputer.data.common.DataTableOperation;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.ui.Ruler;


public abstract class LoaderItemCollection<T extends LoaderItem> implements Observer
{
	//fields
	protected ActivityMain parent = null;	
	protected Database dataBase = null;
	protected MainState state = null;
	
  //fields
	protected Screen screen = null;	
	protected GpsLocationStatus gpsLocationStatus = null;
	protected DataTable table = null;
  
  //fields
	protected final Boolean bListAccessMutex = false;
	protected ArrayList<T> items = new ArrayList<T>();

  
	//methods
	public LoaderItemCollection(ActivityMain parent, Screen screen, GpsLocationStatus gpsLocationStatus, DataTable table)
	{
		this.parent = parent;
		this.dataBase = parent.getDatabase();
  	this.state = parent.getMainState();  	

		this.screen = screen;
		this.gpsLocationStatus = gpsLocationStatus;
		this.table = table;
  	
		//watch for resetViewPort
		screen.addObserver(this);
		
		//watch for location change
		gpsLocationStatus.addObserver(this);
	}

	public void update(Observable observable, Object data)
	{
		//watch for screen viewport change
		if (observable == screen)
		{
			if (data != Screen.RESET_VIEWPORT_MODE_NONE)
			{
				updateForResetViewPort();
			}
			Ruler.show(data != Screen.RESET_VIEWPORT_MODE_NONE);
		}
		
		//watch for new location incoming
		if (observable == gpsLocationStatus)
		{
			if (data == GpsLocationStatus.STATE_NEW_GPS_LOCATION)
			{
				//update collection only for save point mode 
				updateForNewLocation(gpsLocationStatus.isSaveMode());
			}
		}
		
		//watch for datatable changes
		if (observable instanceof DataTable)
		{
			if (data instanceof DataTableOperation)
			{
				final DataTableOperation op = (DataTableOperation)data;
				if (op.iOperation == DataTableOperation.OP_INSERT)
				{
					loadItem(op.lRowId);
				}
				if (op.iOperation == DataTableOperation.OP_UPDATE)
				{
					loadItem(op.lRowId);
				}
				if (op.iOperation == DataTableOperation.OP_DELETE)
				{
					removeItem(op.lRowId);
				}		
			}
		}
	}

	
	//overridables
	protected abstract void updateForResetViewPort();
	protected abstract void updateForNewLocation(boolean bLocationSaved);	
	protected abstract void sortItems();
	protected abstract T createItem(ActivityMain parent, Screen screen, Database dataBase, long lItemId);
		
	
	public void loadAllItems()
	{
		synchronized(bListAccessMutex)
		{		
			items.clear();
			final ArrayList<Long> list = table.getRowsIdList(null, null);		
			if (list != null)
			{
				for (Long lItemId : list)
					loadItem(lItemId);
			}
		}
	}
	
	public ArrayList<Long> getIdList()
	{		
		ArrayList<Long> list = new ArrayList<Long>(); 
		synchronized(bListAccessMutex)
		{
			for (T loaderItem : items)
				list.add(loaderItem.getId());
		}
		return list;		
	}
	
	public T getLoaderItem(long lItemId)
	{
		T foundLoaderItem = null;
		synchronized(bListAccessMutex)
		{
			for (T loaderItem : items)
				if (loaderItem.getId() == lItemId)
				{
					foundLoaderItem = loaderItem; 
					break;
				}
		}
		return foundLoaderItem;
	}

	protected void loadItem(long lItemId)
	{
		if (lItemId != -1)
		{
			synchronized(bListAccessMutex)
			{
				final T loaderItem = getLoaderItem(lItemId);				
				if (loaderItem == null)
				{
					//load item
					final T newLoaderItem = createItem(parent, screen, dataBase, lItemId);
					if (newLoaderItem.reload())
					{
						items.add(newLoaderItem);
						sortItems();
					}
				} else {
					//update item
					loaderItem.reload();
				}
			}
		}
	}
		
	protected void removeItem(long lItemId)
	{
		synchronized(bListAccessMutex)
		{
			for (int index = 0; index < items.size(); index++)
			{
				final T loaderItem = items.get(index);
				if ((loaderItem != null) && (loaderItem.getId() == lItemId))
				{
					items.remove(index);
					sortItems();
					break;					
				}									
			}
		}
	}
	
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		synchronized(bListAccessMutex)
		{
			for (T loaderItem : items)
				loaderItem.getLayer().surfaceSizeChanged(width, height, iScreenOrientation);
		}
	}	
	
	public void doDraw(Canvas cs, Rect rtBounds, long lSelectedItemId)
	{
		synchronized(bListAccessMutex)
		{
			T loaderItemSelected = null;
			
			for (T loaderItem : items)
			{
				if (loaderItem.isVisible())
				{
					if (loaderItem.getId() == lSelectedItemId)
					{
						loaderItemSelected = loaderItem;
						continue;
					} else {					
						loaderItem.getLayer().doDraw(cs, rtBounds);
					}
				}
			}
			
			//draw selected item last, on top
			if (loaderItemSelected != null)
				loaderItemSelected.getLayer().doDraw(cs, rtBounds);
		}
	}
	
	public void updateObjectsState()
	{
		synchronized(bListAccessMutex)
		{
			for (T loaderItem : items)
				loaderItem.getLayer().updateObjectsState();
		}
	}	
			
}
