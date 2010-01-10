package pl.tripcomputer.custom;

import pl.tripcomputer.R;
import pl.tripcomputer.gps.Utils;
import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


public class EditsGeoPos extends LinearLayout
{
	//layouts
	private LinearLayout LayoutDecimal = null;
	private LinearLayout LayoutSeconds = null;
	
	//decimal
	private EditText EditLocationDec_lat = null; 
	private EditText EditLocationDec_lon = null; 
	
	//seconds latitude
	private EditText EditLocation_lat_deg = null;
	private EditText EditLocation_lat_min = null;
	private EditText EditLocation_lat_sec = null;		
	private Button btnDirLat = null;

	//seconds longitude
	private EditText EditLocation_lon_deg = null;
	private EditText EditLocation_lon_min = null;
	private EditText EditLocation_lon_sec = null;		
	private Button btnDirLon = null;
	
	private boolean bDecimalMode = true;
	

	//methods
	public EditsGeoPos(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public EditsGeoPos(Context context)
	{
		super(context);
		initialize(context);
	}

	private void initialize(Context context)
	{
		inflate(context, R.layout.edits_geopos, this);
		
		LayoutDecimal = (LinearLayout)findViewById(R.id.LayoutDecimal);
		LayoutSeconds = (LinearLayout)findViewById(R.id.LayoutSeconds);
		
	  EditLocation_lat_deg = (EditText)findViewById(R.id.EditLocation_lat_deg);
	  EditLocation_lat_min = (EditText)findViewById(R.id.EditLocation_lat_min);
	  EditLocation_lat_sec = (EditText)findViewById(R.id.EditLocation_lat_sec);
	  
	  btnDirLat = (Button)findViewById(R.id.btnDirLat);        
	  btnDirLat.setOnClickListener(eventSetDirLat);
	
	  EditLocation_lon_deg = (EditText)findViewById(R.id.EditLocation_lon_deg);
	  EditLocation_lon_min = (EditText)findViewById(R.id.EditLocation_lon_min);
	  EditLocation_lon_sec = (EditText)findViewById(R.id.EditLocation_lon_sec);
	  
	  btnDirLon = (Button)findViewById(R.id.btnDirLon);
	  btnDirLon.setOnClickListener(eventSetDirLon);
	  
		EditLocationDec_lat = (EditText)findViewById(R.id.EditLocationDec_lat);
		EditLocationDec_lon = (EditText)findViewById(R.id.EditLocationDec_lon);
		
		//set focus event
		EditLocation_lat_deg.setOnFocusChangeListener(eventEditFocusChange);
		EditLocation_lat_min.setOnFocusChangeListener(eventEditFocusChange);
		EditLocation_lat_sec.setOnFocusChangeListener(eventEditFocusChange);
		
		EditLocation_lon_deg.setOnFocusChangeListener(eventEditFocusChange);
		EditLocation_lon_min.setOnFocusChangeListener(eventEditFocusChange);
		EditLocation_lon_sec.setOnFocusChangeListener(eventEditFocusChange);
		
		EditLocationDec_lat.setOnFocusChangeListener(eventEditFocusChange);
		EditLocationDec_lon.setOnFocusChangeListener(eventEditFocusChange);
		
		setDecimalMode(true);
	}
	
	private View.OnFocusChangeListener eventEditFocusChange = new View.OnFocusChangeListener()
	{
		public void onFocusChange(View v, boolean hasFocus)
		{
			if (!hasFocus)
			{
				final EditText edit = (EditText)v;
				//set "0" if no text
				if (edit.getText().toString().trim().length() == 0)
					edit.setText("0");
			}
		}
	};	
	
  private View.OnClickListener eventSetDirLat = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			btnDirLat.setText(isNorth()?"S":"N");
		}
	};

  private View.OnClickListener eventSetDirLon = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			btnDirLon.setText(isEast()?"W":"E");
		}
	};

	public boolean isNorth()
	{
		return (btnDirLat.getText().equals("N"));
	}

	public boolean isEast()
	{
		return (btnDirLon.getText().equals("E"));
	}

	private String removeMinus(String s)
	{
		try
		{
			int d = Integer.parseInt(s);			
			return Integer.toString(Math.abs(d));							
		} catch (Exception e) {
			return "0";
		}
	}

	private String getText(String[] vec, int index)
	{
		if (vec.length > index)
			return vec[index];
		else
			return "0";
	}
	
	public boolean setLatitude(double dLat)
	{
		if (bDecimalMode)
		{
			EditLocationDec_lat.setText(Double.toString(dLat));
			return true;
		} else {		
	    try
	    {
	    	final String sLat = Location.convert(dLat, Location.FORMAT_SECONDS);
	    	if ((sLat != null) && (sLat.length() > 0))
	  		{
	    		final String[] vecLat = sLat.split(":");
	    		
    			EditLocation_lat_deg.setText(removeMinus(getText(vecLat, 0)));
    			EditLocation_lat_min.setText(getText(vecLat, 1));
    			EditLocation_lat_sec.setText(getText(vecLat, 2));
	    		    		
	    		btnDirLat.setText((dLat >= 0) ? "N" : "S");
	    		
	      	return true;
	  		} 
	    } catch (Exception e) {
	    }								
	  	return false;
		}
	}

	public boolean setLongitude(double dLon)
	{
		if (bDecimalMode)
		{
			EditLocationDec_lon.setText(Double.toString(dLon));
			return true;
		} else {		
			try
	    {
	    	final String sLon = Location.convert(dLon, Location.FORMAT_SECONDS);
	    	if ((sLon != null) && (sLon.length() > 0))
	  		{
	    		final String[] vecLon = sLon.split(":");
	    		
    			EditLocation_lon_deg.setText(removeMinus(getText(vecLon, 0)));
    			EditLocation_lon_min.setText(getText(vecLon, 1));
    			EditLocation_lon_sec.setText(getText(vecLon, 2));
	    		
	    		btnDirLon.setText((dLon >= 0) ? "E" : "W");
	    		
	      	return true;
	  		} 
	    } catch (Exception e) {
	    }	
	  	return false;
		}
	}
	
	private String getEditText(EditText edit)
	{
		final String s = edit.getText().toString().trim();		
		if (s.length() == 0)
		{
			edit.setText("0");
			return "0";
		} else {
			return s;
		}
	}
	
	public Double getLatitude()
	{
		if (bDecimalMode)
		{
			try
			{
				return Double.parseDouble(getEditText(EditLocationDec_lat));
			} catch (Exception e) {
				return null;				
			}						
		} else {
			final String sign = isNorth()?"":"-";
	
	    final String sDeg = getEditText(EditLocation_lat_deg);
	    final String sMin = getEditText(EditLocation_lat_min);
	    final String sSec = getEditText(EditLocation_lat_sec);
	    
	    try
	    {
	    	return new Double(Utils.convert(sign + sDeg + ":" + sMin + ":" + sSec));
	    } catch (Exception e) {
		    Log.e("exception ", e.toString());
	    	return null; 
	    }
		}
	}

	public Double getLongitude()
	{
		if (bDecimalMode)
		{
			try
			{
				return Double.parseDouble(getEditText(EditLocationDec_lon));
			} catch (Exception e) {
				return null;
			}
		} else {		
			final String sign = isEast()?"":"-";
	
	    final String sDeg = getEditText(EditLocation_lon_deg);
	    final String sMin = getEditText(EditLocation_lon_min);
	    final String sSec = getEditText(EditLocation_lon_sec);

	    try
	    {
	    	return new Double(Utils.convert(sign + sDeg + ":" + sMin + ":" + sSec));
	    } catch (Exception e) {
	    	return null;
	    }
		}
	}
	
	public void setEnabled(boolean bEnabled)
	{
	  EditLocation_lat_deg.setEnabled(bEnabled);
	  EditLocation_lat_min.setEnabled(bEnabled);
	  EditLocation_lat_sec.setEnabled(bEnabled);
	  
	  btnDirLat.setEnabled(bEnabled);        
	
	  EditLocation_lon_deg.setEnabled(bEnabled);
	  EditLocation_lon_min.setEnabled(bEnabled);
	  EditLocation_lon_sec.setEnabled(bEnabled);
	  
	  btnDirLon.setEnabled(bEnabled);
	  
		EditLocationDec_lat.setEnabled(bEnabled);
		EditLocationDec_lon.setEnabled(bEnabled);	  				
	}
	
	public void setDecimalMode(boolean bDecimalMode)
	{
		this.bDecimalMode = bDecimalMode;
		
		LayoutDecimal.setVisibility(bDecimalMode ? View.VISIBLE : View.GONE);
		LayoutSeconds.setVisibility(bDecimalMode ? View.GONE : View.VISIBLE);
		
    setLatitude(0);
    setLongitude(0);		
	}
	
	public boolean isDecimalMode()
	{
		return bDecimalMode;		
	}
	
}
