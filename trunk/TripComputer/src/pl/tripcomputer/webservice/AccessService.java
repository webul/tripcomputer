package pl.tripcomputer.webservice;

import android.content.DialogInterface;
import pl.tripcomputer.R;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.common.CommonActivity;


public class AccessService extends WebServiceAccess
{
	//fields
	private String sMsgAccessCodeSent = null;
	private String sMsgAccessCodeCorrect = null;

	
	//methods
	public AccessService(CommonActivity parent)
	{
		super(parent);
		
		sMsgAccessCodeSent = parent.getString(R.string.msg_access_code_sent);
		sMsgAccessCodeCorrect = parent.getString(R.string.msg_access_code_correct);		
	}
	
	public boolean executeGetAccessCode(String sEmailAddress)
	{
		setUserEmail(sEmailAddress);
		setAccessCode(null);
		setAccessToken(null);
		
		opGetAccessCode.execute(this);

		return false;
	}

	public boolean executeCheckAccessCode(String sEmailAddress, String sAccessCode)
	{
		setUserEmail(sEmailAddress);
		setAccessCode(sAccessCode);
		setAccessToken(null);

		opCheckAccessCode.execute(this);

		return false;
	}
	
	public void eventAccessReady()
	{
		//none to do if access token ok
	}
	
	//web operation
	private final OpCheckAccessCode opCheckAccessCode = new OpCheckAccessCode()
	{
		public void eventOperationFinish(boolean bSucceed, boolean bCodeInvalid)
		{
			if (bSucceed)
			{
				//save all data
				prefs.sEmailAddress = access.getUserEmail(); 
				prefs.sAccessCode = access.getAccessCode();
				prefs.save();

				UserAlert.show(parent, sMsgAccessCodeCorrect, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						parent.closeActivityWithResultOK();
					}					
				});				
			}
		}
	};	

	//web operation
	private final OpGetAccessCode opGetAccessCode = new OpGetAccessCode()
	{
		public void eventOperationFinish(boolean bSucceed)
		{
			if (bSucceed)
			{
				//save email address
				prefs.sEmailAddress = access.getUserEmail(); 
				prefs.sAccessCode = "";
				prefs.save();
				
				UserAlert.show(parent, sMsgAccessCodeSent, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						parent.closeActivityWithResultOK();
					}					
				});
			}
		}
	};	
	
}
