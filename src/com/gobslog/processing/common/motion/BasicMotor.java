package com.gobslog.processing.common.motion;

import java.util.HashMap;
import java.util.Map;

import com.gobslog.processing.common.GobsPApplet;
import com.gobslog.processing.common.draw.Drawable;

public class BasicMotor<T extends GobsPApplet> implements Motor<T>
{

	public static final String _PROP_MOTOR_MIN_CYCLE = "common.motor.basic.nbcyclesMin";
	public static final String _PROP_MOTOR_MAX_CYCLE = "common.motor.basic.nbcyclesMax";
	public static final String _PROP_MOTOR_MIN_ROTA = "common.motor.basic.rotationMin";
	public static final String _PROP_MOTOR_MAX_ROTA = "common.motor.basic.rotationMax";

	private T parent;
	private Map<Drawable<T>, Integer> maxCycles = new HashMap<>();
	private Map<Drawable<T>, Float> rotations = new HashMap<>();

	public BasicMotor(T parent)
	{
		super();
		this.parent = parent;
	}

	public void moveObject(Drawable<T> drawable, float speedFactor)
	{
		if ( maxCycles.containsKey(drawable) && parent.frameCount % maxCycles.get(drawable) != 0)
		{
			drawable.speed.rotate(rotations.get(drawable));
			drawable.position.x +=  drawable.speed.x * speedFactor;
			drawable.position.y +=  drawable.speed.y * speedFactor;
			
			if ((drawable.position.x > parent.width) || (drawable.position.x < 0)) {
				drawable.speed.x = drawable.speed.x * -1;
			  }
			  if ((drawable.position.y > parent.height) || (drawable.position.y < 0)) {
				  drawable.speed.y = drawable.speed.y * -1;
			  }
			  
		}
		// Let's start the next cycle
		else
		{
			maxCycles.put(drawable,  parent.getIntRandom(_PROP_MOTOR_MIN_CYCLE, _PROP_MOTOR_MAX_CYCLE));
			rotations.put(drawable,  parent.getRandom(_PROP_MOTOR_MIN_ROTA, _PROP_MOTOR_MAX_ROTA));
			 
		}

	}

}
