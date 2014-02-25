package com.nag.android.volumemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
	private static PreferenceHelper instance;
	public static PreferenceHelper getInstance(Context context){
		if(instance==null){
			instance=new PreferenceHelper(context);
		}
		return instance;
	}
	private final SharedPreferences pref;

	public PreferenceHelper(Context context){
		pref=PreferenceManager.getDefaultSharedPreferences(context);
	}

	public int getInt(String key, int defValue){
		return pref.getInt(key, defValue);
	}

	public String getString(String key, String defValue){
		return pref.getString(key, defValue);
	}

	public void putInt(String key, int value){
		SharedPreferences.Editor edit=pref.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	public void putString(String key, String value){
		SharedPreferences.Editor edit=pref.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	public void remove(String key){
		SharedPreferences.Editor edit=pref.edit();
		edit.remove(key);
		edit.commit();
	}
}
