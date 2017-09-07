package com.iwitness.androidapp.libraries.utils;

public class KeyValue {
	public String key = "";
	public String value = "";
	public int dataType = -1;
	public int val;
	
	public KeyValue() 
	{
		// default
	}

	public KeyValue(String k, String v, int dataType) 
	{
		key = k;
		value = v;
		this.dataType = dataType;
	}
	
	public KeyValue(String k, int v, int dataType) 
	{
		key = k;
		val = v;
		this.dataType = dataType;
	}
}
