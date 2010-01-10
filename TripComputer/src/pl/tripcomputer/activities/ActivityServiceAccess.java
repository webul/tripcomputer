package pl.tripcomputer.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import pl.tripcomputer.R;
import pl.tripcomputer.UserAlert;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.webservice.AccessService;


public class ActivityServiceAccess extends CommonActivity
{
	//fields
	private AccessService access = null;
	
	private EditText edEmailAddress = null;
	private EditText edAccessCode = null;	
	
	
	//methods	
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);

	  requestWindowFeature(Window.FEATURE_NO_TITLE);
	  
	  setContentView(R.layout.activity_service_access);
	  
	  edEmailAddress = (EditText)findViewById(R.id.EditEmailAddress);
	  edAccessCode = (EditText)findViewById(R.id.EditAccessCode);
	  
	  edAccessCode.setOnFocusChangeListener(eventEditAccessCodeFocus);
	  
		access = new AccessService(this);
	}

	private View.OnFocusChangeListener eventEditAccessCodeFocus = new View.OnFocusChangeListener()
	{
		public void onFocusChange(View v, boolean hasFocus)
		{
			if (hasFocus)
			{
				edAccessCode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			} else {
				edAccessCode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);				
			}
		}
	};
	
	protected void onStart()
	{
		super.onStart();
		
		prefs.load();
			  
	  edEmailAddress.setText(prefs.sEmailAddress);
	  edAccessCode.setText(prefs.sAccessCode);
	}
	
	private String getEmailAddress()
	{
		return edEmailAddress.getText().toString().trim();
	}

	private String getAccessCode()
	{
		return edAccessCode.getText().toString().trim();
	}
	
	//DONE button function: get access code
	public boolean onClickedDone(Bundle data)
	{
		final String sEmailAddress = edEmailAddress.getText().toString().trim();
		final String sAccessCode = edAccessCode.getText().toString().trim();
		
		if (sEmailAddress.length() == 0)
		{
			UserAlert.show(this, getString(R.string.alert_msg_enter_valid_email));
			return false;
		}
		
		if (sAccessCode.length() != 0)
		{
			UserAlert.show(this, getString(R.string.alert_msg_leave_access_code_empty));
			return false;			
		}
		
		access.executeGetAccessCode(getEmailAddress());
		
		return false;
	}
	
	//REVERT button function: check code and close
	public boolean onClickedRevert(Bundle data)
	{
		final String sEmailAddress = edEmailAddress.getText().toString().trim();
		final String sAccessCode = edAccessCode.getText().toString().trim();
		
		if (sEmailAddress.length() == 0)
		{
			UserAlert.show(this, getString(R.string.alert_msg_enter_valid_email));
			return false;
		}
		
		if (sAccessCode.length() == 0)
		{
			UserAlert.show(this, getString(R.string.alert_msg_enter_accesscode));
			return false;
		}
		
		access.executeCheckAccessCode(getEmailAddress(), getAccessCode());
				
		return false;
	}
	
}
