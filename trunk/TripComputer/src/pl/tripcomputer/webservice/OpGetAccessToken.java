package pl.tripcomputer.webservice;


public abstract class OpGetAccessToken extends WebServiceOperation
{
	private String sAccessToken = null;

	protected boolean eventOperationExec()
	{
		boolean bResult = false;
		
		if (client.getAccessToken(access.getAccessCode()))
		{						
			bResult = client.statusCodeOk();
			
			if (bResult)
			{
				sAccessToken = client.getResponseData();				
			}
		}
		
		return bResult;
	}

	protected boolean eventOperationAlert(boolean bSuccees)
	{
		if (!bSuccees && codeInvalid())
		{
			access.showAlertCodeInvalid();
			return true;
		}
		if (!bSuccees && emailInvalid())
		{
			access.showAlertEmailInvalid();
			return true;
		}
		return false;
	}
	
	public String getAccessToken()
	{
		return sAccessToken;		
	}

}
