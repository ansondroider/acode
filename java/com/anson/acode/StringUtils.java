package com.anson.acode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextPaint;

public class StringUtils {

	/**
	 * split the spical String from res, started with startStr and end with endStr.
	 * eg. res = tag abcde /tag
	 *     startStr = tag
	 *     endStr = /tag
	 *     return abcde
	 * @param res
	 * @param startStr
	 * @param endStr
	 * @return
	 */
	public static String specialString(String res, String startStr, String endStr) {
		int startIdx = res.indexOf(startStr) + startStr.length();
		// ansonlog("PageAnalyze", res);
		res = res.substring(startIdx);
		// ansonlog("PageAnalyze", "End indexof=" + res.indexOf(endStr));
		return res.substring(0, res.indexOf(endStr));
	}
	
	/**
	 * Format a Integer value to same length String.
	 * eg. getStringFromInt(1, 100) will return 001
	 * @param i value you want to change
	 * @param max the max value you want
	 * @return 001 or 01 or 0001.
	 */
	public static String getStringFromInt(int i, int max) {
		String mstr = String.valueOf(max);
		String s = String.valueOf(i);
		int ilen = s.length();
		int mlen = mstr.length();
		StringBuffer finalStr = new StringBuffer();
		for (int t = 0; t < mlen - ilen; t++) {
			finalStr.append('0');
			// if( t== mlen - ilen -1)finalStr.append(s);
		}
		finalStr.append(s);
		return finalStr.toString();
	}
	
	public static ArrayList<String> findStringsFromSpeicalString(String src, String split, String startWith, String containWith, String endWith){
		ArrayList<String> strs = new ArrayList<String>();
		String[] lines = src.split(split);
		for(int i=0; i<lines.length; i++){
			if(lines[i].contains(startWith) && lines[i].contains(containWith) && lines[i].endsWith(endWith)){
				strs.add(lines[i]);
			}
		}
		return strs;
	}
	
	/**
	 * this method return the special content from src. 
	 * eg. src = ansonlaitest string.
	 * strS = son
	 * strE = stri
	 * it's will return "laitest " 
	 * @param src source string
	 * @param strS started string
	 * @param strE end string
	 * @return string you want
	 */
	public static String getSpecialStringFromStartNEnd(String src, String strS, String strE){
		int idx = -1;
		idx = src.indexOf(strS);
		if(idx > 0){
			String src2 = src.substring(idx + strS.length());
			idx = src2.indexOf(strE);
			if(idx > 0){
				return src2.substring(0, idx);
			}else{
				return src2;
			}
		}
		
		return src;
	}
	
	/**
	 * abcde*pdf, *pdf, *txt = abcde*txt
	 * @param src
	 * @param pix
	 * @param newtile
	 * @return
	 */
	public static String changeStringTile(String src, String pix, String newtile){
		String result = src;
		int idx = src.lastIndexOf(pix);
		if(idx != -1){
			result = src.substring(0, idx) + newtile;
		}
		return result;
	}
	

	
	/**
	 * sort English chars
	 * @param strArr
	 * @param orderUp
	 */
	public static void sortStringArray(String[] strArr, boolean orderUp){
		for(int i=0; i<strArr.length - 1; i++){
			for(int j=i; j < strArr.length; j++){
				String s1 = strArr[i];
				String s2 = strArr[j];
				
				if(orderUp && s1.compareTo(s2) > 0){
					String temp = s1;
					strArr[i] = s2;
					strArr[j] = temp;
				}else if(!orderUp && s1.compareTo(s2) < 0){
					String temp = s1;
					strArr[i] = s2;
					strArr[j] = temp;
				}
			}
		}
	}
	
	/**
	 * sort chieses Chars.
	 * @param strArr
	 */
	public static void sortStringArrayInChina(String[] strArr){
		Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
		Arrays.sort(strArr, cmp);
	}
	
	/**
	 * format yyyy-MM-dd
	 * @param arr
	 */
	/**
	 * format yyyy-MM-dd
	 * @param arr
	 */
	public static void sortDateStringArray(String[] arr, boolean orderUp){
		if(arr != null && arr.length > 1){
			for(int i=0; i<arr.length; i++){
				String d1[] = arr[i].split("-");
				int di[] = {Integer.parseInt(d1[0]), Integer.parseInt(d1[1]), Integer.parseInt(d1[2])};
				
				for(int j=i+1; j<arr.length;j++){
					String d2[] = arr[j].split("-");
					int dj[] = {Integer.parseInt(d2[0]), Integer.parseInt(d2[1]), Integer.parseInt(d2[2])};
					if(!orderUp){
						if(di[0] < dj[0] ||
								(di[0] == dj[0] && di[1] < dj[1]) || 
								(di[0] == dj[0] && di[1] == dj[1] && di[2] < dj[2])){
							String tmp = arr[i];
							arr[i] = arr[j];
							arr[j] = tmp;
							d1 = arr[i].split("-");
							di = new int[]{Integer.parseInt(d1[0]), Integer.parseInt(d1[1]), Integer.parseInt(d1[2])};
						}
					}else{
						if(di[0] > dj[0] ||
								(di[0] == dj[0] && di[1] > dj[1]) || 
								(di[0] == dj[0] && di[1] == dj[1] && di[2] > dj[2])){
							String tmp = arr[i];
							arr[i] = arr[j];
							arr[j] = tmp;
							d1 = arr[i].split("-");
							di = new int[]{Integer.parseInt(d1[0]), Integer.parseInt(d1[1]), Integer.parseInt(d1[2])};
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * return the UUID;
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString(); 
	}
	
	public static SimpleDateFormat getSimpleDateFormat(String format){
		return new SimpleDateFormat(format);
	}
	public static String formatTime(Date d, SimpleDateFormat format){
		return format.format(d);
	}
	
	/**
	 * return the text width.
	 * @param str
	 * @param paint
	 * @return
	 */
	public static int getTextWidth(String str, Paint paint) {
        float totalWidth = 0f;
        float[] widths = new float[str.length()];
        paint.getTextWidths(str, widths);
        for (float f : widths) {
            totalWidth += f;
        }
        return (int) totalWidth;
    }
	
	/**
	 * return text descent. this value is useful when you draw text by canvas;
	 * @param paint
	 * @return
	 */
	public static float getTextDescent(Paint paint){
		FontMetrics metrics = paint.getFontMetrics();
		/*StringBuilder sb = new StringBuilder("FontMetrics:");
		sb.append("ascent=").append(metrics.ascent).append("\n");
		sb.append("bottom=").append(metrics.bottom).append("\n");
		sb.append("descent=").append(metrics.descent).append("\n");
		sb.append("leading=").append(metrics.leading).append("\n");
		sb.append("top=").append(metrics.top).append("\n");
		Log.d(TAG, sb.toString());*/
		return metrics.descent;
	}
	
	
	/**
	 * find spec in str, and return string started with
	 * @param str
	 * @param spec
	 * @param length
	 * @return
	 */
	public static String getStringFromSpecAndSizeAfterIndex(String str, String spec, int length){
		int idx = str.indexOf(spec);
		if(idx > 0){
			if((idx + length+1) < str.length()){
				return str.substring(0, idx + length+1);
			}else{
				return str;
			}
		}else{
			return str;
		}
	}
	
	/**
	 * read system property.
	 * @param key
	 * @param def
	 * @return
	 */
	public static String readValueFromSystem(String key, String def){
		String result = def;
		try {
			Class systemProperties = Class.forName("android.os.SystemProperties");
			Class[] params = {String.class, String.class};
			Method m = systemProperties.getMethod("get", params);
			String[] inParam = {key, def};
			result = (String) m.invoke(systemProperties, (Object[])inParam);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			return result;
		}
	}
	
	/**
	 * get MD5 String from str
	 * @param str
	 * @return
	 */
	public static String getMD5Str(String str) {       
	      MessageDigest messageDigest = null;       
	      try {       
	          messageDigest = MessageDigest.getInstance("MD5");       
	          messageDigest.reset();       
	          messageDigest.update(str.getBytes("UTF-8"));       
	      } catch (NoSuchAlgorithmException e) {       
	          //System.out.println("NoSuchAlgorithmException caught!");       
	          //System.exit(-1);       
	      } catch (UnsupportedEncodingException e) {       
	          e.printStackTrace();       
	      }       
	      byte[] byteArray = messageDigest.digest();       
	     
	      StringBuffer md5StrBuff = new StringBuffer();
	        
	      for (int i = 0; i < byteArray.length; i++) {                   
	          if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)       
	              md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));       
	          else       
	              md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));       
	      }       
	    //16位加密，从第9位到25位  
	      return md5StrBuff.substring(8, 24).toString().toUpperCase();      
	  }
	
	
	/**
	 * getLocal Mac address; return 00:00:00:00:00:00:00 by default
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {   
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);   
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        return mac == null || mac.length() == 0 ? "00:00:00:00:00:00":mac;   
    } 
	
	/**
	 * check if file is a gif file.
	 * @param data
	 * @return
	 */
	public static boolean isGif(byte[] data) {

		String id = "";
		for (int i = 0; i < 6; i++) {
			id += (char)data[i];
		}
		if (!id.toUpperCase().startsWith("GIF")) {
			return false;
		}
	
		return true;
	}
	
	/**
	 * read CPU serial no from /proc/cpuinfo
	 * @return
	 */
	public static String getCpuInfo(){
		String cpuadd = "000000000000000000";
		ByteArrayBuffer buff = null;
		byte[] cache = new byte[1024];
		try {
			FileInputStream fis = new FileInputStream(new File("/proc/cpuinfo"));
			buff = new ByteArrayBuffer(fis.available());
			int readed = 0;
			while((readed = fis.read(cache)) > 0){
				buff.append(cache, 0, readed);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			byte[] result = buff.toByteArray();
			cpuadd = new String(result);
		}
		return cpuadd;
	}
	
	/**
	 * read CPU serial no from cat /proc/cpuinfo
	 * @return
	 */
	public static String getCPUSerial() { 
        String str = "", strCPU = "", cpuAddress = "0000000000000000"; 
        try { 
                //读取CPU信息 
                Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo"); 
                InputStreamReader ir = new InputStreamReader(pp.getInputStream()); 
                LineNumberReader input = new LineNumberReader(ir); 
                //查找CPU序列号 
                for (int i = 1; i < 100; i++) { 
                        str = input.readLine(); 
                        if (str != null) { 
                                //查找到序列号所在行 
                                if (str.indexOf("Serial") > -1) { 
                                        //提取序列号 
                                        strCPU = str.substring(str.indexOf(":") + 1, 
                                                        str.length()); 
                                        //去空格 
                                        cpuAddress = strCPU.trim(); 
                                        break; 
                                } 
                        }else{ 
                                //文件结尾 
                                break; 
                        } 
                } 
        } catch (IOException ex) { 
                //赋予默认值 
                ex.printStackTrace(); 
        }
        return cpuAddress; 
	}
	
	/**
	 * return true if s1 should after s2.
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean compareString(String s1, String s2){
		if(s1 == null && s2 == null)return false;
		else if(s1 == null && s2 != null)return true;
		else if(s1 != null && s2 == null)return false;
		return s1.compareTo(s2) > 0;
	}
	
	public static void sortStringArrayWithIdx(List<String[]> arr, int idx, boolean orderUp){
		for(int i=0; i<arr.size(); i++){
			for(int j=i; j<arr.size(); j++){
				String s1 = arr.get(i)[idx];
				String s2 = arr.get(j)[idx];
				
				if(orderUp && compareString(s1, s2)){
					String[] ss1 = arr.get(i).clone();
					String[] ss2 = arr.get(j).clone();
					for(int k=0; k<ss1.length; k++){
						arr.get(i)[k] = ss2[k];
						arr.get(j)[k] = ss1[k];
					}
				}
			}
		}
	}
	
	public static String decodeUnicode(String src) {  
	    char aChar;  
	    int len = src.length();  
	    StringBuffer outBuffer = new StringBuffer(len);  
	    for (int x = 0; x < len;) {  
	        aChar = src.charAt(x++);  
	        if (aChar == '\\') {  
	            aChar = src.charAt(x++);  
	            if (aChar == 'u') {  
	                // Read the xxxx  
	                int value = 0;  
	                for (int i = 0; i < 4; i++) {  
	                    aChar = src.charAt(x++);  
	                    switch (aChar) {  
	                    case '0':  
	                    case '1':  
	                    case '2':  
	                    case '3':  
	                    case '4':  
	                    case '5':  
	                    case '6':  
	                    case '7':  
	                    case '8':  
	                    case '9':  
	                        value = (value << 4) + aChar - '0';  
	                        break;  
	                    case 'a':  
	                    case 'b':  
	                    case 'c':  
	                    case 'd':  
	                    case 'e':  
	                    case 'f':  
	                        value = (value << 4) + 10 + aChar - 'a';  
	                        break;  
	                    case 'A':  
	                    case 'B':  
	                    case 'C':  
	                    case 'D':  
	                    case 'E':  
	                    case 'F':  
	                        value = (value << 4) + 10 + aChar - 'A';  
	                        break;  
	                    default:  
	                        throw new IllegalArgumentException(  
	                                "Malformed   \\uxxxx   encoding.");  
	                    }  
	  
	                }  
	                outBuffer.append((char) value);  
	            } else {  
	                if (aChar == 't')  
	                    aChar = '\t';  
	                else if (aChar == 'r')  
	                    aChar = '\r';  
	                else if (aChar == 'n')  
	                    aChar = '\n';  
	                else if (aChar == 'f')  
	                    aChar = '\f';  
	                outBuffer.append(aChar);  
	            }  
	        } else  
	            outBuffer.append(aChar);  
	    }  
	    return outBuffer.toString();  
	}
	
	/**
	 * f = 20.65186135, format = #.##; return "20.65"
	 * @param f
	 * @param format
	 * @return
	 */
	public static String getStringOfFloat(float f, String format){
		DecimalFormat df = new DecimalFormat(format);
		return df.format(f);
	}

    /**
     * get string of byte array
     * eg. [0x0, 0x0, 0x1, 0x1,    0x1......]
     * @param bs byte array
     * @return String
     */
    public static String getByteArrayString(byte[] bs){
        StringBuilder sb = new StringBuilder();
        if(bs != null){
            sb.append("array(byte)=[");
            for(int i = 0; i < bs.length; i++){
                byte b = bs[i];
                String bStr = Integer.toHexString(b & 0xFF);
                sb.append(bStr.length() == 1 ? "0x0":"0x").append(bStr).append(",");
                if((i + 1) % 4 == 0)sb.append("    ");
            }
            sb.append("]");
        }else{
            sb.append("byte array is NULL");
        }

        return sb.toString();
    }
	/**
	 * 012-> 012
0123-> 012-3
0123456-> 012-3456
01234567-> (012)345-67
0123456789-> (012)345-6789
4-> 4
456-> 456
45678900-> (456)789-00
4123456789-> (412)345-6789
1012-> 1012
10123-> 1012-3
10123456-> 1012-345-6
101234567-> 1012-345-67
101234567890-> 101234567890
111-> 111
1112-> 1112
11123-> 1112-3
111234567-> 1112-345-67
111234567890-> 111234567890
121-> 1 21
1212-> 1 212
12123-> 1 212-3
131234567-> 1 312-345-67
131234567890-> 131234567890
	 * @param src
	 * @return
	 */
	public static String formatPhoneNumber(String src){
		String s = src;
		int length = src == null ? 0 : src.length();
		
		if(src.startsWith("1")){
			if(length > 11){
				return src;
			}
			
			if(src.startsWith("10")){
				if(length > 7){
					return "10" + src.substring(2, 4) + "-" + src.substring(4, 7) + "-" + src.substring(7);
				}else if(length > 4){
					return "10" + src.substring(2, 4) + "-" + src.substring(4);
				}else{
					return src;
				}
			}else if(src.startsWith("11")){
				if(length > 7){
					return "11" + src.substring(2, 4) + "-" + src.substring(4, 7) + "-" + src.substring(7);
				}else if(length > 4){
					return "11" + src.substring(2, 4) + "-" + src.substring(4);
				}else{
					return src;
				}
			}else {
				if(length > 7){
					return "1 " + src.substring(1, 4) + "-" + src.substring(4, 7) + "-" + src.substring(7);
				}else if(length > 4){
					return "1 " + src.substring(1, 4) + "-" + src.substring(4);
				}else if(length > 2){
					return "1 " + src.substring(1);
				}
			}

		}
		
		if(length > 10){
			return src;
		}else if(length > 7){
			s = "(" + src.substring(0, 3) + ") " + src.substring(3, 6) + "-" + src.substring(6);
		}else if(length > 3){
			s = src.substring(0, 3) + "-" + src.substring(3);
		}
		return s;
	}

}
