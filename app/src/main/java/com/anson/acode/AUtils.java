package com.anson.acode;

import java.io.File;
import java.lang.reflect.Array;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import android.graphics.Color;

public class AUtils {
	
	public static boolean checkArray(Object[] arr){
		return (arr != null && arr.length > 0);
	}
	/**
	 * merge File array t0 and t1 to result array;
	 * @param t0
	 * @param t1
	 * @return
	 */
	public static File[] mergeFiles(File[] t0, File[] t1){
		final int size = (t0 != null ? t0.length:0) + (t1 != null ? t1.length:0);
		if(size == 0){
			return null;
		}else{
			File[] result = new File[size];
			int idx = 0;
			if(t0 != null){
				for(File f:t0){
					result[idx] = f;
					idx ++;
				}
			}
			
			if(t1 != null){
				for(File f:t1){
					result[idx] = f;
					idx ++;
				}
			}
			
			return result;
		}
	}
	
	/**
	 * this use to compare two String code in Chinese.
	 * @return
	 */
	public static Comparator getChinaComparator(){
		return Collator.getInstance(java.util.Locale.CHINA);
	}
	
	public static void shortArray(Object[] arr, Comparator comp){
		Arrays.sort(arr, 0, arr.length, comp);
	}
	
	
	/**
	 * get a color in alpha you want
	 * @param color ORIGIN color
	 * @param alpha alpha you want
	 * @return
	 */
	public static int getColorWithAlpha(int color, int alpha){
		if(color == -1){
			return Color.argb(0,0,0,0);
		}
		int a = alpha;
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		return Color.argb(a, r, g, b);
	}
}
