package com.anson.acode;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtils {
	
	/**
	 * get default SharedPreferences from context
	 * @param cxt, Context
	 * @return SharedPreferences
	 */
	public static SharedPreferences getDefaultSharedPreferences(Context cxt){
		if(cxt == null)return null;
		return PreferenceManager.getDefaultSharedPreferences(cxt);
	}
	
	/**
	 * set an array to default SharedPreferences.
	 * @param cxt, Context; use to get default SharedPreference
	 * @param titles, String[]; titles, and length MUST equals values's lenght;
	 * @param values, String[]; values, and length MUST equals titles's length;
	 * @return if success.
	 */
	public static boolean setStringToDefault(Context cxt, String[] titles, String[] values){
		if(titles == null || values == null)return false;
		else{
			if(titles.length == values.length){
				for(int i=0; i<titles.length; i++){
					setString(getDefaultSharedPreferences(cxt), titles[i], values[i]);
				}
			}else{
				return false;
			}
			return true;
		}
	}
	
	/**
	 * add a new Setting to default SharedPreferences.
	 * @param cxt, Context; use to get default SharedPreference
	 * @param titles, String[]; titles, and length MUST equals values's lenght;
	 * @param values, String[]; values, and length MUST equals titles's length;
	 * @return if success.
	 */
	public static boolean setStringToDefault(Context cxt, String title, String value){
		setString(getDefaultSharedPreferences(cxt), title, value);
		return true;
	}
	
	/**
	 * put value to special SharedPreference.
	 * @param preference, SharedPreferences
	 * @param title, String
	 * @param value, String
	 */
	public static void setString(SharedPreferences preference, String title, String value){
		Editor edit = preference.edit();
		edit.putString(title, value);
		edit.commit();
	}
	
	public static boolean getBoolean(SharedPreferences preference, String title, boolean def){
		return preference.getBoolean(title, def);
	}
	public static void setBoolean(SharedPreferences preference, String title, boolean value){
		Editor edit = preference.edit();
		edit.putBoolean(title, value);
		edit.commit();
	}
	
	/**
	 * get Settings value from title
	 * @param preference
	 * @param title
	 * @param def
	 * @return
	 */
	public static String getString(SharedPreferences preference, String title, String def){
		return preference.getString(title, def);
	}
	
	/**
	 * get Settings value from title
	 * @param preference
	 * @param title
	 * @param def
	 * @return
	 */
	public static String getStringFromDefault(Context cxt, String title, String def){
		return getString(getDefaultSharedPreferences(cxt), title, def);
	}
	
	/**
	 * get Settings value from title
	 * @param preference
	 * @param title
	 * @param def
	 * @return
	 */
	public static int getIntFromDefault(Context cxt, String title, int def){
		return getInt(getDefaultSharedPreferences(cxt), title, def);
	}
	
	/**
	 * get Settings value from title
	 * @param preference
	 * @param title
	 * @param def
	 * @return
	 */
	public static int getInt(SharedPreferences preference, String title, int def){
		return preference.getInt(title, def);
	}
	/**
	 * get Settings value from title
	 * @param preference
	 * @param title
	 * @param def
	 * @return
	 */
	public static void setInt(SharedPreferences preference, String title, int value){
		Editor edit = preference.edit();
		edit.putInt(title, value);
		edit.commit();
	}
	public static void setIntToDefault(Context cxt, String title, int value){
		setInt(getDefaultSharedPreferences(cxt), title, value);
	}
	/**
	 * add a new Setting to default SharedPreferences.
	 * @param cxt, Context; use to get default SharedPreference
	 * @param titles, String[]; titles, and length MUST equals values's lenght;
	 * @param values, String[]; values, and length MUST equals titles's length;
	 * @return if success.
	 */
	public static boolean seIntToDefault(Context cxt, String title, int value){
		setInt(getDefaultSharedPreferences(cxt), title, value);
		return true;
	}
	
	/**
	 * get Settings value from title
	 * @param preference
	 * @param title
	 * @param def
	 * @return
	 */
	public static boolean getBooleanFromDefault(Context cxt, String title, boolean def){
		return getBoolean(getDefaultSharedPreferences(cxt), title, def);
	}
	
	/**
	 * get Settings value from title
	 * @param preference
	 * @param title
	 * @param def
	 * @return
	 */
	public static void setBooleanToDefault(Context cxt, String title, boolean value){
		setBoolean(getDefaultSharedPreferences(cxt), title, value);
	}
	
	public static Set<String> getStringSet(SharedPreferences preference, String key, Set<String> def){
		return preference.getStringSet(key, def);
	}
	
	public static void setStringSet(SharedPreferences preference, String key, Set<String> value){
		preference.edit().putStringSet(key, value).commit();
	}
	public static Set<String> getSetStringFromDefault(Context cxt, String key, Set<String> def){
		return getStringSet(getDefaultSharedPreferences(cxt), key, def);
	}
	
	public static void setStringSetToDefault(Context cxt, String key, Set<String> value){
		setStringSet(getDefaultSharedPreferences(cxt), key, value);
	}
}
