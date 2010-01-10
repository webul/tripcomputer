package pl.tripcomputer.layers;

import java.util.ArrayList;

import pl.tripcomputer.common.CommonActivity;
import pl.tripcomputer.map.Screen;

import android.graphics.Canvas;
import android.graphics.Rect;


public class SetOfLayers extends Layer
{
  //layers
  private ArrayList<Layer> layers = new ArrayList<Layer>();
  
  private Layer grid = null;
  private Layer bearing = null;
  
  
	//methods
	public SetOfLayers(CommonActivity parent, Screen screen)
	{
		super(parent, screen);
						
  	//create list
  	layers.add(new LayerCross(parent, screen));
  	layers.add(new LayerCompass(parent, screen));
  	layers.add(new LayerLocation(parent, screen));
  	layers.add(new LayerInfo(parent, screen));
  	
  	grid = new LayerGrid(parent, screen);
  	bearing = new LayerBearing(parent, screen);
	}

	//called by MainDraw, before animation start
	public void initData()
	{
  	for (int i = 0; i < layers.size(); i++)
  		layers.get(i).initData();
	}

	public void surfaceSizeChanged(int width, int height, int iScreenOrientation)
	{
  	for (int i = 0; i < layers.size(); i++)
  		layers.get(i).surfaceSizeChanged(width, height, iScreenOrientation);
	}
	
	public void doDraw(Canvas cs, Rect rtBounds)
	{
  	for (int i = 0; i < layers.size(); i++)
  		layers.get(i).doDraw(cs, rtBounds);
	}
	
	public void doDrawGrid(Canvas cs, Rect rtBounds)
	{
		grid.doDraw(cs, rtBounds);
	}
	
	public void doDrawBearing(Canvas cs, Rect rtBounds)
	{
		bearing.doDraw(cs, rtBounds);
	}

	public void updateObjectsState()
	{
  	for (int i = 0; i < layers.size(); i++)  		
  		layers.get(i).updateObjectsState();
	}
	
}
