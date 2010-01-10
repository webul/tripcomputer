package pl.tripcomputer.compass;

import java.util.Calendar;

import pl.tripcomputer.R;
import pl.tripcomputer.UserAlert;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.widget.Toast;


public class Compass
{
	//fields
	private Context context = null;
	private boolean bCalibrateLow = false;
	private boolean bCalibrateMedium = false;	
	private String sMsgCalibrate = null;

	//fields
	private SensorManager mSensorManager = null;
	private Sensor sensor = null; 
    
  //true North declination correction
	private GeomagneticField geoField = null;
	private Calendar calNow = Calendar.getInstance();
	private float fTrueNorthDeclination = 0;
	
	//fields
	private final Boolean bDataMutex = false;
	private float fAzimuth = 0;
	private float fPitch = 0;
	private float fRoll = 0;
	    
    
	//methods
	public Compass(Context context)
	{
		this.context = context;
		
		sMsgCalibrate = context.getString(R.string.alert_calibrate_compass);
		
		mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}
	
	public void updateTrueNorthCorrection(Location location)
	{
		if (geoField == null)
		{
			final float fLatDeg = (float)location.getLatitude();
			final float fLonDeg = (float)location.getLongitude();
			final float fAltitude = (float)location.getAltitude();
			
	  	geoField = new GeomagneticField(fLatDeg, fLonDeg, fAltitude, calNow.getTimeInMillis());
	  	
	  	fTrueNorthDeclination = geoField.getDeclination();
		}
	}
	
	private final SensorEventListener mSensorEvent = new SensorEventListener()
	{
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			if (sensor.getType() == Sensor.TYPE_ORIENTATION)
			{
				if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW)
				{
					if (!bCalibrateLow)
					{
						bCalibrateLow = true;
						UserAlert.show(context, sMsgCalibrate);						
					}					
				}		
				if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)
				{
					if (!bCalibrateMedium)
					{
						bCalibrateMedium = true;
						Toast.makeText(context, sMsgCalibrate, Toast.LENGTH_LONG).show();
					}
				}		
			}
		}
		public void onSensorChanged(SensorEvent event)
		{	
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
			{
				synchronized(bDataMutex)
				{					
					fAzimuth = event.values[0];
					fPitch = event.values[1];
					fRoll = event.values[2];
				}
			}
		}
	};
	
	public void start()
	{
	  if (sensor != null)
	  {
	  	mSensorManager.registerListener(mSensorEvent, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	  }
	}
	
	public void stop()
	{
    if (sensor != null)
    {
    	mSensorManager.unregisterListener(mSensorEvent);
    }
	}
	
	public float getAzimuth()
	{
		float fValue = 0;
		synchronized(bDataMutex)
		{
			//get azimuth and correct with declination
			fValue = fAzimuth + fTrueNorthDeclination;
			if (fValue > 359)
				fValue -= 360;
		}
		return fValue;
	}
	
	public float getPitch()
	{
		float fValue = 0;
		synchronized(bDataMutex)
		{
			fValue = fPitch;
		}
		return fValue;
	}
	
	public float getRoll()
	{
		float fValue = 0;
		synchronized(bDataMutex)
		{
			fValue = fRoll;
		}
		return fValue;
	}

}
