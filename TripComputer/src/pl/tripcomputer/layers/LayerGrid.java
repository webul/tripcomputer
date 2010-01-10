package pl.tripcomputer.layers;

import pl.tripcomputer.Command;
import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.GeoPoint;
import pl.tripcomputer.map.Screen;
import pl.tripcomputer.map.ScreenPoint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class LayerGrid extends Layer
{
	//fields
	private final static int COLOR_LIGHT = 0xffeaeaea;
	private final static int COLOR_DARK = 0xffd8d8d8;
	
	private final static int MIN_CELL_SIZE = 12;
	private final static int START_CELL_SIZE_ALPHA = 20;
	private final static int ALPHA_STEPS = START_CELL_SIZE_ALPHA - MIN_CELL_SIZE;
	private final static float ALPHA_STEP_SIZE = (255 / ALPHA_STEPS);
	
  private Paint mLine = new Paint();
  
  private double dCellPixelSize = 0; 

	private int iCellLeft = 0;
	private int iCellTop = 0;
  
	private GeoPoint ptGridStart = new GeoPoint(0, 0);
	private ScreenPoint ptGridScreen = new ScreenPoint();
  
  private int width = 0;
  private int height = 0;
  
	private int iGridMargin = 0;				

  private int iStepsCount = 0;


  //methods
	public LayerGrid(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
		
  	//paint line
		mLine.setAntiAlias(false);
		mLine.setColor(COLOR_LIGHT);
		mLine.setStrokeWidth(1);
		mLine.setStyle(Paint.Style.STROKE);
	}

	public void initData()
	{
	}

	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{		
	}

	public void updateObjectsState()
	{		
	}

	public void doDraw(Canvas cs, Rect rtBounds)
	{
		//hide grid in compass mode
		if (state.getCurrentUiMode() == Command.CMD_MODE_COMPASS)
			return;
		
		width = rtBounds.width();
		height = rtBounds.height();

		//add margin for compass rotation 
		iGridMargin = (int)(Math.max(width, height) * 0.3f);				
		
	  dCellPixelSize = screen.getGridCellPixelSize();

		//calc alpha
		int iAlpha = (int)(ALPHA_STEP_SIZE * (dCellPixelSize - MIN_CELL_SIZE));
		
		if (iAlpha < 0)
			iAlpha = 0;
		if (iAlpha > 255)
			iAlpha = 255;
		
		//if grid is barely visible, hide it
		if (iAlpha > 30)
		{
			//get steps count
			iStepsCount = (int)Math.max((width / dCellPixelSize), (height / dCellPixelSize)) + 1;

			ptGridStart = screen.getGridStart();
			
			boolean bGridThickLineX = (ptGridStart.wgsLon == 1); 
			boolean bGridThickLineY = (ptGridStart.wgsLat == 1);
			
			//get screen grid starting point
			screen.toScreenPoint(ptGridStart, ptGridScreen);
			
			//draw lines
			for (int i = 0; i < iStepsCount; i++)
			{
				iCellLeft = (int)(ptGridScreen.x + ((double)i * dCellPixelSize));
				iCellTop = (int)(ptGridScreen.y + ((double)i * dCellPixelSize));

				//draw X line
				mLine.setColor(bGridThickLineX ? COLOR_DARK : COLOR_LIGHT);				
				mLine.setAlpha(iAlpha);
				
				cs.drawLine(iCellLeft, -iGridMargin, iCellLeft, height + iGridMargin, mLine);

				//draw Y line
				mLine.setColor(bGridThickLineY ? COLOR_DARK : COLOR_LIGHT);				
				mLine.setAlpha(iAlpha);
				
				cs.drawLine(-iGridMargin, iCellTop, width + iGridMargin, iCellTop, mLine);

				//invert flags
				bGridThickLineX = !bGridThickLineX; 
				bGridThickLineY = !bGridThickLineY; 
			}
		}
	}

}
