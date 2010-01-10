package pl.tripcomputer.ui;

import java.util.Observable;
import java.util.Observer;

import pl.tripcomputer.Command;
import pl.tripcomputer.MainState;
import pl.tripcomputer.R;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;
import android.graphics.*;
import android.view.MotionEvent;


public class ModeStatus extends UIElement implements Observer
{
	//fields
	protected MainState state = null;
	
  //fields
	private String sMode = null;
	private String sModeValue = null;	
	private String[] vecModes = null;
	private int iCurrentUiMode = 0;
	
  //fields
	private Paint mTextModeValue = new Paint();
	private Paint mBarFrame = new Paint();
	private Paint mBackground = new Paint();	

	private Rect rtTextMode = new Rect(0, 0, 0, 0);
	private Rect rtTextModeValue = new Rect(0, 0, 0, 0);
	private Rect rtBarFrame = new Rect(0, 0, 0, 0);	
	
	private float iWidth = 0;
	private float iHeight = 0;
	private static int iTopMargin = 2;
	
	
  //methods
	public ModeStatus(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
				
		this.state = parent.getMainState();
		
		sMode = parent.getString(R.string.label_ui_mode);	
		vecModes = parent.getResources().getStringArray(R.array.status_ui_modes);
		
		//paint
		mTextModeValue.setAntiAlias(true);
		mTextModeValue.setColor(style.iTextColor2);
		mTextModeValue.setTextSize(style.fTextSize1);
		mTextModeValue.setTextAlign(Paint.Align.LEFT);
		mTextModeValue.setTextSkewX(fTextSkew);
				
		mBarFrame.setColor(style.iBarFrame);
		mBarFrame.setAlpha(128);
		
		mBackground.setAntiAlias(true);
		mBackground.setColor(style.iBackgroundColor);
		
		//get default height
		mTextModeValue.getTextBounds(sMode, 0, sMode.length(), rtTextMode);		
		
		this.bAlwaysVisible = true;
		
		updateWidth();		
		
		//watch state object for UI mode change
		state.addObserver(this);
	}

	public void update(Observable observable, Object data)
	{		
		if (observable == state)
		{
			if (data == MainState.UI_MODE_UPDATE)
			{
				iCurrentUiMode = (state.getCurrentUiMode() - Command.CMD_MODE_TRACKS);
				updateWidth();
			}
		}
	}	
	
	private void updateWidth()
	{
		sModeValue = vecModes[iCurrentUiMode];

		mTextModeValue.getTextBounds(sModeValue, 0, sModeValue.length(), rtTextModeValue);
					
		rtBarFrame.top = rtBounds.top + iTopMargin - 1;
		rtBarFrame.bottom = rtTextMode.height() + 2;		
		rtBarFrame.left = (int)style.fHorSpace;
		rtBarFrame.right = rtBarFrame.left + rtTextModeValue.width();
		
		iWidth = style.fHorSpace + rtTextModeValue.width() + style.fHorSpace + style.fHorSpace;		
		iHeight = rtTextMode.height() + style.fBackgroundMargin;

		setSize(iWidth, iHeight);
	}
	
	public void updateStyle()
	{
		style.iBackgroundColor = 0xff407080;
		style.iTextColor1 = 0xffffffff;
		style.iTextColor2 = 0xffffffff;
		
		style.fHorSpace = 4.0f;
		
		style.fBackgroundRound = 4.0f;
		style.fBackgroundMargin = 4.0f;
				
		style.rtBackgroundMarginCorrect.left = (int)style.fBackgroundRound;
				
		style.fTextSize1 = 14;
		style.fTextSize2 = 14;
		
		style.rtBackgroundRoundMargin.left = (int)style.fBackgroundRound;
		style.rtBackgroundRoundMargin.top = (int)style.fBackgroundRound;		
	}
	
	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
		//set position
		final int iPosX = -(int)style.fBackgroundMargin;
		final int iPosY = DigitDisplayTime.getStaticDisplayBottom() + DigitDisplayTime.iDisplayBottomMargin;				

		setPos(iPosX, iPosY);
	}
	
	public void doDraw(Canvas cs)
	{
		super.doDraw(cs);
		
		this.drawSimpleShadow(cs, 6);
		
		//draw background
		mBackground.setColor(style.iBackgroundColor);
		cs.drawRoundRect(rtBackground, style.fBackgroundRound, style.fBackgroundRound, mBackground);

		final float fTextModeTop = rtBounds.top + rtTextMode.height() + iTopMargin;
		
		//draw Mode Value
		cs.drawText(sModeValue, rtBarFrame.left, fTextModeTop, mTextModeValue);
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		//do not want touch events, onClick(), etc..
		return false;
	}
	
	public void onClick()
	{
	}
	
}
