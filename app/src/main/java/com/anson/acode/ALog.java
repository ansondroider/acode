package com.anson.acode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.ArrayList;

import android.util.Log;
import android.view.MotionEvent;

public class ALog {

    /*** LOG.D ***********************************************/
	public static void d(String... strings){
		alog(strings);
	}
	public static void alog(String... strings){
		if(null != strings){
			int len = strings.length;
			if( len == 1){
				Log.d("ALog", "ALog >" + strings[0]);
			}
			
			if(2 == len){
				Log.d(strings[0], "ALog > " + strings[1]);
			}
			
			if(3 == len){
				Log.d(strings[0], strings[1] + strings[2]);
			}
		}
	}

    /*** LOG.W ***********************************************/
    public static void w(String... strings){
        alogW(strings);
    }
    public static void alogW(String... strings){
        if(null != strings){
            int len = strings.length;
            if( len == 1){
                Log.w("ALog", "ALog >" + strings[0]);
            }

            if(2 == len){
                Log.w(strings[0], "ALog > " + strings[1]);
            }

            if(3 == len){
                Log.w(strings[0], strings[1] + strings[2]);
            }
        }
    }

    /*** LOG.E ***********************************************/
    public static void e(String... strings){
		alogE(strings);
	}
	public static void alogE(String... strings){
		if(null != strings){
			int len = strings.length;
			if( len == 1){
				Log.e("ALog", strings[0]);
			}
			
			if(2 == len){
				Log.e(strings[0], "ALog > " + strings[1]);
			}
			
			if(3 == len){
				Log.e(strings[0], strings[1] + strings[2]);
			}
		}
	}

    /*** LOG BYTE[] ***********************************************/
    public static void logBytes(byte[] bytes){
		if(bytes != null){
			StringBuilder sb = new StringBuilder();
            sb.append("\n>>>>>>>>>>>>>>>>>>bytes:" + bytes.length + "<<<<<<<<<<<<<<<<<\n");
            for(int i = 0; i < bytes.length; i ++){
                String s = Integer.toHexString((0xff & bytes[i]));
                if(s.length() == 1)sb.append("0");
                sb.append(s).append(" ");

                if(i/16 > 0 && i % 16 == 0)sb.append("\n");
            }

            sb.append("<<<<<<<<<<<<<<<<<<<<<bytes>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            d("bytes", sb.toString());
		}else{
            d("bytes is NULL");
        }
	}
	
	public static void logBytes(String tag, byte[] bytes){
		if(bytes != null){
			for(int i=0; i<bytes.length; i++){
				alog(tag, "ALog byte[" + i + "]=" + bytes[i]);
			}
		}
	}

    /*** LOG.String[]  ***********************************************/
    public static void logArray(String TAG, String[] arr){
		if(TAG == null) TAG = "ALog";
		if(arr == null || arr.length < 1)
			alog(TAG, "array is NULL or EMPTY");
		for(int i=0; i<arr.length; i++){
			alog(TAG, "[" + i + "]" + arr[i]);
		}
	}
	
	public static void logArray(String TAG, float[] arr){
		if(TAG == null) TAG = "ALog";
		if(arr == null || arr.length < 1)
			alog(TAG, "array is NULL or EMPTY");
		for(int i=0; i<arr.length; i++){
			alog(TAG, "[" + i + "]" + arr[i]);
		}
	}
	
	public static void logArray(String TAG, String TAG2, float[] arr){
		if(TAG == null) TAG = "ALog";
		if(arr == null || arr.length < 1)
			alog(TAG, TAG2, "array is NULL or EMPTY");
		for(int i=0; i<arr.length; i++){
			alog(TAG, TAG2, "[" + i + "]" + arr[i]);
		}
	}

	public static void logListArray(ArrayList<String> arr){
		if(arr != null){
			int i = 0;
			for(String s: arr){
				alog("ArrayList " + i + ", = " + s);
				i ++;
			}
		}
	}

    /** LOG ISNULL *********************************************/
    public static void isNull(Object ... objs){
        StringBuilder sb = new StringBuilder("isNull(");
        for(Object o: objs){
            sb.append(o == null ? "true" : false).append(",");
        }
        sb.append(")");
        d(sb.toString());
    }


    public static void writeToFile(File f, byte[] bytes){
		if(f.exists()){
			f.mkdirs();
		}
		try {
			FileOutputStream fos = new FileOutputStream(f);
			
			fos.write(bytes);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * write log to file(ABSPath), and append to file tile
	 * @param fileName file full path
	 * @param s String of log
	 */
	public static void writeToFileAppend(String fileName, String s){
		FileWriter writer;
		try {
			File f = new File(fileName);
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			writer = new FileWriter(fileName, true);
			writer.write(s);
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * write log (yyyy-MM-dd hh:mm:ss--> s \n)to file(ABSPath), and append to file tile
	 * @param filePath file full path
	 * @param s String of log
	 */
	public static void writeToFileWithTimeAppend(String filePath, String s){
		FileWriter writer;
		try {
			FileUtils.checkFolderAndCreate(filePath);
			String content = TimeUtils.getTimeString() + "-->" + s + "\n";
			File f = new File(filePath);
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			writer = new FileWriter(filePath, true);
			writer.write(content);
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * return event action String
     * @param ev motion event
     * @return String
     */
	public static String getEventAction(MotionEvent ev){
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			return "ACTION_DOWN";
		}else if(ev.getAction() == MotionEvent.ACTION_MOVE){
			return "ACTION_MOVE";
		}else if(ev.getAction() == MotionEvent.ACTION_UP){
			return "ACTION_UP";
		}else if(ev.getAction() == MotionEvent.ACTION_CANCEL){
            return "ACTION_CANCEL";
        }else{
			return "ACTION_UNKNOWN";
		}
	}
}
