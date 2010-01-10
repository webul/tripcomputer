package pl.tripcomputer.webservice;

import pl.tripcomputer.Command;
import pl.tripcomputer.CommandData;
import pl.tripcomputer.Preferences;
import pl.tripcomputer.R;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.Utils;
import pl.tripcomputer.WaitDialog;
import pl.tripcomputer.common.CommonActivity;


public abstract class WebServiceAccess
{
	//fields
	private static String sCachedEmailAddress = null;
	private static String sCachedAccessCode = null;
	
	//fields
	protected CommonActivity parent = null; 
	protected Preferences prefs = null;		
	private WaitDialog dlgWait = null;
	
	private String sUserEmail = null;
	private String sAccessCode = null;
	
	private static String sAccessToken = null;
	
	private String sMsgConnectingWithServer = null;
	
	private String sLanguageId = "en";
	
	
	//methods
	public WebServiceAccess(CommonActivity parent)
	{
		this.parent = parent;
		this.prefs = parent.getPrefs();
		
		this.dlgWait = new WaitDialog(parent);

		sMsgConnectingWithServer = parent.getString(R.string.progress_text_server_connecting);
		
		this.sLanguageId = Utils.getLanguageId(parent);
	}
	
	private void cacheAccessData()
	{
		//save email
		if (sCachedEmailAddress == null)
		{
			sCachedEmailAddress = getUserEmail();
		} else {
			//if changed, reset access token
			if (!sCachedEmailAddress.equals(getUserEmail()))
				sAccessToken = null;
		}
		//save access code
		if (sCachedAccessCode == null)
		{
			sCachedAccessCode = getAccessCode();
		} else {
			//if changed, reset access token
			if (!sCachedAccessCode.equals(getAccessCode()))
				sAccessToken = null;
		}				
	}
	
	public void setUserEmail(String sUserEmail)
	{
		this.sUserEmail = sUserEmail;
	}

	public void setAccessCode(String sAccessCode)
	{
		this.sAccessCode = sAccessCode;
	}

	public void setAccessToken(String sAccessToken)
	{
		WebServiceAccess.sAccessToken = sAccessToken;
	}

	protected String getLanguageId()
	{
		return sLanguageId;
	}
	
	public String getUserEmail()
	{
		return sUserEmail;
	}

	public String getAccessCode()
	{
		return sAccessCode;
	}

	public String getAccessToken()
	{
		return sAccessToken;
	}
	
	protected void showWait()
	{
		dlgWait.show(sMsgConnectingWithServer);
	}

	protected void hideWait()
	{
		dlgWait.hide();
	}

	protected void showAlertConnectionError(int iStatusCode)
	{
		final String sMsg = parent.getString(R.string.connection_error);
		UserAlert.show(parent, String.format(sMsg, iStatusCode));
	}
	
	protected void showAlertCodeInvalid()
	{
		final String sMsg = parent.getString(R.string.access_code_invalid);
		UserAlert.show(parent, sMsg);
	}	

	protected void showAlertEmailInvalid()
	{
		final String sMsg = parent.getString(R.string.email_invalid);
		UserAlert.show(parent, sMsg);
	}	

	protected void showAlertNoWaypointsFound()
	{
		final String sMsg = parent.getString(R.string.alert_no_waypoints_found);		
		UserAlert.show(parent, sMsg);
	}	

	protected void showAlertNoWaypointsToDownload()
	{
		final String sMsg = parent.getString(R.string.alert_no_waypoints_to_download);		
		UserAlert.show(parent, sMsg);
	}	
	
	public boolean dataValid()
	{
		prefs.load();
		
		if (prefs.sEmailAddress.length() > 0)
		{
			if (prefs.sAccessCode.length() > 0)
			{
				return true;					
			}
		}
		
		return false;
	}
	
	public boolean serviceAccessNotSet()
	{
		if (dataValid())
		{
			return false;
		} else {
			//show service access config dialog
			parent.getMain().runCommand(Command.CMD_SET_SERVICE_ACCESS, new CommandData(CommandData.MODE_NONE));
			return true;
		}		
	}

	protected void requestWebOperation()
	{
		prefs.load();
		
		setUserEmail(prefs.sEmailAddress);
		setAccessCode(prefs.sAccessCode);
		
		cacheAccessData();
		
		if (sAccessToken == null)
		{
			opGetAccessToken.execute(this);
		} else {
			eventAccessReady();
		}
	}
	
	//called after getting access token
	protected abstract void eventAccessReady();
	
	//web operation
	private final OpGetAccessToken opGetAccessToken = new OpGetAccessToken()
	{
		public void eventOperationFinish(boolean bSucceed)
		{
			if (bSucceed)
			{
				setAccessToken(getAccessToken());
				eventAccessReady();
			}
		}
	};
	
}
