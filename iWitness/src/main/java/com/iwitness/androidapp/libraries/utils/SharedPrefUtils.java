package com.iwitness.androidapp.libraries.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.iwitness.androidapp.AppDelegate;
import com.iwitness.androidapp.controllers.authentication.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


public class SharedPrefUtils 
{
	public static final int DATA_TYPE_INT 	 = 101;
	public static final int DATA_TYPE_BOOL 	 = 102;
	public static final int DATA_TYPE_FLOAT  = 103;
	public static final int DATA_TYPE_STRING = 104;
	public static final int DATA_TYPE_LONG 	 = 105;
	
	public final static String IWITNESS_PREF = "iWitness_preferences";
	public final static String NOTIFICATION_POINTS = "points";


	public static Vector<KeyValue> getVecVales(String prefName)
	{
		SharedPreferences pref = AppDelegate.getCurrentContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		Map<String,?> keyMap 	= pref.getAll();
		Set<String> keySet 		=keyMap.keySet();
		
		Vector<KeyValue> vKeyValues = new Vector<KeyValue>();
		
		for(String key:keySet)
		{
			KeyValue value = new KeyValue();
			value.key 	= key;
			
			value.value = pref.getString(key, "");
			
			vKeyValues.add(value);
		}
		
		return vKeyValues;
	}
	public static ArrayList<String> getVectorVales(String prefName)
	{
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		ArrayList<String> vKeyValues = new ArrayList<String>();
		try {
			vKeyValues = (ArrayList<String>) ObjectSerializer.deserialize(pref.getString("Names", ObjectSerializer.serialize(new ArrayList<String>())));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vKeyValues;
	}

	public static String getKeyValue(String prefName, String key)
	{
		String value = "";
		
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		value = pref.getString(key, "");
		
		return value;
	}
	
	public static int getKeyVal(String prefName, String key)
	{
		int value = 0;
		
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		value = pref.getInt(key, 0);
		
		return value;
	}
	

	public static String getKeyValue(String prefName, String key, String defaultValue)
	{
		String value = "";
		
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		value = pref.getString(key, defaultValue);
		
		if(value.equalsIgnoreCase(""))
			value = defaultValue;
		
		return value;
	}

	public static void setValues(String prefName,Vector<KeyValue> vecKeyValue)
	{
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		for(int i=0;i<vecKeyValue.size();i++)
		{
			KeyValue value = vecKeyValue.get(i);
			switch(value.dataType)
			{
				case DATA_TYPE_INT:
								editor.putInt(value.key, value.val);	
								break;
				case DATA_TYPE_BOOL: 
								editor.putBoolean(value.key, Boolean.parseBoolean(value.value));
								break;
				case DATA_TYPE_FLOAT: 
								editor.putFloat(value.key, Float.parseFloat(value.value));
								break;
				case DATA_TYPE_STRING: 
								editor.putString(value.key,value.value);
								break;
				case DATA_TYPE_LONG: 
								editor.putLong(value.key, Long.parseLong(value.value));
								break;
				default:editor.putString(value.key,value.value);
			}
		}
		
		editor.commit();
	}

	public static void setVectorValues(String prefName,ArrayList<String> vecKeyValue)
    {
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		try {
			editor.putString("Names", ObjectSerializer.serialize(vecKeyValue));
//			editor.putStringSet("Names", (Set<String>) vecKeyValue);
		} catch (Exception e) {
			e.printStackTrace();
		}

		editor.commit();
   }
//	public static void setArrayListValues(String prefName,ArrayList<String> notifNames)
//	{
//
//		SharedPreferences pref = AppDelegate.getCurrentContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = pref.edit();
//		editor.putStringSet()
//		editor.commit();
//	}
//	public static ArrayList <String> getArrayListValues(String prefName,ArrayList <String> notifNames)
//	{
//		ArrayList <String> vals = null;
//		SharedPreferences pref = AppDelegate.getCurrentContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = pref.edit();
//		String value = null;
//		for(int i=0;i<notifNames.size();i++){
//			value = pref.getString("Names",notifNames.get(i));
//			vals.add(value);
//		}
//		editor.commit();
//		return vals;
//	}

	public static void setHashMapValues(String prefName,HashMap<String, String> notifications)
	{
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		for (String key : notifications.keySet()) {
			editor.putString(key, notifications.get(key));
		}
		editor.commit();
	}
	public static HashMap<String, String> getHashmapValues(String prefName)
	{
		HashMap<String, String> notificationsvalue = null;
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		notificationsvalue = (HashMap<String, String>) pref.getAll();
		return notificationsvalue;
	}
	public static void setValue(String prefName,KeyValue value)
	{
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		switch(value.dataType)
		{
			case DATA_TYPE_INT:
							editor.putInt(value.key, (value.val));	
							break;
			case DATA_TYPE_BOOL: 
							editor.putBoolean(value.key, Boolean.parseBoolean(value.value));
							break;
			case DATA_TYPE_FLOAT: 
							editor.putFloat(value.key, Float.parseFloat(value.value));
							break;
			case DATA_TYPE_STRING: 
							editor.putString(value.key,value.value);
							break;
			case DATA_TYPE_LONG: 
							editor.putLong(value.key, Long.parseLong(value.value));
							break;
			default:editor.putString(value.key,value.value);
		}
		
		editor.commit();
	}

	public static void clearValues(String prefName)
	{
		SharedPreferences pref =  AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	public static void clearValue(String prefName,String key)
	{
		SharedPreferences pref = AppDelegate.getAppContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(key);
		editor.commit();
	}

}
