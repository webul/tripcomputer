package pl.tripcomputer.ui;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.Command;
import pl.tripcomputer.MainState;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;


public class ButtonZoomLocation extends UIButton implements Observer
{
	//fields
	private String sMsgLocationTrackingOn = null;
	private String sMsgLocationTrackingOff = null;
	

	//methods
	public ButtonZoomLocation(CommonActivity parent, Screen screen)
	{
		super(parent, screen, R.drawable.btn_zoom_location, Command.CMD_ZOOM_LOCATION, R.drawable.btn_zoom_location_off);

		sMsgLocationTrackingOn = parent.getString(R.string.location_tracking_on);
		sMsgLocationTrackingOff = parent.getString(R.string.location_tracking_off);
		
		//watch state object for UI mode change
		parent.getMainState().addObserver(this);				
	}
	
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		setSize(style.iButtonWidth, style.iButtonHeight);
		setPos(style.iButtonMargin, iPosY);
	}	
	
	public void onClick()
	{
		super.onClick();
		
		if (!parent.getMainState().isSelectedLocation())
		{
			StatusMessage.showText(sMsgLocationTrackingOff);
		}
	}	
	
	public void update(Observable observable, Object data)
	{		
		if (observable == parent.getMainState())
		{
			if (data == MainState.UI_MODE_UPDATE)
			{
				if (parent.getMainState().isSelectedLocation())
				{
					super.setStateOn();
					
					StatusMessage.showText(sMsgLocationTrackingOn);
				} else {
					super.setStateOff();
				}				
			}
		}
	}
	
}
