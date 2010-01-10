package pl.tripcomputer;

import pl.tripcomputer.R;
import android.app.ProgressDialog;
import android.content.Context;


public class WaitDialog extends ProgressDialog
{
	//fields
	private String sText = null;
	
	
	//methods	
	public WaitDialog(Context context)
	{
		super(context);					
		initialize(context);
	}
	
	private void initialize(Context context)
	{
		sText = context.getString(R.string.progress_text);				
		
	  setTitle(R.string.progress_title);
	  setProgressStyle(ProgressDialog.STYLE_SPINNER);		
		setIndeterminate(true);
	}
	
	public void show(String sDialogText)
	{
		if (sDialogText == null)
			sDialogText = this.sText;
		setMessage(sDialogText);
		show();
	}

}
