package com.anson.acode.pm;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.anson.acode.AUtils;
import com.anson.acode.FileUtils;
import com.anson.acode.aos.ManagerTools;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class XPackageHelper {
	
	/**
	 * get all apps from PackageManager
	 * @param pm
	 * @return
	 */
	public static List<PackageInfo> getInstalledPackages(PackageManager pm){
		return pm.getInstalledPackages(0);
	}
	
	/**
	 * get all install apk from packagemanager.
	 * @param pm
	 * @return
	 */
	public static List<XPackageInfo> getInstalledXPackageInfos(PackageManager pm){
		List<XPackageInfo> psi = new ArrayList<XPackageInfo>();
		List<PackageInfo> pis = getInstalledPackages(pm);
		
		for(PackageInfo p:pis){
			psi.add(new XPackageInfo(p, pm));
		}
		
		return psi;
	}
	
	/**
	 * return all apks from path
	 * @param pm
	 * @param path
	 * @return
	 */
	public static List<XPackageInfo> getInstalledXPackageInfosFromPath(PackageManager pm, String path){
		List<XPackageInfo> psi = new ArrayList<XPackageInfo>();
		File[] apks = FileUtils.getFilesFromPathByType(path, FileUtils.TYPE_APK);
		if(AUtils.checkArray(apks)){
			for(File f : apks){
				PackageInfo pi = pm.getPackageArchiveInfo(f.getAbsolutePath(), 0);
				if(pi != null){
					XPackageInfo p = new XPackageInfo(pi, pm, f.getAbsolutePath());
					psi.add(p);
				}
			}
		}
		
		sortXPackageInfo(psi);
		return psi;
	}
	
	/**
	 * get all instaled packages and sort;
	 * @param pm
	 * @return
	 */
	public static List<XPackageInfo> getInstalledXPackageInfosSorted(PackageManager pm){
		List<XPackageInfo> psi = getInstalledXPackageInfos(pm);
		Collections.sort(psi, getAppNameComparatorNisSystem());
		return psi;
	}
	
	public static void sortXPackageInfo(List<XPackageInfo> arr){
		Collections.sort(arr, getAppNameComparatorNisSystem());
	}
	
	/**
	 * comparator to sort app by app name or packagename
	 * @return
	 */
	public static final Comparator<XPackageInfo> getAppNameComparator() {
        final Collator collator = Collator.getInstance();
        return new Comparator<XPackageInfo>() {
            public final int compare(XPackageInfo a, XPackageInfo b) {
                int result = collator.compare(a.getLabel(), b.getLabel());
                if (result == 0) {
                    result = a.getP().packageName.compareTo(b.getP().packageName);
                }
                return result;
            }
        };
    }
	
	/**
	 * comparator to sort app by app name or packagename
	 * @return
	 */
	public static final Comparator<XPackageInfo> getAppNameComparatorNisSystem() {
        final Collator collator = Collator.getInstance();
        return new Comparator<XPackageInfo>() {
            public final int compare(XPackageInfo a, XPackageInfo b) {
                boolean aSys = ManagerTools.isSystemPackageByDir(a.getP());
                boolean bSys = ManagerTools.isSystemPackageByDir(b.getP());
            	int result = aSys && bSys ? 0 : (aSys ? -1: (bSys ? 1 : 0));
                if(result==0)result = collator.compare(a.getLabel(), b.getLabel());
                if (result == 0) {
                    result = a.getP().packageName.compareTo(b.getP().packageName);
                }
                return result;
            }
        };
    }
	
	/**
	 * load all activitys sorted like LAUNCHER
	 * @param intent
	 * @param pm
	 * @return
	 */
	public static List<ResolveInfo> loadAllActivitiesSorted(Intent intent, PackageManager pm){
		List<ResolveInfo> apps = loadAllActivities(intent, pm);
		Collections.sort(apps, getAppNameComparator(pm));
		return apps;
	}
	
	/**
	 * load all activitys like LAUNCHER
	 * @param intent
	 * @param pm
	 * @return
	 */
	public static List<ResolveInfo> loadAllActivities(Intent intent, PackageManager pm){
		List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
		return apps;
	}
	/**
	 * comparator to sort app by app name or packagename
	 * @return
	 */
	public static final Comparator<ResolveInfo> getAppNameComparator(final PackageManager pm) {
        final Collator collator = Collator.getInstance();
        return new Comparator<ResolveInfo>() {
            public final int compare(ResolveInfo a, ResolveInfo b) {
                int result = collator.compare(a.loadLabel(pm), b.loadLabel(pm));
                if (result == 0) {
                    result = a.activityInfo.packageName.compareTo(b.activityInfo.packageName);
                }
                return result;
            }
        };
    }
}
