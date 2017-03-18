package com.gobslog.processing.common;

public class RadioItem
{
	
	private String itemName;
	private float itemValue;
	
	
	
	public RadioItem()
	{
		super();
	}

	public RadioItem(String itemName, float itemValue)
	{
		super();
		this.itemName = itemName;
		this.itemValue = itemValue;
	}

	public String getItemName()
	{
		return itemName;
	}

	public void setItemName(String itemName)
	{
		this.itemName = itemName;
	}

	public float getItemValue()
	{
		return itemValue;
	}

	public void setItemValue(float itemValue)
	{
		this.itemValue = itemValue;
	}
	
	
	
}
