package pl.tripcomputer.common;

import pl.tripcomputer.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class CommonActivityButtons
{
	//button events interface
	public interface ButtonEvents
	{		
		public boolean onClickedDone(Bundle data);
		public boolean onClickedRevert(Bundle data);
		public void closeActivityWithResultOK(Bundle data);
		public void closeActivityWithResultCancel(Bundle data);		
	}
	
	//common data
  private CommonActivity parent = null;
  
  //default controls
  private Button btnOK = null;
  private Button btnRevert = null;
	
  
  //methods
  public CommonActivityButtons(CommonActivity parent)
  {
  	this.parent = parent;  	
  }

  public void initialize()
	{
	  btnOK = (Button)parent.findViewById(R.id.ButtonOK);
	  if (btnOK != null)
	  {
	  	btnOK.setOnClickListener(eventButtonOKClicked);	  	
	  }
	  
	  btnRevert = (Button)parent.findViewById(R.id.ButtonRevert);
	  if (btnRevert != null)
	  {
	  	btnRevert.setOnClickListener(eventButtonRevertClicked);
	  }		
	}
	
	private View.OnClickListener eventButtonOKClicked = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			Bundle data = new Bundle();
			if (parent.onClickedDone(data))
				parent.closeActivityWithResultOK(data);
		}
	};

	private View.OnClickListener eventButtonRevertClicked = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			Bundle data = new Bundle();
			if (parent.onClickedRevert(data))
				parent.closeActivityWithResultCancel(data);
		}		
	};
  
	public void setEnabledOK(boolean bEnabled)
	{
		btnOK.setEnabled(bEnabled);		
	}

	public void setEnabledRevert(boolean bEnabled)
	{
		btnRevert.setEnabled(bEnabled);		
	}
	
	public void setRevertText(String sText)
	{
		btnRevert.setText(sText);
	}
	
}
