package pl.tripcomputer.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.WindowManager;


public abstract class CommonThread extends Thread
{
	// fields
	protected Handler mHandler = null;
	protected Context mContext = null;
	private Display defaultDisplay = null;
	
	private volatile Boolean bIsActive = false;
	private volatile Boolean bEnabled = true;

	private static final Integer STATE_PAUSED = 0;
	private static final Integer STATE_RUNNING = 1;

	private Integer iState = STATE_PAUSED;
	
	private volatile Boolean bStateMutex = false; 
	private volatile Boolean bMutex = false; 

	
	// methods
	public CommonThread(Context context, Handler handler)
	{
		mHandler = handler;
		mContext = context;

		// get display
		final WindowManager winManager = (WindowManager)(mContext.getSystemService(Context.WINDOW_SERVICE));
		if (winManager != null)
			defaultDisplay = winManager.getDefaultDisplay();
	}

	public int getScreenOrientation()
	{
		if (defaultDisplay != null)
			return defaultDisplay.getOrientation();
		return 0;
	}

	// send message to main thread
	public boolean sendMessageToParent(Bundle bundle)
	{
		Message msg = mHandler.obtainMessage();
		msg.setData(bundle);
		return mHandler.sendMessage(msg);
	}

	public void createThread()
	{
		bIsActive = true;
		this.start();
	}

	public void destroyThread()
	{
		synchronized(bMutex)
		{
			bIsActive = false;
		}
		boolean bRetry = true;
		while (bRetry)
		{
			try
			{
				this.join();
				bRetry = false;
			} catch (InterruptedException e)
			{
			}
		}
	}

	public boolean isActive()
	{
		synchronized(bMutex)
		{
			return bIsActive;
		}
	}

	public void setEnabled(boolean bEnabled)
	{
		synchronized(bMutex)
		{
			this.bEnabled = bEnabled;
		}
	}
	
	public boolean isEnabled()
	{
		synchronized(bMutex)
		{
			return bEnabled;
		}
	}
	
	public void run()
	{
		onInitializeData();
		super.run();
	}

	public boolean isRunning()
	{
		boolean bValue = false;
		synchronized(bStateMutex)
		{
			bValue = (iState.equals(STATE_RUNNING));
		}
		return bValue;
	}
	
	public void setStateRunning()
	{
		setState(STATE_RUNNING);	
	}
	
	public void setStatePaused()
	{
		setState(STATE_PAUSED);	
	}

	private void setState(Integer state)
	{
		if (!isActive())
			return;
		
		synchronized(bStateMutex)
		{	
			iState = state;
	
			if (iState.equals(STATE_PAUSED))
			{
				// pause thread here
			}
	
			if (iState.equals(STATE_RUNNING))
			{
				// resume thread here
			}
	
			onStateChanged();
		}
	}

  //done before run
	public abstract void onInitializeData();

	//done after state changed
	public abstract void onStateChanged();

}
