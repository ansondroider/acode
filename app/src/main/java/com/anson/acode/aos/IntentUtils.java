package com.anson.acode.aos;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.anson.acode.FileUtils;

public class IntentUtils {
	
	/**
	 * open file auto.
	 * @param context
	 * @param f
	 */
	public static final void openFile(Context context, File f){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + f.getAbsolutePath()), FileUtils.getFileMineType(f));
		context.startActivity(intent);
	}
	
	/**
	 * url: eg. http://www.baidu.com
	 * @param context
	 * @param url
	 */
	public static void OpenUrl(Context context, String url){
		Intent intent=new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri CONTENT_URI_BROWSERS = Uri.parse(url);
        intent.setData(CONTENT_URI_BROWSERS);
        //intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        context.startActivity(intent);
	}
	
	/**
	 * cut image from system, maybe open Camera;
	 * @param activity
	 * @param f
	 * @param ratex
	 * @param ratey
	 * @param w
	 * @param h
	 * @param requestCode
	 */
	public static void cutImage(Activity activity, File f, int ratex, int ratey, int w, int h, int requestCode){
		Intent intent = new Intent();  
        intent.setAction("com.android.camera.action.CROP");  
        intent.setDataAndType(Uri.fromFile(f), "image/*");// mUri是已经选择的图片Uri  
        intent.putExtra("crop", "true");  
        intent.putExtra("aspectX", ratex);// 裁剪框比例  
        intent.putExtra("aspectY", ratey);  
        intent.putExtra("outputX", w);// 输出图片大小  
        intent.putExtra("outputY", h);  
        intent.putExtra("return-data", true);  
          
        activity.startActivityForResult(intent, requestCode);
	}
	
	/**
	 * get Action_Main, and Category_Launcher Intent
	 * @return
	 */
	public static final Intent getAppFilterIntent(){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		return intent;
	}
	
	/**
	 * start activity by default; but catch not found Exception
	 * @param intent
	 * @param context
	 * @return
	 */
	public static final boolean startActivitySafe(Intent intent, Context context){
		if(intent != null){
			try{
				context.startActivity(intent);
				return true;
			}catch(Exception e){
			}
		}
		return false;
	}
	
	/**
	 * start activity by default Activity in package
	 * @param packageName
	 * @param context
	 */
	public static final boolean startActivitySafe(String packageName, Context context){
		if(packageName != null){
			Intent main = getAppFilterIntent();
			main.setPackage(packageName);
			List<ResolveInfo> rl = context.getPackageManager().queryIntentActivities(main, 0);
			if(rl != null && rl.size() > 0){
				ActivityInfo ai = rl.get(0).activityInfo;
				main.setPackage(packageName);
				main.setClassName(packageName, ai.name);
				return startActivitySafe(main, context);
			}
		}
		return false;
	}
	
	/**
	 * start activity by ActivityInfo;
	 * if failed, add Flag FLAG_ACTIVITY_NEW_TASK;
	 * if failed, find default activity in package;
	 * @param ai
	 * @param context
	 */
	public static final boolean startActivitySafe(ActivityInfo ai, Context context){
		if(ai != null){
			Intent intent = new Intent();
			intent.setPackage(ai.packageName);
			intent.setClassName(ai.packageName, ai.name);
			if(!startActivitySafe(intent, context)){
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if(!startActivitySafe(intent, context)){
					String pkg = ai.packageName;
					if(pkg != null){
						Intent main = getAppFilterIntent();
						main.setPackage(pkg);
						List<ResolveInfo> rl = context.getPackageManager().queryIntentActivities(main, 0);
						if(rl != null && rl.size() > 0){
							ai = rl.get(0).activityInfo;
							main.setPackage(pkg);
							main.setClassName(pkg, ai.name);
							return startActivitySafe(main, context);
						}
					}
				}
			}
		}
		return false;
	}
	
	public static void uninstallPackage(String packageName, Context context){
		Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", packageName, null));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivitySafe(intent, context);
	}
}
