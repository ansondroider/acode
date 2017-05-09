package com.anson.acode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

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

    /**
     * check array is empty
     * @param arr source
     * @return array is NULL or size = 0
     */
    public static boolean isEmpty(Object[] arr){
        if(arr != null && arr.length > 0){
            return false;
        }
        return true;
    }

    public static boolean isEmpty(List arr){
        if(arr != null && arr.size()> 0){
            return false;
        }
        return true;
    }

    /**
     * read byte[] from input stream
     * @param is input stream
     * @return byte array
     * @throws IOException
     */
    public static byte[] readInptStream(InputStream is) throws IOException {
        final int CACHE_SIZE = 128;
        int readed;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[CACHE_SIZE];
        while((readed = is.read(buffer)) > 0){
            bos.write(buffer, 0, readed);
        }
        return bos.toByteArray();
    }

    /**
     * read 4 bytes to convert to int
     * @param bytes byte array
     * @param startIndex started index of array
     * @return int value
     */
    public static int readInt(byte[] bytes, int startIndex){
        int i = 0;
        int i0 = 0xFF & bytes[startIndex];
        int i1 = 0xFF & bytes[startIndex + 1];
        int i2 = 0xFF & bytes[startIndex + 2];
        int i3 = 0xFF & bytes[startIndex + 3];
        i = ((i0 << 24) + (i1 << 16) + (i2 << 8) + i3);
        return i;
    }

    /**
     * convert int to byte array
     * @param i int value
     * @return byte[4] array
     */
    public static byte[] getBytes(int i){
        byte[] b = new byte[4];
        b[0] = (byte) (i >>> 24);
        b[1] = (byte) (i >>> 16);
        b[2] = (byte) (i >>> 8);
        b[3] = (byte) i;
        return b;
    }

    /**
     * convert long to byte array
     * @param l long value
     * @return byte[8] array
     */
    public static byte[] getBytes(long l){
        byte[] b = new byte[8];
        b[0] = (byte) (l >>> 56);
        b[1] = (byte) (l >>> 48);
        b[2] = (byte) (l >>> 40);
        b[3] = (byte) (l >>> 32);
        b[4] = (byte) (l >>> 24);
        b[5] = (byte) (l >>> 16);
        b[6] = (byte) (l >>> 8);
        b[7] = (byte) l;
        return b;
    }
}
