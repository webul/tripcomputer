package pl.tripcomputer.ui;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.Command;
import pl.tripcomputer.CommandData;
import pl.tripcomputer.Main;
import pl.tripcomputer.MainState;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.data.items.DataItemTrack;
import pl.tripcomputer.map.Screen;


public class ButtonTrackState extends UIButton implements Observer
{
	//fields
	private MainState state = null;
	private Main main = null;
	

	//methods
	public ButtonTrackState(CommonActivity parent, Screen screen)
	{
		super(parent, screen, R.drawable.btn_track_recording, Command.CMD_NONE, R.drawable.btn_track_paused);
		
		state = parent.getMainState();
		main = parent.getMain();

		//watch state object for UI mode change
		state.addObserver(this);				
	}
	
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		setSize(style.iButtonWidth, style.iButtonHeight);
		setPos(UIElement.iScreenWidth - style.iButtonWidth - style.iButtonMargin, iPosY);		
	}	
	
	public void onClick()
	{		
		super.onClick();
		
		final long lRowId = state.getOpenTrackId();		
		if (lRowId != -1)
		{			
			final DataItemTrack track = parent.getDatabase().tableTracks().getTrackItem(lRowId);

			if (!track.isClosed())
			{
				if (track.isRecording())
				{
					main.runCommand(Command.CMD_TRACK_PAUSE, new CommandData(CommandData.MODE_NONE, lRowId));				
					setStateOn();
				} else {
					main.runCommand(Command.CMD_TRACK_RESUME, new CommandData(CommandData.MODE_NONE, lRowId));				
					setStateOff();
				}
			}			
		}		
	}	
	
	public void update(Observable observable, Object data)
	{
		if (observable == state)
		{
			if (data == MainState.UI_MODE_UPDATE)
			{
				final int iTrackStatus = state.getTrackStatus();
				
				if (iTrackStatus == MainState.TRACK_NOT_SELECTED)					
				{
					hideButton();					
				}				
				if (iTrackStatus == MainState.TRACK_PAUSED)
				{
					setStateOn();				
					showButton();
				}				
				if (iTrackStatus == MainState.TRACK_RECORDING)
				{
					setStateOff();
					showButton();
				}				
			}
		}		
	}
	
}
