package com.gobslog.processing.common.motion;

import com.gobslog.processing.common.GobsPApplet;
import com.gobslog.processing.common.draw.Drawable;

public interface Motor<T extends GobsPApplet>
{
	public void moveObject(Drawable<T> drawable, float speedFactor);
}
