package pl.tripcomputer.ui;

import pl.tripcomputer.Command;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;


public class ButtonZoomOut extends UIButton
{

	public ButtonZoomOut(CommonActivity parent, Screen screen)
	{
		super(parent, screen, R.drawable.btn_zoom_out, Command.CMD_ZOOM_OUT);		
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
