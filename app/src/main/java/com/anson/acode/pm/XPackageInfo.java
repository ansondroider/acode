package com.anson.acode.pm;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class XPackageInfo {
	String apkFile;
	String libFile;
	String dataFile;
	String label;
	int icon;
	Resources res;
	PackageInfo p;
	
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
	
}
