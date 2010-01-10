package pl.tripcomputer.activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import pl.tripcomputer.Preferences;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;


public class ActivitySettings extends CommonActivity
{
	//fields
	private RadioButton rbtnMeasureSystemUSA = null;
	private RadioButton rbtnMeasureSystemEurope = null;
	
	private Spinner listLocationUpdatePeriod = null;
	private Spinner listLocationAccuracy = null;

	private String[] vecUpdatePeriod = null;
	private String[] vecAccuracy = null;

	private CheckBox chkShowGrid = null;
	private CheckBox chkLocationEditDecimal = null;
	
	private int iOldPeriodItemIndex = -1;
	private int iNewPeriodItemIndex = -1;
	
	
	//methods
	public void onCreate(Bundle savedInstanceState)
	{
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_settings);
	  
  	setSubTitle(R.string.title_settings);

  	rbtnMeasureSystemUSA = (RadioButton)this.findViewById(R.id.rbtnMeasureSystemUSA);
  	rbtnMeasureSystemEurope = (RadioButton)this.findViewById(R.id.rbtnMeasureSystemEurope);
  	
  	listLocationUpdatePeriod = (Spinner)this.findViewById(R.id.listLocationUpdatePeriod);
  	listLocationAccuracy = (Spinner)this.findViewById(R.id.listLocationAccuracy);
  	
  	vecUpdatePeriod = mContext.getResources().getStringArray(R.array.location_update_periods);
  	vecAccuracy = mContext.getResources().getStringArray(R.array.location_accuracy);
  	
	  setItemsForSpinner(listLocationUpdatePeriod, vecUpdatePeriod);
	  setItemsForSpinner(listLocationAccuracy, vecAccuracy);
	  
	  chkShowGrid = (CheckBox)this.findViewById(R.id.chkShowGrid);
	  chkLocationEditDecimal = (CheckBox)this.findViewById(R.id.chkLocationEditDecimal);
	}
	
	protected void onStart()
	{
		super.onStart();
		
		prefs.load();
				
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_USA)	
			rbtnMeasureSystemUSA.setChecked(true);
		
		if (prefs.iMeasureMode == Preferences.MEASURE_MODE_EURO)	
			rbtnMeasureSystemEurope.setChecked(true);
				
		iOldPeriodItemIndex = prefs.getUpdatePeriodItemIndex();
		if (iOldPeriodItemIndex != -1)
			listLocationUpdatePeriod.setSelection(iOldPeriodItemIndex);
		
		final int iAccuracyIndex = prefs.getLocationAccuracyIndex();
		if (iAccuracyIndex != -1)
			listLocationAccuracy.setSelection(iAccuracyIndex);
		
		chkShowGrid.setChecked(prefs.bShowGrid);
		chkLocationEditDecimal.setChecked(prefs.bLocationEditDecimal);
	}	

	public boolean onClickedDone(Bundle data)
	{
		if (rbtnMeasureSystemUSA.isChecked())
			prefs.iMeasureMode = Preferences.MEASURE_MODE_USA;
		
		if (rbtnMeasureSystemEurope.isChecked())
			prefs.iMeasureMode = Preferences.MEASURE_MODE_EURO;		

		iNewPeriodItemIndex = listLocationUpdatePeriod.getSelectedItemPosition();
		prefs.setUpdatePeriodItemIndex(iNewPeriodItemIndex);

		final int iAccuracyIndex = listLocationAccuracy.getSelectedItemPosition();
		prefs.setLocationAccuracyIndex(iAccuracyIndex);

		prefs.bShowGrid = chkShowGrid.isChecked();
		prefs.bLocationEditDecimal = chkLocationEditDecimal.isChecked();
		
		prefs.save();
		
		restartGpsIfPeriodChanged();
		
		return true;
	}
	
	public boolean onClickedRevert(Bundle data)
	{
		return true;		
	}

	private void restartGpsIfPeriodChanged()
	{
		if ((iNewPeriodItemIndex != -1) && (iOldPeriodItemIndex != -1))
		{
			if (iNewPeriodItemIndex != iOldPeriodItemIndex)
			{
				ActivityMain.restartGPS();				
			}			
		}
	}
	
}
