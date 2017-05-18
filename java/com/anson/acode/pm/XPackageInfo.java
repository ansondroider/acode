package com.anson.acode.pm;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;

import com.anson.acode.ALog;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;

public class XPackageInfo {
	String apkFile;
	String libFile;
	String dataFile;
	String label;
	int icon;
	Resources res;
	PackageInfo p;
    public static final String TAG = "XPackageInfo";
	
	public XPackageInfo(PackageInfo pi, PackageManager pm){
		p = pi;
		apkFile = pi.applicationInfo.sourceDir;
		libFile = pi.applicationInfo.nativeLibraryDir;
		dataFile = pi.applicationInfo.dataDir;
		label = pi.applicationInfo.loadLabel(pm).toString();
		
		/*try {
			res = pm.getResourcesForApplication(pi.applicationInfo);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public XPackageInfo(PackageInfo pi, PackageManager pm, String path){
		pi.applicationInfo.sourceDir = path;
		pi.applicationInfo.publicSourceDir = path;
		p = pi;
		apkFile = path;
		libFile = pi.applicationInfo.nativeLibraryDir;
		dataFile = pi.applicationInfo.dataDir;
		label = pm.getApplicationLabel(pi.applicationInfo).toString();
		
		/*try {
			res = pm.getResourcesForApplication(pi.applicationInfo);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public Drawable loadIcon(PackageManager pm){
		return p.applicationInfo.loadIcon(pm);
	}

	public String getApkFile() {
		return apkFile;
	}

	public void setApkFile(String apkFile) {
		this.apkFile = apkFile;
	}

	public String getLibFile() {
		return libFile;
	}

	public void setLibFile(String libFile) {
		this.libFile = libFile;
	}

	public String getDataFile() {
		return dataFile;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public Resources getRes() {
		return res;
	}

	public void setRes(Resources res) {
		this.res = res;
	}

	public PackageInfo getP() {
		return p;
	}

	public void setP(PackageInfo p) {
		this.p = p;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Path=").append(apkFile).append("\n");
		sb.append("Lib=").append(libFile).append("\n");
		sb.append("Data=").append(dataFile).append("\n");
		sb.append("Label=").append(label).append("\n\n");
		return sb.toString();
	}

    /*public static String[] parseActivities(File f, Resources res){
        String[] result = new String[2];
        String mArchiveSourcePath = f.getPath();
        XmlResourceParser parser;
        AssetManager assmgr;
        try {
            assmgr = new AssetManager();
            int cookie = assmgr.addAssetPath(mArchiveSourcePath);
            if (cookie != 0) {
                //res = new Resources(assmgr, res.getDisplayMetrics(), null);
                assmgr.setConfiguration(0, 0, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        17);
                parser = assmgr.openXmlResourceParser(cookie, "AndroidManifest.xml");

                int type;
                int outerDepth = parser.getDepth();
                StringBuilder sb = new StringBuilder();
                while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                        && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {

                    int depth = parser.getDepth();
                    //sb.append("\nSTART\n");
                    String tagName = parser.getName();
                    if(type == XmlPullParser.END_TAG){
                        continue;
                    }
                    //sb.append(tagName);
                    int attrCount = parser.getAttributeCount();
                    if(attrCount > 0 && tagName.contains("activity")){
                        for(int i = 0; i < attrCount; i ++){
                            String attrName = parser.getAttributeName(i);
                            String attrValue = parser.getAttributeValue(i);

                            if(attrName.equals("name")){
                                sb.append(attrValue).append("\n");
                            }
                        }
                    }
                    //sb.append("\nEND\n");
                }
                //sb.delete(sb.length()-3, sb.length()-1);
                result = sb.toString().split("\n");
                ALog.d("parseActivities found " + result.length + " activity");

            } else {
                ALog.alog(TAG, "Failed adding asset path:" + mArchiveSourcePath);
            }
        } catch (Exception e) {
            ALog.alog(TAG, "Unable to read AndroidManifest.xml of "
                    + mArchiveSourcePath);
        }
        return result;
    }*/
	
}
