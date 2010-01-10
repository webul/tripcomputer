package pl.tripcomputer.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Spinner;
import android.widget.TextView;
import pl.tripcomputer.CommandData;
import pl.tripcomputer.EmailSender;
import pl.tripcomputer.R;
import pl.tripcomputer.WaitDialog;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.files.FileUtils;
import pl.tripcomputer.files.xml.ExportTrackToKML;
import pl.tripcomputer.loader.TrackItem;


public class ActivityExport extends CommonActivity
{
	//fields
	private static String EXPORT_TYPE_KEY = "export_type";
	private static int EXPORT_TYPE_TRACK = 1;
	
	//fields
	private TextView labelTitle = null;
	private TextView labelObjectName = null;
	private Spinner listExportTarget = null;

	private boolean bExportTrack = false;
	private String[] vecExportTargets = null;
	
	private TrackItem itemTrack = null; 

	private Handler mHandler = new Handler();
	private WaitDialog dlgWait = null;
	
	private ExportTrackToKML xmlExportTrack = null;	
	private String sExportFileName = null;
	private boolean bExportSuccess = false;
	
	
	//methods	
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);

	  requestWindowFeature(Window.FEATURE_NO_TITLE);
	  
	  setContentView(R.layout.activity_export);

	  labelTitle = (TextView)this.findViewById(R.id.LabelTitle);
	  labelObjectName = (TextView)this.findViewById(R.id.labelObjectName);
	  listExportTarget = (Spinner)this.findViewById(R.id.listExportTarget);
		
	  vecExportTargets = mContext.getResources().getStringArray(R.array.export_targets);
	  setItemsForSpinner(listExportTarget, vecExportTargets);
	 
	  bExportTrack = (cmdStartData.getValueInt(EXPORT_TYPE_KEY) == EXPORT_TYPE_TRACK);
	}
	
	public static void setExportTypeTrack(CommandData cmdData)
	{
		cmdData.setValue(EXPORT_TYPE_KEY, EXPORT_TYPE_TRACK);
	}
		
	protected void onStart()
	{
		super.onStart();

		initialize();
	}

	public void initialize()
	{
	  //get startup data
	  if (bExportTrack)
	  {
	  	setData(dataBase.tableTracks());	  	
	  	itemTrack =	ActivityMain.loader.getTrackItem(cmdStartData.getRowId());
	  }
	  
	  //export track
	  if (itemTrack != null)
	  {
	  	labelTitle.setText(getString(R.string.title_export_track_to_kml));	  	
	  	labelObjectName.setText(itemTrack.dataTrack.getName());
	  }
	}
	
	//REVERT button function:
	public boolean onClickedRevert(Bundle data)
	{		
		return true;
	}	
	
	//DONE button function:
	public boolean onClickedDone(Bundle data)
	{
		final int iSelectedItemIndex = listExportTarget.getSelectedItemPosition();

		//send with email
		if (iSelectedItemIndex == 0)
			return dataSendWithEmail();
			
		//write to memory card
		if (iSelectedItemIndex == 1)
			return dataWriteToMemoryCard();
		
		return false;
	}
	
	private void initializeExport()
	{
		xmlExportTrack = new ExportTrackToKML(this, itemTrack);
		sExportFileName = FileUtils.getStoragExportPathKML(itemTrack.dataTrack.getName());
			
		//show wait dialog
		dlgWait = new WaitDialog(this);
		dlgWait.show(getString(R.string.exporting_data));		
	}
	
	private boolean dataWriteToMemoryCard()
	{		
		initializeExport();
		
		//run thread off UI, because of slow storage saving
    Thread t = new Thread()
    {
      public void run()
      {
      	xmlExportTrack.create();
    		bExportSuccess = xmlExportTrack.saveToFile(sExportFileName);
    		
    		//update finish to UI by handler
    		mHandler.post(mTaskFinishedWriteToStorage);
      }
    };
    t.start();
		
		return false;
	}

	private Runnable mTaskFinishedWriteToStorage = new Runnable()
	{
		public void run()
		{
			//hide wait dialog
			if (dlgWait != null)
				dlgWait.hide();
			
  		FileUtils.showSaveToast(ActivityExport.this, sExportFileName, bExportSuccess);			

  		ActivityExport.this.closeActivityWithResultOK();  		
		}
	};
	
	private boolean dataSendWithEmail()
	{	
		initializeExport();
		
		//run thread off UI, because of slow storage saving
    Thread t = new Thread()
    {
      public void run()
      {
      	xmlExportTrack.create();
    		bExportSuccess = xmlExportTrack.saveToFile(sExportFileName);
    		
    		//update finish to UI by handler
    		mHandler.post(mTaskFinishedSendEmail);    		
      }
    };
    t.start();
    
		return false;
	}	

	private Runnable mTaskFinishedSendEmail = new Runnable()
	{
		public void run()
		{
  		if (bExportSuccess)
  		{
  			EmailSender email = new EmailSender(ActivityExport.this);
  			email.sendFileTrackExport(sExportFileName, itemTrack);
  		}
  		
			//hide wait dialog
			if (dlgWait != null)
				dlgWait.hide();
			
  		if (!bExportSuccess)
  		{
  			FileUtils.showSaveToast(ActivityExport.this, sExportFileName, false);
  		}
  		
  		ActivityExport.this.closeActivityWithResultOK();  		
		}
	};
	
}
