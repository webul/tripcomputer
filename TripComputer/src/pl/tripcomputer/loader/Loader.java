package pl.tripcomputer.loader;

import java.util.ArrayList;

import android.os.Handler;

import pl.tripcomputer.MainState;
import pl.tripcomputer.WaitDialog;
import pl.tripcomputer.activities.ActivityMain;
import pl.tripcomputer.data.common.Database;
import pl.tripcomputer.gps.GpsLocationStatus;
import pl.tripcomputer.map.Screen;


public abstract class Loader
{
	//fields
	protected ActivityMain parent = null;	
	protected Database dataBase = null;
	protected MainState state = null;
	
  //fields
	private Handler mHandler = new Handler();
	private WaitDialog dlgWait = null; 

	
	//methods
	public Loader(ActivityMain parent, Screen screen, GpsLocationStatus gpsLocationStatus)
	{
		this.parent = parent;
		this.dataBase = parent.getDatabase();
		this.state = parent.getMainState();
	}

	
	protected abstract void loadAllItems();
	protected abstract boolean selectObject(long lObjectId);
		
	
	public void loadData()
	{
		//show wait dialog
		dlgWait = new WaitDialog(parent);
		dlgWait.show(null);

		//run thread off UI
    Thread t = new Thread()
    {
      public void run()
      {
  			//load all items
      	loadAllItems();
  			
  			//update finish state return to UI by handler
  			mHandler.post(mTaskLoadingFinished);
      }
    };
    t.start();
	}	
	
	private Runnable mTaskLoadingFinished = new Runnable()
	{
		public void run()
		{
			//hide wait dialog
			if (dlgWait != null)
				dlgWait.hide();
		}
	};
	
	protected synchronized void selectNextObject(long lSelectedObjectId, ArrayList<Long> listObjects) 
	{		
		//iterate list, trying to select new next object
		boolean bSelectionDone = false;
		
		for (Long lObjectId : listObjects)
		{
			if (lObjectId == lSelectedObjectId)
				continue;
			if (selectObject(lObjectId))
			{
				bSelectionDone = true;
				break;
			}
		}
		
		//if no object selected, select again current object
		if (!bSelectionDone)
			if (lSelectedObjectId != -1)
				selectObject(lSelectedObjectId);
	}	
	
}
