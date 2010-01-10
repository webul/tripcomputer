package pl.tripcomputer.layers;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.Command;
import pl.tripcomputer.R;
import pl.tripcomputer.Utils;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.ui.DigitDisplayTime;

import android.graphics.Canvas;
import android.graphics.Rect;


public class LayerInfo extends Layer implements Observer
{
	//fields
	private TipFrameText tipFrameLocation = null;
	private TipFrameAltitudeGraph tipFrameAltGraph = null;
	
	private String sLocation = "";


	//methods
	public LayerInfo(CommonActivity parent, Screen screen)
	{
		super(parent, screen);				
		
		tipFrameLocation = new TipFrameText(parent.getPrefs(), state, parent.getString(R.string.info_mode_frame_location));
		tipFrameAltGraph = new TipFrameAltitudeGraph(parent.getPrefs(), state, parent.getString(R.string.info_mode_frame_altgraph));
		
		//observe location status
		super.startObserveLocation(this);
	}

	public void update(Observable observable, Object data)
	{
		if (state.getCurrentUiMode() == Command.CMD_MODE_INFO)
		{
			super.updateLocation(observable, data);
		}
	}
	
	public void initData()
	{
		
	}

	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{	
		final int iPosY = DigitDisplayTime.getStaticDisplayBottom() + DigitDisplayTime.iDisplayBottomMargin;				

		tipFrameLocation.setPosition(iPosY + 32);
		tipFrameLocation.setMargins(70, 70);
		
		tipFrameAltGraph.setPosition(height - tipFrameAltGraph.getHeight() - 70);
		tipFrameAltGraph.setMargins(70, 70);
	}

	public void updateObjectsState()
	{
	}

  public void updateLocation()
  {
  	sLocation = "";
  	
		synchronized(locationGeoPoint)
		{
			if (isLocationEnabled())
			{
				try
				{
					String sLon = Utils.locLonToString(locationGeoPoint.wgsLon);
					String sLat = Utils.locLatToString(locationGeoPoint.wgsLat);
																	
					sLocation = sLat + "\n" + sLon;
				} catch (Exception e) {						
				}
				
			}
		}	
  }
	
	public void doDraw(Canvas cs, Rect rtBounds)
	{
		if (state.getCurrentUiMode() == Command.CMD_MODE_INFO)
		{
			tipFrameLocation.setText(sLocation);
			tipFrameLocation.doDraw(cs, rtBounds);			
			tipFrameAltGraph.doDraw(cs, rtBounds);
		}		
	}
	
}
