package pl.tripcomputer.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class ServiceCommand
{
	//fields
	public final static int CMD_NONE = 0;
	public final static int CMD_TRACK_RECORDING_RESUME = 1;
	public final static int CMD_TRACK_RECORDING_PAUSE = 2; 
	public final static int CMD_START_SERVICE = 3;
	
	private final static String[] vecCommands =
	{
		"CMD_NONE",
		"CMD_TRACK_RECORDING_RESUME",
		"CMD_TRACK_RECORDING_PAUSE",
		"CMD_START_SERVICE",
	};
	
	private final static String PARAM_VALUE_LONG = "PARAM_VALUE_LONG";
	private final static String PARAM_VALUE_STRING = "PARAM_VALUE_STRING";
	
	//fields
	private static Context context = null;
	private int iCommand = CMD_NONE;
	private long lValue = -1;
	private String sValue = "";
	
	
	//methods
	public static void init(Context context)
	{
		ServiceCommand.context = context;	
	}
	
	//sender
	public ServiceCommand(int iCommand, long lValue, String sValue)
	{
		this.iCommand = iCommand;
		this.lValue = lValue;
		this.sValue = sValue;
		
    Bundle data = new Bundle();
    
    data.putBoolean(getCommandString(iCommand), true);    
    
    data.putLong(PARAM_VALUE_LONG, lValue);
    data.putString(PARAM_VALUE_STRING, sValue);
    
    //update service
    Intent intent = new Intent(context, TripComputerService.class);
    intent.putExtras(data);
    
    if (iCommand == CMD_START_SERVICE)
    {
    	context.stopService(intent);
    }
    
    context.startService(intent);
	}
	
	//receiver
	public ServiceCommand(Intent intent)
	{
		iCommand = CMD_NONE;
		lValue = -1;
		sValue = "";
		
		Bundle data = intent.getExtras();
		if (data != null)
		{	
			iCommand = getCommand(data);
	
			//get params
			if (data.containsKey(PARAM_VALUE_LONG))
				lValue = data.getLong(PARAM_VALUE_LONG);
			
			if (data.containsKey(PARAM_VALUE_STRING))
				sValue = data.getString(PARAM_VALUE_STRING);
		}
	}

	public static void send(int iCommand, long lValue, String sValue)
	{
		ServiceCommand cmd = new ServiceCommand(iCommand, lValue, sValue);
		cmd.getValueLong();
	}	

	public static void send(int iCommand)
	{
		ServiceCommand cmd = new ServiceCommand(iCommand, -1, null);
		cmd.getValueLong();
	}	
	
	private int getCommand(Bundle data)
	{
		for (int iCmd = 0; iCmd < vecCommands.length; iCmd++)
		{			
			final String sCommand = getCommandString(iCmd);
			if (data.containsKey(sCommand))
				return iCmd;
		}
		return CMD_NONE;
	}
	
	private String getCommandString(int iCmdId)
	{
		return vecCommands[iCmdId];		
	}
	
	public boolean isCommand(int iCommand)
	{
		return (this.iCommand == iCommand);
	}

	public long getValueLong()
	{
		return lValue;
	}

	public String getValueString()
	{
		return sValue;
	}
	
}
