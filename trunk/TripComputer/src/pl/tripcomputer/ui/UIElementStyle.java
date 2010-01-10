package pl.tripcomputer.ui;

import android.graphics.Rect;


public class UIElementStyle
{
	public int iPosX = 0;
	public int iPosY = 0;
		
	public int iBackgroundColor = 0xff444444;
	public int iBackgroundColorDark = 0xff444444;
		
	public float fBackgroundRound = 0.0f;
	public float fBackgroundMargin = 0.0f;
	public float fHorSpace = 0.0f;
	
	public Rect rtBackgroundRoundMargin = new Rect(0,0,0,0);
	public Rect rtBackgroundMarginCorrect = new Rect(0,0,0,0);
	
	public int iTextColor1 = 0xff000000;
	public int iTextColor2 = 0xff000000;

	public float fTextSize1 = 22;
	public float fTextSize2 = 12;
	
	public int iTextColorUnderline = 0xff222222;
	public int iUnderlineColor = 0xff888888;
	
	public float fTextSizeUnderline = 12;
	public float fUnderlineTextTopMargin = 4.0f;
	
	public int iBarFrame = 0xff104050;
	public int iBarNoFixFrame = 0xffbb2222;
	public int iBarProgress = 0xff70a0b0;
	public int iFrameNoFix = 0xffcc5555;
	
	public int iButtonMargin = 8;
	public int iButtonWidth = 50;
	public int iButtonHeight = 50;
	public int iButtonTopPos = 130;

	public int iButtonUpColor = 0xffe07000;
	public int iButtonDownColor = 0xffff8800;

}
