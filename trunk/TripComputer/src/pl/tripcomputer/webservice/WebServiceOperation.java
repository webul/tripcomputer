package pl.tripcomputer.webservice;

import org.apache.http.HttpStatus;

import android.os.Handler;
import pl.tripcomputer.http.ClientInterface;


public abstract class WebServiceOperation
{
	//fields
	protected WebServiceAccess access = null;
	protected ClientInterface client = null;

	private Handler handler = new Handler();	
	
	private boolean bSuccees = false;
	
	
	//return false to show default connection error alert	
	protected abstract boolean eventOperationAlert(boolean bSuccees);
	
	//return operation result. True if success
	protected abstract boolean eventOperationExec();
	
	public abstract void eventOperationFinish(boolean bSuccees);
	
	
  private Runnable handlerFinish = new Runnable()
  {
    public void run()
    {
			synchronized(client)
			{
	    	access.hideWait();	    		    
	    		
	    	if (!eventOperationAlert(bSuccees))
	    	{
	    		if (!bSuccees)
	    		{
	    			access.showAlertConnectionError(client.getStatusCode());
	    		}
	    	}
	    	
	  		eventOperationFinish(bSuccees);
			}
    }
  };
  
	public void execute(WebServiceAccess access)
	{
		this.access = access;

		this.client = new ClientInterface(access.getUserEmail(), access.getLanguageId());		
		
		access.showWait();
		
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				synchronized(client)
				{
					WebServiceOperation.this.bSuccees = eventOperationExec();
	
					//finish operation
					handler.removeCallbacks(handlerFinish);
					handler.post(handlerFinish);
				}
			}			
		});

		thread.start();
	}
  
	public boolean emailInvalid()
	{
		return (client.getStatusCode() == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION);
	}
	
	public boolean codeInvalid()
	{
		return (client.getStatusCode() == HttpStatus.SC_NOT_FOUND);
	}

}
