package pl.tripcomputer.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import pl.tripcomputer.Main;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;


public class ActivityAbout extends CommonActivity
{
	//fields
	private TextView labAboutVersion = null;
	private String sVersion = null;
	
	
	//methods	
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);

	  requestWindowFeature(Window.FEATURE_NO_TITLE);
	  
	  setContentView(R.layout.activity_about);
	  
	  sVersion = getString(R.string.app_version);
	  
	  labAboutVersion = (TextView)findViewById(R.id.LabelAboutVersion);
	}
		
	protected void onStart()
	{
		super.onStart();
		
		prefs.load();
		
		labAboutVersion.setText(sVersion + " " + ActivityMain.getAppVersion());
	}
	
	public boolean onClickedDone(Bundle data)
	{
		final Uri uri = Uri.parse(Main.WEB_PAGE);
		startActivity(new Intent(Intent.ACTION_VIEW, uri));				
		return false;
	}
	
	public boolean onClickedRevert(Bundle data)
	{
		return true;		
	}

}
