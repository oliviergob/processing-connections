package com.gobslog.processing.common.draw;

public enum Colors
{
	WHITE(255, 255, 255), BLACK(0, 0, 0), DARK_BLUE(0, 0, 102), BLUE(0, 128, 255), TRANSLUCENT_BLACK(0, 0, 0, 50),
	RED(255, 0, 0);

	private final int red;
	private final int green;
	private final int blue;
	private final int opacity;

	private Colors(int red, int green, int blue)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.opacity = 255;
	}
	
	private Colors(int red, int green, int blue, int opacity)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.opacity = opacity;
	}

	public RGBColor getRGBColor()
	{
		return new RGBColor(red, green, blue, opacity);
	}

}
