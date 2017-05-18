package com.anson.acode;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUtils {
	public static final int TYPE_UNKNOW = 0;
	public static final int TYPE_IMAGE = 1;
	public static final int TYPE_VIDEO = 2;
	public static final int TYPE_MUSIC = 3;
	public static final int TYPE_APK = 4;
	public static final int TYPE_TXT = 5;
	public static final int TYPE_WORD = 6;
	public static final int TYPE_PPT = 7;
	public static final int TYPE_XLS = 8;
	public static final int TYPE_PDF = 9;
	public static final int TYPE_HTML = 10;
	public static final String IMAGES[] = {".jpg", ".png", ".gif", ".bmp", ".i", ".co", ".thu"};
	public static final String VIDEOS[] = {".mp4", ".rmvb", ".rm", ".wmv", ".avi"};
	public static final String MUSICS[] = {".mp3", ".acc", ".3gp", ".ogg", ".wma"};
	public static final String APKS[] = {".apk", ".nm"};
	public static final String TXT[] = {".txt", ".java", ".ini"};
	public static final String WORD[] = {".doc", ".docx"};
	public static final String PPT[] = {".ppt", ".pptx"};
	public static final String XLS[] = {".xls", ".xlsx"};
	public static final String PDF[] = {".pdf"};
	public static final String HTML[] = {".htm", ".html", ".shtml", ".php", ".jsp", ".asp"};
	
	public static final char[] BADCHAR = {'/', '\\', '?', '<', '>', '*', '|', ':', '"'};
	public static final char GOODCHAR = '_';
	
	/**
	 * return the type of File
	 * @param f file
	 * @return type via int
	 */
	public static int getFileType(File f){
		int type = TYPE_UNKNOW;
		String name = f.getName().toLowerCase();
		int idx = name.lastIndexOf('.');
		if(idx > 0){
			String ext = name.substring(idx);
			if(isContained(ext, IMAGES))
				return TYPE_IMAGE;
			else if(isContained(ext, VIDEOS))
				return TYPE_VIDEO;
			else if(isContained(ext, MUSICS))
				return TYPE_MUSIC;
			else if(isContained(ext, APKS))
				return TYPE_APK;
			else if(isContained(ext, TXT))
				return TYPE_TXT;
			else if(isContained(ext, WORD))
				return TYPE_WORD;
			else if(isContained(ext, PPT))
				return TYPE_PPT;
			else if(isContained(ext, XLS))
				return TYPE_XLS;
			else if(isContained(ext, PDF))
				return TYPE_PDF;
			else if(isContained(ext, HTML))
					return TYPE_HTML;
		}
		return type;
	}
	
	/**
	 * check the String s is Contained in arr!
	 * @param s
	 * @param arr
	 * @return
	 */
	public static boolean isContained(String s, String[] arr){
		for(String ss : arr){
			if(ss.equals(s))return true;
		}
		return false;
	}
	public static File[] getImageFiles(String path){
		ArrayList<File> files = new ArrayList<File>();
		File parent = new File(path);
		if(parent.isDirectory()){
			File[] fs = parent.listFiles();
			if(fs != null && fs.length >= 1){
				for(File f:fs){
					if(TYPE_IMAGE == getFileType(f)){
						files.add(f);
					}
				}
			}
		}else{
			File[] fs = parent.getParentFile().listFiles();
			if(fs != null && fs.length >= 1){
				for(File f:fs){
					if(TYPE_IMAGE == getFileType(f)){
						files.add(f);
					}
				}
			}
		}
		
		
		int size = files.size();
		if(size >= 1){
			File[] result = new File[size];
			for(int i=0; i<size; i++){
				result[i] = files.get(i);
			}
			sortFile(result);
			return result;
		}
		
		return null;
	}

    /**
     * get Mounted Storage from StorageManager.
     * @param mContext to get StorageManager
     * @return VolumeInfo List
     */
    public static List<VolumeInfo> getMountByReflect(Context mContext) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            //Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object vols = getVolumeList.invoke(mStorageManager);

            final int length = Array.getLength(vols);
            List<VolumeInfo> vis = new ArrayList<VolumeInfo>();
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(vols, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                //boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);

                vis.add(new VolumeInfo(path, dir.equals(path)));
            }
            return vis;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /** class for save storage volume info **/
    public static class VolumeInfo {
        public String path;
        public boolean isInternalSD;
        VolumeInfo(String path, boolean isInternalSD){
            this.path = path;
            this.isInternalSD = isInternalSD;
        }

        public boolean isVolumeAvailable(){
            File f = new File(path);
            return f.exists() && f.canWrite() && f.getTotalSpace() > 0 && f.getFreeSpace() > 0;
        }

        @Override
        public String toString() {
            return "VolumeInfo[path(" + path + "), isInternalSD(" + isInternalSD + ")]";
        }
    }


    /**
	 * get all file sub Path, no Folder.
	 * @param path Folder you want
	 * @return file list
	 */
	public static File[] getAllFileSubPathNoDirectory(String path){
		File f = new File(path);
		if(f.isDirectory()){
			File[] fs = f.listFiles();
			if(fs != null && fs.length > 0){
				int size = 0;
				for(File ff: fs){
					if(ff.isFile())size ++;
				}
				if(size == 0) return null;
				
				File[] result = new File[size];
				int idx = 0;
				for(File ff:fs){
					if(ff.isFile()){
						result[idx] = ff;
						idx ++;
					}
				}
				return result;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	
	public static File[] getImageFiles(File parent){
		ArrayList<File> files = new ArrayList<File>();
		if(parent.isDirectory()){
			File[] fs = parent.listFiles();
			if(fs != null && fs.length >= 1){
				for(File f:fs){
					if(TYPE_IMAGE == getFileType(f)){
						files.add(f);
					}
				}
			}
		}else{
			File[] fs = parent.getParentFile().listFiles();
			if(fs != null && fs.length >= 1){
				for(File f:fs){
					if(TYPE_IMAGE == getFileType(f)){
						files.add(f);
					}
				}
			}
		}
		
		
		int size = files.size();
		if(size >= 1){
			File[] result = new File[size];
			for(int i=0; i<size; i++){
				result[i] = files.get(i);
			}
			sortFile(result);
			return result;
		}
		
		return null;
	}
	
	public static void sortFile(File[] files){
		sortFile(files, true);
	}
	public static void sortFile(File[] files, boolean upOrder){
		if(files == null || files.length < 1)return;
		for(int i=0; i<files.length;i++){
			for(int j=i+1; j<files.length;j++){
				String a = files[i].getName().toLowerCase();
				String b = files[j].getName().toLowerCase();
				if(upOrder){
					if(a.compareTo(b) > 0){
						File temp = files[i];
						files[i] = files[j];
						files[j] = temp;
					}
				}else{
					if(a.compareTo(b) < 0){
						File temp = files[i];
						files[i] = files[j];
						files[j] = temp;
					}
				}
			}
		}
	}
	
	public static File[] getFilesFromPathByType(String path, int type){
		ArrayList<File> files = new ArrayList<File>();
		File parent = new File(path);
		if(parent.isDirectory()){
			File[] fs = parent.listFiles();
			if(fs != null && fs.length >= 1){
				for(File f:fs){
					if(type == getFileType(f)){
						files.add(f);
					}
				}
			}
		}else{
			File[] fs = parent.getParentFile().listFiles();
			if(fs != null && fs.length >= 1){
				for(File f:fs){
					if(type == getFileType(f)){
						files.add(f);
					}
				}
			}
		}
		
		
		int size = files.size();
		if(size >= 1){
			File[] result = new File[size];
			for(int i=0; i<size; i++){
				result[i] = files.get(i);
			}
			sortFile(result);
			return result;
		}
		
		return null;
	}

    /**
     * get file no ext; file.txt -> file
     * @param f file
     * @return simple name
     */
	public static String getSimpleName(File f){
		String name = f.getName();
		int idx = name.lastIndexOf('.');
		if(idx > 1){
			return name.substring(0, idx);
		}
		return name;
	}
	
	public static byte[] getBytesFromLocalFile(String path){
		File f = new File(path);
		if(!f.exists()){
			return null;
		}
		ByteArrayBuffer buf = null;
		FileInputStream fis = null;
		try{
			fis= new FileInputStream(f);
			buf = new ByteArrayBuffer(fis.available());
			int read = 0;
			byte[] buffer = new byte[8 * 1024];
			while((read = fis.read(buffer)) > 0){
				buf.append(buffer, 0, read);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return buf.toByteArray();
		}
	}
	
	public static String getImageFileFromByteArray(byte[] data) {  
        String type = null;  
        // Png test:  
        if (data[1] == 'P' && data[2] == 'N' && data[3] == 'G') {  
            type = "PNG";  
            return type;  
        }  
        // Gif test:  
        if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {  
            type = "GIF";  
            return type;  
        }  
        // JPG test:  
        if (data[6] == 'J' && data[7] == 'F' && data[8] == 'I'  
                && data[9] == 'F') {  
            type = "JPG";  
            return type;  
        }  
        return type; 
	}
	public static int retryTime = 0;
	public static byte[] getBytesFromInputStream(InputStream is, int contentLength){
		ByteArrayBuffer buf = new ByteArrayBuffer(contentLength);
		byte[] cache = new byte[1024 * 8];
		int avaLen = 0;
		int totalLen = 0;
		try {
			while((avaLen = is.read(cache)) > 0){
				buf.append(cache, 0, avaLen);
				totalLen += avaLen;
			}
			if(totalLen < contentLength && retryTime < 3){
				retryTime ++;
				return getBytesFromInputStream(is, contentLength);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		retryTime = 0;
		return buf.toByteArray();
	}
	
	/**
	 * if "/mnt/sdcard/file.ext" return file
	 * @param fullPath file path
	 * @return filename
	 */
	public static String pickFileShortName(String fullPath){
		String target = pickFileName(fullPath);
		int idx = target.indexOf('.');
		if(idx != -1){
			return target.substring(0, idx);
		}else
			return target;
		
	}

    /**
     * if '/mnt/sdcard/file.ext' return file
     * @param fullPath file path
     * @param ext file suffix
     * @return file name
     */
	public static String pickFileShortName(String fullPath, String ext){
		fullPath = fullPath.replaceAll(ext, "");
		return pickFileName(fullPath);
	}
	
	/**
	 * if "/mnt/sdcard/file.ext" return file.ext
	 * @param fullPath full path of file.
	 * @return
	 */
	public static String pickFileName(String fullPath){
		int index = fullPath.lastIndexOf('/');
		if(index != -1){
			return fullPath.substring(index + 1);
		}
		return fullPath;
	}
	public static String pickFileNameWithSpecFormat(String fullPath, String format){
		String name;
		int index = fullPath.lastIndexOf('/');
		if(index != -1){
			name = fullPath.substring(index + 1);
		}else{
			name = fullPath;
		}
		index = name.lastIndexOf('.');
		if(index != -1){
			name = name.substring(0, index);
		}
		name += format;
		return name;
	}
	
	public static boolean saveFileToLocal(String url, String folderPath, String targetName, String ext){
		ALog.alog("Tool", "saveFileToLocal: " + url);
		byte[] content = null;
		boolean success = false;
		File sf = new File(folderPath + targetName + ".temp");
		File target = new File(folderPath + targetName + ext);
		ALog.alog("Tool", "sf: " + sf.getAbsolutePath());
		ALog.alog("Tool", "target: " + target.getAbsolutePath());
		if(target.exists())return true;
		if(content == null){
			content = HttpUtilsAndroid.getByteContentFromURL(url);
		}
		
		try{
	        FileOutputStream fos = null;	        
	        	try {
	        		File f = new File(folderPath);
	        		if(!f.exists()){
	        			f.mkdirs();
	        		}
	        		
					fos = new FileOutputStream(sf);
					fos.write(content);
					success = true;
				} catch (Exception e) {
					// TODO: handle exception
					success = false;
				} finally{
					if(fos != null){
						fos.flush();
						fos.close();
					}
				}
	        
		}catch(Exception e){
			e.printStackTrace();
			success = false;
		}finally{
			if(success) sf.renameTo(target);
			return success;
		}
	}
	
	/**
	 * remove a File or a Path (all file sub Path)
	 * @param f
	 * @return
	 */
	public static boolean removeFile(String f){
		return removeFile(new File(f));
	}
	/**
	 * remove a File or a Path (all file sub Path)
	 * @param f
	 * @return
	 */
	public static boolean removeFile(File f){
		if(f.isDirectory()){
			File[] files = f.listFiles();
			for(File file:files){
				if(!removeFile(file))return false;
			}
			return f.delete();
		}else{
			if(f.exists()){
				return f.delete();
			}
		}		
		return true;
	}

    /**
     * create new Thread to remove file or Folder.
     * @param f file or folder
     * @param h handler to notify finish
     * @param msgFinish message for finish.
     */
    public static void removeFile(String f, final Handler h, final int msgFinish){
        removeFile(new File(f), h, msgFinish);
    }
    public static void removeFile(final File f, final Handler h, final int msgFinish){
        new Thread(){
            @Override
            public void run() {
                removeFile(f);
                if(h != null){
                    h.sendEmptyMessage(msgFinish);
                }
            }
        }.start();
    }
	
	public static void findApkFromDataApp(){
		try {
			final Process process = Runtime.getRuntime().exec("ls -l /data/app");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 8192);
			String readed = null;
			while((readed = reader.readLine()) != null){
				ALog.alog("FileUtils", "____" + readed);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	/**
	 * scan all file sub dir, and add to files.
	 * @param dir
	 * @param files
	 * @return
	 */
	public static ArrayList<File> getAllFileSubDir(File dir, ArrayList<File> files, boolean addfolder){
		if(files == null) files = new ArrayList<File>();
		if(dir.exists()){
			if(dir.isFile())
				files.add(dir);
			else if(dir.isDirectory()){
				File[] fs = dir.listFiles();
				if(AUtils.checkArray(fs)){
					for(File f: fs){
						if(f.isFile()){
							files.add(f);
						}else if(f.isDirectory()){
							if(addfolder)files.add(f);
							getAllFileSubDir(f, files, addfolder);
						}
					}
				}
			}
		}
		
		return files;
	}
	
	/**
	 * write String s to File f
	 * @param f
	 * @param s
	 */
	public static void writeStringToFile(File f, String s){
        byte[] bytes = s.getBytes();
        writeToFile(f, bytes);
	}

    /**
     * write String s to File f
     * @param f target file
     * @param content byte content
     */
    public static void writeToFile(File f, byte[] content){
        File parent = f.getParentFile();
        //ALog.d(parent.getAbsolutePath() + " " + (parent.exists() ? "exists":"not exists"));
        if(!parent.exists()){
            ALog.w("writeToFile create Folder(" + parent.getAbsolutePath() + ") " + parent.mkdirs());
        }
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * always we read a X.txt file, and want to get the content to do something
	 * @param f
	 * @return
	 */
	public static String readFileContentToString(File f){
		StringBuffer sb = new StringBuffer();
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			ByteArrayBuffer bab = new ByteArrayBuffer(2048);
			byte[] cache = new byte[1024];
			int readed = -1;
			while((readed = raf.read(cache)) != -1){
				bab.append(cache, 0, readed);
			}
			sb.append(new String(bab.toByteArray()));
			bab.clear();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	/**
	 * return the path size by long, 1024 * 1024 == 1024 KB == 1MB
	 * @param path
	 * @return
	 */
	public static long calculateFileSize(String path){
		File f = new File(path);
		if(f.exists()){
			if(f.isDirectory()){
				File[] fs = f.listFiles();
				long length = 0;
				if(fs != null && fs.length > 0){
					for(File f1:fs){
						if(f1.isDirectory()){
							length += calculateFileSize(f1.getAbsolutePath());
						}else{
							length += f1.length();
						}
					}
					return length;
				}
			}else{
				return f.length();
			}
		}
		return 0;
	}
	public static final float SIZE_KB = 1024;
	public static final float SIZE_MB = SIZE_KB * 1024;
	public static final float SIZE_GB = SIZE_MB * 1024;
	public static final float SIZE_TB = SIZE_GB * 1024;
	
	/**
	 * return the String of the size, eg. 1024 will "1 KB", 1024 * 1024 will "1MB"...
	 * @param size
	 * @return
	 */
	public static String formatFileSize(long size){
		if(size > SIZE_TB){
			float result = size /SIZE_TB;
			return StringUtils.getStringFromSpecAndSizeAfterIndex(String.valueOf(result), ".", 2) + " TB";
		}else if(size > SIZE_GB){
			float result = size /SIZE_GB;
			return StringUtils.getStringFromSpecAndSizeAfterIndex(String.valueOf(result), ".", 2) + " GB";
		}else if(size > SIZE_MB){
			float result = size /SIZE_MB;
			return StringUtils.getStringFromSpecAndSizeAfterIndex(String.valueOf(result), ".", 2) + " MB";
		}else if(size > SIZE_KB){
			float result = size /SIZE_KB;
			return StringUtils.getStringFromSpecAndSizeAfterIndex(String.valueOf(result), ".", 2) + " KB";
		}else{
			return size + " Byte";
		}
	}
	
	
	/**
	 * get new File with new suffix, eg: 00.jpg  > 00.suffix
	 * @param f
	 * @param suffix
	 * @return
	 */
	public static File renameFileWithSuffix(File f, String suffix){
		String name = f.getAbsolutePath();
		int idx = name.lastIndexOf(".");
		if(idx > 0){
			return new File(name.replace(name.substring(idx), "." + suffix));
		}
		return f;
	}
	
	/**
	 * return the file MineType, like "video/3gpp", "image/jpeg"
	 * 
	 **/
	public static String getFileMineType(File f){
		String fn = f.getAbsolutePath();
		fn = fn.toLowerCase();
		int idx = fn.lastIndexOf(".");
		if(idx > 0 && idx < fn.length()){
			String suffix = fn.substring(idx);
			for(int i=0; i<MIME_MapTable.length; i++){
				if(suffix.equals(MIME_MapTable[i][0])){
					return MIME_MapTable[i][1];
				}
			}
		}
		
		return "*/*";
	}
	
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String getFileModifiedTime(File f){
		long time = f.lastModified();
		return format.format(new Date(time));
	}
	
	public static final String[][] MIME_MapTable={
			 //{后缀名， MIME类型} 
			 {".3gp", "video/3gpp"},
			 {".3gpp", "video/3gpp"},
			 {".apk", "application/vnd.android.package-archive"},
			 {".asf", "video/x-ms-asf"},
			 {".avi", "video/x-msvideo"},
			 {".bin", "application/octet-stream"},
			 {".bmp", "image/bmp"},
			 {".c", "text/plain"},
			 {".class", "application/octet-stream"},
			 {".conf", "text/plain"},
			 {".cpp", "text/plain"},
			 {".doc", "application/msword"},
			 {".exe", "application/octet-stream"},
			 {".gif", "image/gif"},
			 {".gtar", "application/x-gtar"},
			 {".gz", "application/x-gzip"},
			 {".h", "text/plain"},
			 {".htm", "text/html"},
			 {".html", "text/html"},
			 {".i", "image/jpeg"},
			 {".jar", "application/java-archive"},
			 {".java", "text/plain"},
			 {".jpeg", "image/jpeg"},
			 {".jpg", "image/jpeg"},
			 {".js", "application/x-javascript"},
			 {".log", "text/plain"},
			 {".m3u", "audio/x-mpegurl"},
			 {".m4a", "audio/mp4a-latm"},
			 {".m4b", "audio/mp4a-latm"},
			 {".m4p", "audio/mp4a-latm"},
			 {".m4u", "video/vnd.mpegurl"},
			 {".m4v", "video/x-m4v"},
			 {".mov", "video/quicktime"},
			 {".mp2", "audio/x-mpeg"},
			 {".mp3", "audio/x-mpeg"},
			 {".mp4", "video/mp4"},
			 {".mpc", "application/vnd.mpohun.certificate"},
			 {".mpe", "video/mpeg"},
			 {".mpeg", "video/mpeg"},
			 {".mpg", "video/mpeg"},
			 {".mpg4", "video/mp4"},
			 {".mpga", "audio/mpeg"},
			 {".msg", "application/vnd.ms-outlook"},
			 {".ogg", "audio/ogg"},
			 {".pdf", "application/pdf"},
			 {".png", "image/png"},
			 {".pps", "application/vnd.ms-powerpoint"},
			 {".ppt", "application/vnd.ms-powerpoint"},
			 {".prop", "text/plain"},
			 {".rar", "application/x-rar-compressed"},
			 {".rc", "text/plain"},
			 {".rmvb", "audio/x-pn-realaudio"},
			 {".rtf", "application/rtf"},
			 {".sh", "text/plain"},
			 {".tar", "application/x-tar"},
			 {".tgz", "application/x-compressed"},
			 {".txt", "text/plain"},
			 {".wav", "audio/x-wav"},
			 {".wma", "audio/x-ms-wma"},
			 {".wmv", "audio/x-ms-wmv"},
			 {".wps", "application/vnd.ms-works"},
			 //{".xml", "text/xml"}, 
			 {".xml", "text/plain"},
			 {".z", "application/x-compress"},
			 {".zip", "application/zip"},
			 {"", "*/*"}
			 };
	
	/**
	 * copy file or folder to targetFolder
	 * @param src src file
	 * @param targetFolder target folder
	 * @return success
	 */
	public static boolean copyFile(String src, String targetFolder){
		try {
			File F0 = new File(targetFolder);
			if (!F0.exists()) {
				if (!F0.mkdirs()) {
					return false;
				}
			}
			
			File F = new File(src);
			if(F.isFile()){
				return copySingleFile(src, F0.getAbsolutePath() + "/" + F.getName());
			}else if(F.isDirectory()){
				//src = /mnt/sdcard/thumb;
				//                       |-1.jpg, 2.jpg, 
				//                       |-movie
				//                         |-3.jpg
				//tar = /mnt/sdcard/download;
				File tar = new File(F0.getAbsolutePath() +"/" + F.getName());
				tar.mkdir();
				
				File[] allFile = F.listFiles(); 
				int totalNum = allFile.length; 
				String srcName = "";
				String desName = "";
				int currentFile = 0;
				for (currentFile = 0; currentFile < totalNum; currentFile++) {
					if (!allFile[currentFile].isDirectory()) {
						// 如果是文件是采用處理文件的方式
						srcName = allFile[currentFile].toString();
						desName = targetFolder + "/" + F.getName() + "/"+ allFile[currentFile].getName();
						copySingleFile(srcName, desName);
					}else {
						if (copyFile(allFile[currentFile].getPath().toString(),
								targetFolder + "/" + F.getName())) {
						} else {return false;}
					}
				}
				return true;
			}else return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	/**
	 * copy SINGLE file from src to des.
	 * @param src
	 * @param des
	 * @return
	 */
	public static boolean copySingleFile(String src, String des) {
		FileInputStream FIS = null;
		FileOutputStream FOS = null;
		File df = new File(des);
		if(df.exists() && df.length() == new File(src).length()){
			return true;
		}
		File dp = df.getParentFile();
		if(!dp.exists()){
			dp.mkdirs();
		}
		
		try {
			FIS = new FileInputStream(src);
			FOS = new FileOutputStream(des);
			byte[] bt = new byte[1024];
			int readNum = 0;
			while ((readNum = FIS.read(bt)) != -1) {
				FOS.write(bt, 0, readNum);
			}
			FIS.close();
			FOS.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				FIS.close();
				FOS.close();
			} catch (IOException f) {
				// TODO
			}
			return false;
		} finally {
		}
	}

	/**
	 * copy file or folder to targetFolder
     * src(/mnt/sdcard/src) tar(/mnt/external_sd/)
     * dst(/mnt/external_sd);
	 * @param src src folder
	 * @param targetFolder target folder
	 * @return finish
	 */
	public static boolean copyFile(String src, String targetFolder, Handler h, int what){
		try {
            //create target folder if NOT exists
			File target = new File(targetFolder);
			if (!target.exists()) {
				if (!target.mkdirs()) {
					return false;
				}
			}
			
			File F = new File(src);
			if(F.isFile()){
				boolean res = copySingleFile(src, target.getAbsolutePath() + "/" + F.getName(), h, what);
                if(h != null)h.sendEmptyMessage(what);
                return res;
			}else if(F.isDirectory()){
				//src = /mnt/sdcard/thumb;
				//                       |-1.jpg, 2.jpg, 
				//                       |-movie
				//                         |-3.jpg
				//tar = /mnt/sdcard/download;
                //create target folder.
				File tar = new File(target.getAbsolutePath() +"/" + F.getName());
				tar.mkdir();

                //start copy file.
				File[] allFile = F.listFiles(); 
				int totalNum = allFile.length; 
				String srcName = "";
				String desName = "";
				int currentFile = 0;
				for (currentFile = 0; currentFile < totalNum; currentFile++) {
					if (!allFile[currentFile].isDirectory()) {
						// 如果是文件是采用處理文件的方式
						srcName = allFile[currentFile].getAbsolutePath();
						desName = tar.getAbsolutePath() + "/"+ allFile[currentFile].getName();
						copySingleFile(srcName, desName, h, what);
					}else {
						copyFile(allFile[currentFile].getAbsolutePath(), tar.getAbsolutePath());
					}
				}
				return true;
			}else return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
            if(h != null)h.sendEmptyMessage(what);
        }
    }
	
	/**
	 * copy SINGLE file from src to des.
	 * @param src
	 * @param des
	 * @return
	 */
	public static boolean copySingleFile(String src, String des, Handler h, int what) {
		FileInputStream FIS = null;
		FileOutputStream FOS = null;
		File df = new File(des);
		if(df.exists() && df.length() == new File(src).length()){
			return true;
		}
		File dp = df.getParentFile();
		if(!dp.exists()){
			dp.mkdirs();
		}
		try {
			FIS = new FileInputStream(src);
			FOS = new FileOutputStream(des);
			long length = new File(src).length();
			long progress = 0;
			byte[] bt = new byte[1024];
			int readNum = 0;
			while ((readNum = FIS.read(bt)) != -1) {
				FOS.write(bt, 0, readNum);
				
				progress += readNum;
				h.obtainMessage(what, progress * 100 / length);
			}
			FIS.close();
			FOS.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				FIS.close();
				FOS.close();
			} catch (IOException f) {
				// TODO
			}
			return false;
		} finally {
		}
	}
	/**
	 * copy SINGLE file from src to des.
	 * @param src
	 * @param des
	 * @return
	 */
	public static boolean copySingleFile(String src, String des, Handler h, int msg_start, int msg_update, int msg_success, int msg_failed) {
		FileInputStream FIS = null;
		FileOutputStream FOS = null;
		File df = new File(des);
		h.sendEmptyMessage(msg_start);
		if(df.exists() && df.length() == new File(src).length()){
			h.sendMessage(MSG.formatResultMessage(msg_failed, src + ":" + des));
			return true;
		}
		File dp = df.getParentFile();
		if(!dp.exists()){
			dp.mkdirs();
		}
		try {
			FIS = new FileInputStream(src);
			FOS = new FileOutputStream(des);
			long length = new File(src).length();
			long progress = 0;
			byte[] bt = new byte[1024];
			int readNum = 0;
			while ((readNum = FIS.read(bt)) != -1) {
				FOS.write(bt, 0, readNum);
				
				progress += readNum;
				h.sendMessage(MSG.formatUpdateMessage(msg_update, (int)(progress * 100 / length)));
			}
			FIS.close();
			FOS.close();
			h.sendMessageDelayed(MSG.formatResultMessage(msg_success, src + ":" + des), 100);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			h.sendMessageDelayed(MSG.formatResultMessage(msg_failed, src + ":" + des), 100);
			try {
				FIS.close();
				FOS.close();
			} catch (IOException f) {
				// TODO
			}
			return false;
		} finally {
		}
	}
	
	/**
	 * return all Files sub path. check file is hidden at the same time;
	 * @param path
	 * @param includeHide
	 * @return
	 */
	public static File[] getFileList(String path, boolean includeHide){
		File[] list = new File(path).listFiles();
		
		if(includeHide){
			return list;
		}else{
			if(list != null){
				ArrayList<File> fs = new ArrayList<File>();
				for(File f:list){
					if(!f.isHidden() && !f.getName().startsWith(".")){
						fs.add(f);
					}
				}
				File[] contents = new File[fs.size()];
				fs.toArray(contents);
				return contents;
			}else{
				return null;
			}
		}
	}
	
	/**
	 * return File[] list from list
	 * @param list
	 * @param typeFile
	 * @return
	 */
	public static File[] getFileList(File[] list, boolean typeFile){
		if(list != null){
			ArrayList<File> fs = new ArrayList<File>();
			for(File f:list){
				if(typeFile && f.isFile()){
					fs.add(f);
				}else if(!typeFile && f.isDirectory()){
					fs.add(f);
				}
			}
			File[] contents = new File[fs.size()];
			fs.toArray(contents);
			return contents;
		}else{
			return null;
		}
	}
	
	public static void sortFileWithChinese(File[] fs){
		if(fs == null)return;
		String ss[] = new String[fs.length];
		for(int i=0; i<fs.length; i++){
			ss[i] = fs[i].getAbsolutePath();
		}
		
		AUtils.shortArray(ss, AUtils.getChinaComparator());
		
		for(int i=0; i<fs.length; i++){
			fs[i] = new File(ss[i]);
		}
	}
	
	/**
	 * check the file name include badchar, and replace with good char
	 * @param title
	 * @return
	 */
	public static String checkFileName(String title){
		if(title != null){
			char[] cs = title.toCharArray();
			for(int i=0; i<cs.length; i++){
				for(int j=0;j<BADCHAR.length; j++){
					if(cs[i] == BADCHAR[j]){
						cs[i] = GOODCHAR;
						break;
					}
				}
			}
			
			return new String(cs);
		}else{
			return title;
		}
	}
	
	public static File getExternalSD(){
		File rk = new File("/mnt/external_sd");
		if(rk.exists())return rk;
		File mtk = new File("/storage/sdcard1");
		if(mtk.exists())return mtk;
		File amlogic = new File("/storage/external_storage/sdcard1");
		if(amlogic.exists())return amlogic;
		File normal = new File("/mnt/sdcard");
		return normal;
	}
	
	/**
	 * check ABS file exist, if parent folder not exist, create it;
	 * @param f
	 */
	public static final void checkFolderAndCreate(String f){
		File fi = new File(f);
		if(fi.exists())return;
		else{
			File p = fi.getParentFile();
			if(p.exists())return;
			else
				p.mkdirs();
		}
	}

    /**
     * create .nomedia in the special folder
     * @param folder folder path.
     * @param ignoreNoFile if true, create whatever.
     */
    public static void createNoMediaFile(String folder, boolean ignoreNoFile){
        File f = new File(folder + "/.nomedia");
        if(f.exists())return;
        try {
            if(f.getParentFile().exists()) {
                boolean b = f.createNewFile();
                if (!b) ALog.w("create file failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
