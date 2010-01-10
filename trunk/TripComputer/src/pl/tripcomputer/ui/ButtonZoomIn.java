package pl.tripcomputer.ui;

import pl.tripcomputer.Command;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;


public class ButtonZoomIn extends UIButton
{

	public ButtonZoomIn(CommonActivity parent, Screen screen)
	{
		super(parent, screen, R.drawable.btn_zoom_in, Command.CMD_ZOOM_IN);
	}

	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{		
		setSize(style.iButtonWidth, style.iButtonHeight);
		setPos(style.iButtonMargin, iPosY);
	}
	
	public void onClick()
	{
		super.onClick();
				
	}	
	
}
