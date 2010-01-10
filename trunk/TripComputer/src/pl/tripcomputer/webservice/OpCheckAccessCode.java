package pl.tripcomputer.webservice;


public abstract class OpCheckAccessCode extends WebServiceOperation
{
	
	protected boolean eventOperationExec()
	{
		boolean bResult = false;
		
		if (client.getAccessToken(access.getAccessCode()))
		{
			bResult = client.statusCodeOk();
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
	
	public void eventOperationFinish(boolean bSuccees)
	{
		eventOperationFinish(bSuccees, codeInvalid());
	}

	public abstract void eventOperationFinish(boolean bSuccees, boolean bCodeInvalid);
	
}
