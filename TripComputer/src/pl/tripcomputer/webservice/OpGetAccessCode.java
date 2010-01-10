package pl.tripcomputer.webservice;


public abstract class OpGetAccessCode extends WebServiceOperation
{

	protected boolean eventOperationAlert(boolean bSuccees)
	{
		return false;
	}
	
	protected boolean eventOperationExec()
	{
		boolean bResult = false;
		
		if (client.getAccessCode())
		{
			bResult = client.statusCodeOk();
		}
		
		return bResult;
	}

}
