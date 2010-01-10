package pl.tripcomputer;

import pl.tripcomputer.activities.ActivityMain;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ServiceDataReceiver extends BroadcastReceiver
{

	//methods. Object exists only in this call
	public void onReceive(Context context, Intent intent)
	{
		try
		{			
			//post intent to process and immediate return
			synchronized(ActivityMain.loader)
			{
				if (ActivityMain.loader != null)
				{
					if (intent != null)
					{
						ActivityMain.loader.gpsLocationStatus().postIntent(intent);
					}
				}
			}
		} catch (Exception e) {			
		}
	}
	
}
