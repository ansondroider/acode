package com.anson.acode.aos;

import com.anson.acode.ALog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;

public class ManagerTools {
	public static String TAG = "ManagerTools";
	public static final String[] SystemAppDirs = {"/system/priv-app", "/system/app", "/system/framework"};

	public static void goToSleep(Context cxt){
		PowerManager pm = (PowerManager)cxt.getSystemService(cxt.POWER_SERVICE);
		pm.goToSleep(SystemClock.uptimeMillis());
	}
	
	public static WakeLock wakeupWithWakeLock(String tag, PowerManager pm){
		WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);  
        wl.acquire();
        wl.release();
        return wl;
	}
	
	/**
	 * you can get string from other APK throw packageName.
	 * name: R.string.label_name  should "label_name"
	 * pkg: packagename, like "com.android.launcher2"
	 * @param pm
	 * @param pkg
	 * @param name
	 * @return
	 */
	public static String getStringFromPackage(PackageManager pm, String pkg, String name){
		try {
			Resources res = pm.getResourcesForApplication(pkg);
			int strid = res.getIdentifier(name, "string", pkg);
			return res.getString(strid);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * return the is a System application
	 * @param pm PackageManager
	 * @param pi PackageInfo
	 * @return
	 */
	public static boolean isSystemPackage(PackageManager pm, PackageInfo pi) {
        try {
            PackageInfo sys = pm.getPackageInfo("android", PackageManager.GET_SIGNATURES);
            String appDir = pi.applicationInfo.sourceDir;
            boolean isSystem = false;
            for(String s : SystemAppDirs){
            	if(appDir.contains(s)){
            		isSystem = true;
            		break;
            	}
            }
            
            return isSystem || (pi != null && pi.signatures != null &&
                    sys.signatures[0].equals(pi.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
	public static boolean isSystemPackageByDir(PackageInfo pi){
		String appDir = pi.applicationInfo.sourceDir;
        boolean isSystem = false;
        for(String s : SystemAppDirs){
        	if(appDir.contains(s)){
        		isSystem = true;
        		break;
        	}
        }
        return isSystem;
	}
	
	public static String RES_TYPE_STRING = "string";
	public static String RES_TYPE_DRAWABLE = "drawable";
	public static String RES_TYPE_LAYOUT = "layout";
	public static String getStringFromPackage2(PackageManager pm, String pkg, String name, String type){
		Resources res;
		try {
			res = pm.getResourcesForApplication(pkg);
			int indentify = res.getIdentifier(pkg + ":" + type + "/" + name,null,null);
			if(indentify>0){
				return res.getString(indentify);
			}else{
				return null;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}
	
	/**
	 * this method make you can reflect class From an APK file;
	 * apkPatk: /mnt/sdcard/xxx.apk
	 * outPath: /mnt/sdcard/
	 * className: com.android.launcher2.Launcher
	 * @param apkPath
	 * @param outPath
	 * @param className
	 * @return
	 */
	public static Class<?> loadFromAPKFile(String apkPath, String outPath, String className){
		ClassLoader loader = ClassLoader.getSystemClassLoader();  
        dalvik.system.DexClassLoader localDexClassLoader = new dalvik.system.DexClassLoader(apkPath, outPath, null, loader);
		try {
			Class<?> CRString = localDexClassLoader.loadClass(className);
			return CRString;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

    public static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;
    public static void setNavVisibility(boolean visible, Window w) {
        if( Build.VERSION.SDK_INT < 19 /*Build.VERSION_CODES.KITKAT*/) {
            return;
        }
        int newVis = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (!visible) {
            newVis =View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        w.getDecorView().setSystemUiVisibility(newVis);
    }

	public static String getWifiIp(Context ctx){
        String result = "0.0.0.0";
        int ip  = ((WifiManager)ctx.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress();
        if(ip > 0){
            result = ((ip) & 0xff) + "." + ((ip >> 8) & 0xff) + "." + ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);
        }
        return result;
    }
}
