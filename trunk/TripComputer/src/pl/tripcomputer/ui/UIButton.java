package pl.tripcomputer.ui;

import pl.tripcomputer.Command;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.*;
import android.graphics.drawable.Drawable;


public abstract class UIButton extends UIElement
{
	//fields
	private Paint mBackground = new Paint();	
	protected Command cmd = null; 
	protected Drawable icon = null;
	protected Drawable iconStateOff = null;
	protected int iPosY = 0;
	
	//fields
	private boolean bStateOff = false;	
	private boolean bButtonVisible = true;
	
	
	//methods
	public UIButton(CommonActivity parent, Screen screen, int idResIcon, int iID_COMMAND)
	{
		super(parent, screen);
		cmd = Command.get(iID_COMMAND);
		icon = parent.getResources().getDrawable(idResIcon);
		mBackground.setAntiAlias(true);
	}

	public UIButton(CommonActivity parent, Screen screen, int idResIcon, int iID_COMMAND, int idResIconStateOff)
	{
		super(parent, screen);
		cmd = Command.get(iID_COMMAND);
		icon = parent.getResources().getDrawable(idResIcon);
		iconStateOff = parent.getResources().getDrawable(idResIconStateOff);
		mBackground.setAntiAlias(true);
	}
	
	public void surfaceSizeChanged(int width, int height, int screenOrientation)
	{

	}
	
	public void updateStyle()
	{
		style.fBackgroundRound = 12.0f;
	}
	
	public void doDraw(Canvas cs)
	{
		if (!bButtonVisible)
			return;
		
		super.doDraw(cs);

		this.drawSimpleShadow(cs, 8);
		
		final int iLeft = (int)rtBackground.left;
		final int iTop = (int)rtBackground.top;
		
		if (isPressedDown())
		{
			mBackground.setColor(style.iButtonDownColor);			
			cs.drawRoundRect(rtBackground, style.fBackgroundRound, style.fBackgroundRound, mBackground);
		} else {			
			if (bStateOff && iconStateOff != null)
			{
				iconStateOff.setBounds(iLeft, iTop, iLeft + icon.getIntrinsicWidth(), iTop + icon.getIntrinsicHeight());	
				iconStateOff.draw(cs);
			} else {
				icon.setBounds(iLeft, iTop, iLeft + icon.getIntrinsicWidth(), iTop + icon.getIntrinsicHeight());	
				icon.draw(cs);									
			}			
		}
	}
		
	public void onClick()
	{
		if (!bButtonVisible)
			return;
		
		if (parent != null)
		{
			if (cmd != null)
			{
				parent.getMain().runCommand(cmd, null);
			}
		}
	}
	
	public void showButton()
	{
		bButtonVisible = true;
	}

	public void hideButton()
	{
		bButtonVisible = false;
	}
	
	public void setStateOff()
	{
		bStateOff = true;
	}
	
	public void setStateOn()
	{
		bStateOff = false;
	}
	
}
