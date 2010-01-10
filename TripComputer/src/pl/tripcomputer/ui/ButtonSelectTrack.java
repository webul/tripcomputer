package pl.tripcomputer.ui;

import pl.tripcomputer.Command;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;


public class ButtonSelectTrack extends UIButton
{

	public ButtonSelectTrack(CommonActivity parent, Screen screen)
	{
		super(parent, screen, R.drawable.btn_select_track, Command.CMD_SELECT_TRACK);		
	}

	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		setSize(style.iButtonWidth, style.iButtonHeight);
		setPos(UIElement.iScreenWidth - style.iButtonWidth - style.iButtonMargin, iPosY);
	}
	
	public void onClick()
	{
		super.onClick();
				
	}	

}
