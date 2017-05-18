package com.anson.acode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {

	/**
	 * getJSONObject from json string,
	 * after this, you can get int, string, long, float...etc value by getXXX(String name)
	 * @param src
	 * @return
	 */
	public static JSONObject getJSON(String src){
		try {
			JSONObject obj = new JSONObject(src);
			return obj;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * getJSONArray from json string,
	 * JSONArray is compose by many JSONObject.
	 * @param name
	 * @param src
	 * @return
	 */
	public static JSONArray getJSonArray(String name, String src){
		JSONObject jo = getJSON(src);
		if(jo != null){
			try {
				return jo.getJSONArray(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 * return String array of values you want by name in array
	 * @param array
	 * @param name
	 * @return
	 */
	public static String[] getStringArray(JSONArray array, String name){
		if(array != null){
			int len = array.length();
			String result[] = new String[len];
			for(int i = 0; i < len; i ++){
				try {
					String value = array.getJSONObject(i).getString(name);
					result[i] = value == null ? "" : value;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return result;
		}
		
		return null;
	}
	
	/**
	 * get values of names from array;
	 * name: name, id, age, array: 4:
	 * return {n0,n1 n2,n3}{i0,i1,i2,i3}{a0,a1,a2,a3}
	 * @param array
	 * @param name
	 * @return
	 */
	public static String[][] getStringArray(JSONArray array, String name[]){
		int nameCount = name.length;
		int itemCount = array.length();
		String[][] result = new String[nameCount][itemCount];
		if(array != null){
			for(int i = 0; i < itemCount; i ++){
				try {
					String values[] = new String[nameCount];
					JSONObject obj = array.getJSONObject(i);
					for(int j = 0; j < nameCount; j ++){
						values[j] = obj.getString(name[j]);
					}
					result[i] = values;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result;
		}
		return result;
	}
}
