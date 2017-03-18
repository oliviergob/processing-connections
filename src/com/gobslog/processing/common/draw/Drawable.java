package com.gobslog.processing.common.draw;

import processing.core.PVector;

import com.gobslog.processing.common.GobsPApplet;

public abstract class Drawable<T extends GobsPApplet>
{
	public static final String _PROP_DRAWABLE_MIN_SPEED = "common.drawable.speedMin";
	public static final String _PROP_DRAWABLE_MAX_SPEED = "common.drawable.speedMax";
	
	public PVector position;
	public PVector speed;
	public PVector accel;
	protected T parent;
	protected RGBColor fillColor;
	protected RGBColor strokeColor;

	public Drawable(PVector position, T parent)
	{
		super();
		this.position = position;
		this.parent = parent;
		speed = new PVector(parent.getRandom(_PROP_DRAWABLE_MIN_SPEED, _PROP_DRAWABLE_MAX_SPEED),
				parent.getRandom(_PROP_DRAWABLE_MIN_SPEED, _PROP_DRAWABLE_MAX_SPEED));
	}

	public void applyColor()
	{
		if (strokeColor != null)
			strokeColor.stroke(parent);
		else
			parent.noStroke();

		if (fillColor != null)
			fillColor.fill(parent);
		else
			parent.noFill();
	}

	public RGBColor getFillColor()
	{
		return fillColor;
	}

	public void setFillColor(RGBColor fillColor)
	{
		this.fillColor = fillColor;
	}

	public RGBColor getStrokeColor()
	{
		return strokeColor;
	}

	public void setStrokeColor(RGBColor strokeColor)
	{
		this.strokeColor = strokeColor;
	}

	public final void draw(float sizeFactor)
	{
		applyColor();
		drawMe(sizeFactor);
	}

	public abstract void drawMe(float sizeFactor);
}
