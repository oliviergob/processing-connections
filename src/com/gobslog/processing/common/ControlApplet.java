package com.gobslog.processing.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import processing.core.PApplet;

import com.gobslog.processing.common.draw.Colors;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.RadioButton;
import controlP5.Slider;
import controlP5.Textfield;
import controlP5.Toggle;

@SuppressWarnings("serial")
public class ControlApplet extends PApplet //implements ControlListener
{
	
	// HAHA

	private static int _X_INCREMENT = 30;
	private static int _Y_INCREMENT = 20;
	private static int _X_INIT = 30;

	private ControlP5 controlP5;

	private Map<String, GobsPApplet> controls = Collections.synchronizedMap(new HashMap<String, GobsPApplet>());
	private Map<String, GobsPApplet> controlsBis = Collections.synchronizedMap(new HashMap<String, GobsPApplet>());
	private Map<String, RadioButton> radioButtons = Collections.synchronizedMap(new HashMap<String, RadioButton>());
	private Map<String, String[]> radioItems = Collections.synchronizedMap(new HashMap<String, String[]>());
	
	private int xOffset = _X_INIT;
	private int yOffset = 30;
	private boolean isInitialised = false;


	public synchronized void addSlider(String varName, String label, float min, float max,
			float init, GobsPApplet caller) {
		Slider s = controlP5.addSlider(varName, min, max, init, xOffset, yOffset, 10, 10);
		s.setCaptionLabel(label);
		controls.put(varName,caller);
	}

	public synchronized void addSlider(String varName, String label, int min, int max,
			int init, GobsPApplet caller) {
		Slider s = controlP5.addSlider(varName, min, max, init, xOffset, yOffset, 150, 10);
		s.setCaptionLabel(label);
		controls.put(varName,caller);
	}
	
	public synchronized void addToggle(String varName, String label, boolean initValue, GobsPApplet caller) {
		Toggle t = controlP5.addToggle(varName);
		t.setPosition(xOffset, yOffset);
		t.setCaptionLabel(label);
		t.setValue(initValue);
		t.setSize(10, 10);
		controls.put(varName,caller);
	}
	
	public synchronized void addRadioButton(String varName, String label, GobsPApplet caller, String[] items) {
		
		controlP5.addTextlabel(label, label, xOffset, yOffset);
		addColumn();
		RadioButton rb = controlP5.addRadio(varName, xOffset, yOffset);
		rb.setItemsPerRow(4);
		rb.setSpacingColumn(35);
		
		for (int i = 0; i < items.length; i++)
		{
			rb.addItem(items[i], i);
		}
		controls.put(varName,caller);
		radioButtons.put(varName, rb);
		radioItems.put(varName, items);
		rb.activate(0);
	}
	
	public synchronized void addButton(String varName, String label, GobsPApplet caller) {
		
		Button b = controlP5.addButton(varName);
		b.setPosition(xOffset, yOffset);
		b.setSize(60, 11);
		controlsBis.put(varName,caller);
	}
	
	public synchronized void addTextField(String varName, String label, GobsPApplet caller) {
		
		Textfield tf = controlP5.addTextfield(varName);
		tf.setPosition(xOffset, yOffset);
		tf.setSize(100, 13);
		controls.put(varName,caller);
		
	}
	
	public void addColumn()
	{
		xOffset += _X_INCREMENT;
	}
	
	public void addLine()
	{
		yOffset += _Y_INCREMENT;
		xOffset = _X_INIT;
	}
	public void addHalfLine()
	{
		yOffset += (_Y_INCREMENT / 2);
		xOffset = _X_INIT;
	}


	public final void setup() {

		synchronized (this) {
			if (!isInitialised)
			{
				background(100, 100, 100);

				size(300, 400);

				controlP5 = new ControlP5(this);
			}			
		}
		
	}

	public final void draw() 
	{
		
		while (!isInitialised)
    	{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
    	}
    	
		
		Colors.TRANSLUCENT_BLACK.getRGBColor().fill(this);
		rect(0, 0, width, height);

		//smooth(8);
		

		// TODO - Refactor all this is super crap
		// It should all go in the controlEvent method
		synchronized (controls) 
		{
			for (Entry<String, GobsPApplet> entry : controls.entrySet()) {
				Field f;
				try {
					f = entry.getValue().getClass().getField(entry.getKey());
					// If it is a radio button
					if (radioItems.containsKey(entry.getKey()))
					{
						RadioButton rb = radioButtons.get(entry.getKey());
						if (rb.getValue() >= 0 )
							f.set(entry.getValue(), radioItems.get(entry.getKey())[(int) rb.getValue()] );
						else
							f.set(entry.getValue(), radioItems.get(entry.getKey())[0]);
					}
					else if (f.getType() == int.class)
						f.set(entry.getValue(), Math.round(controlP5.getValue(entry.getKey())));
					else if (f.getType() == float.class)
						f.set(entry.getValue(), controlP5.getValue(entry.getKey()));
					else if (f.getType() == String.class)
					{
						
						if ( controlP5.getController(entry.getKey()).getClass() == Textfield.class)
						{
							Textfield tf = (Textfield) controlP5.getController(entry.getKey());
							f.set(entry.getValue(), tf.getText());
						}
						else
							f.set(entry.getValue(), ""+(controlP5.getValue(entry.getKey())) );
					}
					else if (f.getType() == boolean.class)
						f.set(entry.getValue(), controlP5.getValue(entry.getKey()) == 1);
						
					
				} catch (NoSuchFieldException | SecurityException
						| IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}

			}

		}
		
		
	}

	public void setInitialised(boolean isInitialised)
	{
		this.isInitialised = isInitialised;
	}

	//@Override
	public void controlEvent(ControlEvent event)
	{
		if (radioButtons.containsKey(event.getName()))
			return;
		// If it is a button
		if ( event.getController().getClass() == Button.class)
		{
			GobsPApplet applet = controlsBis.get(event.getName());
			try
			{
				Method method = applet.getClass().getMethod(event.getName());
				method.invoke(applet);
				
			}
			catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	// For Later
	public void clear()
	{
		for (Entry<String, GobsPApplet> entry : controls.entrySet())
		{
			
		}
			
	}
	
	
	@Override
	public void keyPressed() {
		
		if (key == ' ')
		{
			GobsPApplet applet = controlsBis.get("screenShot");
			try
			{
				Method method = applet.getClass().getMethod("screenShot");
				method.invoke(applet);
				
			}
			catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		
	}

	
}
