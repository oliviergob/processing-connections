package com.gobslog.processing.common;

import java.awt.Color;

import processing.core.PApplet;
import processing.core.PImage;

@SuppressWarnings("serial")
public class DisplayApplet extends PApplet
{

	private boolean isInitialised = false;
	private int displayWidht;
	private int displayHeight;
	public PImage displayImage = null;
	static final Color WINDOW_BGCOLOR = new Color(0xDD, 0xDD, 0xDD);
	
	public static DisplayApplet startup(int displayWidht, int displayHeight, String name, int deviceIndex) throws InterruptedException
	{
		DisplayApplet applet = new DisplayApplet();
		String[] stringArgs = { "--full-screen","--display="+deviceIndex, name};
		//String[] stringArgs = { name };
		applet.displayWidht = displayWidht;
		applet.displayHeight = displayHeight;
		PApplet.runSketch(stringArgs, applet);
		
    	
    	while (!applet.isInitialised())
    	{
    		Thread.sleep(100);
    	}
    	
    	return applet;
	}
	

	public final void setup()
	{

		synchronized (this)
		{
			if (!isInitialised)
			{
				background(0, 0, 0);

				size(displayWidht, displayHeight);

				isInitialised = true;

			}
						
		}

	}

	public final void draw()
	{
		
		background(0);
		
		if (displayImage != null)
		{
			float xpos = 0;
			if (displayImage.width < width)
			{
				xpos = (width - displayImage.width) / 2F;
			}
			image(displayImage, xpos, 0);
		}
			
		
	}

	public boolean isInitialised()
	{
		return isInitialised;
	}
	

}
