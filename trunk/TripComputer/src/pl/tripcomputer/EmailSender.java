package pl.tripcomputer;

import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.loader.TrackItem;
import android.content.Intent;
import android.net.Uri;


public class EmailSender
{
	//fields
	private CommonActivity parent = null;
	private String sChooserTitle = "";
	private String sSubjectTrack = "";
	private String sBodySummary = "";
	
	private String sSubject = "";
	private String sBody = "";
	
	
	//methods
	public EmailSender(CommonActivity parent)
	{
		this.parent = parent;
		sChooserTitle = parent.getString(R.string.send_email_select);		
		sSubjectTrack = parent.getString(R.string.send_email_subject_track);
		sBodySummary = parent.getString(R.string.send_email_body_summary);
	}

	public void setSubject(String sText)
	{
		sSubject = sText;
	}

	public void setBody(String sText)
	{
		sBody = sText;
	}
	
	public void sendFile(String sFileName)
	{
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
	
		sendIntent.setType("message/rfc822");
		
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, sSubject);
		sendIntent.putExtra(Intent.EXTRA_TEXT, sBody);
		
		Uri uriFile = Uri.parse("file://" + sFileName);
		
		sendIntent.putExtra(Intent.EXTRA_STREAM, uriFile);
		
		parent.startActivity(Intent.createChooser(sendIntent, sChooserTitle));
	}
	
	public void sendFileTrackExport(String sFileName, TrackItem itemTrack)
	{		
		setSubject(sSubjectTrack);
		
		String s = itemTrack.dataTrack.getName();
		s += "\n";
		s += "\n";
		
		final String sDescription = itemTrack.dataTrack.getDescription().trim();
		
		if (sDescription.length() > 0)
		{
			s += sDescription;
			s += "\n";
			s += "\n";
		}

		s += sBodySummary;
		
		setBody(s);
		
		sendFile(sFileName);
	}

}
