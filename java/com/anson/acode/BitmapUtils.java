package com.anson.acode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

public class BitmapUtils {
	public static final String TAG = "BitmapUtils";
	/**
	 * get a bitmap from the given file path
	 * @param file abs file path.
	 * @return Bitmap
	 */
	public static Bitmap getBitmapFromLocal(String file){
		File f = new File(file);
		if(f.exists()){
			return BitmapFactory.decodeFile(file);
		}else
			return null;
	}
	
	/**
	 * read a bitmap from the given url, and if save is true, it will save it to localpath with fileName
	 * @param url remote file path;
	 * @param localFolder locale path to save file /mnt/sdcard/
	 * @param fileName file name you want to save abc.jpg
	 * @param save save or not? if true it will save to local path, of false, nothing to do in local
	 * @return return a bitmap to show in window
	 */
	public static Bitmap getBitmapFromUrl(String url, String localFolder, String fileName, HttpUtilsAndroid.HttpProgressListener progressListener, boolean save){
		ALog.alog("BitmapUtils", "url = " + url);
		ALog.alog("BitmapUtils", "localFolder = " + localFolder);
		ALog.alog("BitmapUtils", "fileName = " + fileName);
		byte[] data;
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		try {
			if(url.contains(" ")){
				url = url.replaceAll(" ", "%20");
			}
            URLConnection urlConn = new URL(url).openConnection();
            urlConn.setConnectTimeout(60 * 1000);
            urlConn.connect();
			data = HttpUtilsAndroid.getResponseEntityBytes(urlConn, progressListener);
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ALog.alog("BitmapUtils", "download " + fileName + " completed!");
			if(save){
				ALog.alog("BitmapUtils", "getBitmapFromUrl save to file!");
				File f = new File(localFolder);
				if(!f.exists()) {
                    boolean mkdirs = f.mkdirs();
                    if(!mkdirs){
                        ALog.w(TAG, "create folder error " + f.getAbsolutePath());
                    }
                }
				fos = new FileOutputStream(f.getAbsoluteFile() + "/" + fileName);
				fos.write(data);
				fos.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		} finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return bitmap;
	}
	
	public static Bitmap getBitmapFromUrlWithHeaders(String url, String localPath, String fileName, boolean save,
			String[] header, String[] headerValue){
		ALog.alog("BitmapUtils", "url = " + url);
		ALog.alog("BitmapUtils", "localPath = " + localPath);
		ALog.alog("BitmapUtils", "fileName = " + fileName);
		byte[] data;
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		try {
			if(url.contains(" ")){
				url = url.replaceAll(" ", "%20");
			}
            URLConnection urlConn = new URL(url).openConnection();
            urlConn.setConnectTimeout(60 * 1000);
			if(header != null && headerValue != null && header.length == headerValue.length){
                for(int i=0; i<header.length;i++){
                    urlConn.setRequestProperty(header[i], headerValue[i]);
                }
            }
            urlConn.connect();
            data = HttpUtilsAndroid.getResponseEntityBytes(urlConn, null);
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			if(save){
				ALog.alog("BitmapUtils", "getBitmapFromUrl save to local");
				File f = new File(localPath);
				if(!f.exists()) {
                    boolean b = f.mkdirs();
                    if(!b)ALog.w(TAG, "crate folder error : " + f.getAbsolutePath());
                }
				fos = new FileOutputStream(f.getAbsoluteFile() + "/" + fileName);
				fos.write(data);
				fos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		} finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return bitmap;
	}
	
	/**
	 * read a bitmap from the given url, and if save is true, it will save it to localpath with fileName
	 * @param url remote file path;
	 * @param localPath locale path to save file /mnt/sdcard/
	 * @param fileName file name you want to save abc.jpg
	 * @param save save or not? if true it will save to local path, of false, nothing to do in local
	 * @param headerName some special header need to add to request
	 * @param headerValue values of header
	 * @return return a bitmap to show in window
	 */
	public static Bitmap getBitmapFromUrlWidthHeader(String url, String localPath, String fileName,
			boolean save, String headerName, String headerValue, HttpUtilsAndroid.HttpProgressListener cb){
		String log = "url = " + url;
		log += "\nlocalPath = " + localPath;
		log += "\nfileName = " + fileName;
		log += "\nheader = " + headerValue;
		ALog.alog(TAG, log);
		byte[] data;
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		try {
            URLConnection urlConn = new URL(url).openConnection();
            urlConn.setConnectTimeout(60 * 1000);
            urlConn.setRequestProperty(headerName, headerValue);
			urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31");
            urlConn.connect();
            //req.addHeader("If-None-Match", "7149c5199067cf1:0");
			//req.addHeader("Accept", "*/*");
			/*req.addHeader("If-Modified-Since", "Sun, 04 May 2014 11:58:02 GMT");
			req.addHeader("Accept-Encoding", "gzip,deflate,sdch");
			req.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
			req.addHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");*/
			//User-Agent: Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7
			data = HttpUtilsAndroid.getResponseEntityBytes(urlConn, cb);
			//bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			if(save){
				File f = new File(localPath);
				if(!f.exists()) {
                    boolean b = f.mkdirs();
                    if(!b)ALog.w(TAG, "crate folder error : " + f.getAbsolutePath());
                }
				fos = new FileOutputStream(f.getAbsoluteFile() + "/" + fileName);
				fos.write(data);
				//log += "\nlength = " + data.length;
				fos.flush();
				fos.close();
				//ALog.alog(TAG, "file:" + f.getAbsoluteFile() + "/" + fileName + " = " + 
				//		(new File(f.getAbsoluteFile() + "/" + fileName).exists()));
				bitmap = BitmapUtils.decodeBitmapWithExceptionCatch(f.getAbsoluteFile() + "/" + fileName);
			}else{
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		} finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return bitmap;
	}
	
	/**
	 * read a bitmap from the given url, and if save is true, it will save it to localpath with fileName
	 * @param url remote file path;
	 * @param localPath locale path to save file /mnt/sdcard/
	 * @param fileName file name you want to save abc.jpg
	 * @param save save or not? if true it will save to local path, of false, nothing to do in local
	 * @return return a bitmap to show in window
	 */
	public static Bitmap getBitmapFromUrl(String url, String localPath, String fileName, boolean save, Handler h, int msgSucc, int msgFail){
		ALog.alog("getBitmapFromUrl", "url = " + url);
		ALog.alog("getBitmapFromUrl", "localPath = " + localPath);
		ALog.alog("getBitmapFromUrl", "fileName = " + fileName);
		byte[] data;
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		try {
            URLConnection urlConn = new URL(url).openConnection();
            urlConn.setConnectTimeout(60 * 1000);
            urlConn.connect();
			data = HttpUtilsAndroid.getResponseEntityBytes(urlConn, null);
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			if(save){
				File f = new File(localPath);
				if(!f.exists()) {
                    boolean b = f.mkdirs();
                    if(!b)ALog.w(TAG, "crate folder error : " + f.getAbsolutePath());
                }
				fos = new FileOutputStream(f.getAbsoluteFile() + "/" + fileName);
				fos.write(data);
				fos.flush();
			}
            if(h != null)h.sendEmptyMessage(msgSucc);
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
            if(h != null)h.sendEmptyMessage(msgFail);
		} finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return bitmap;
	}
	
	/**
	 * this method should make load image safely;
	 * just like OOM, or NP;
	 * @param path full path of file
	 * @return Bitmap
	 */
	public static Bitmap decodeBitmapWithExceptionCatch(String path){
		Bitmap bm = null;
		int realSize[] = decodeBitmapBoundsOnly(path);
		if(realSize == null)return null;
		boolean needScale = false;
		while(bm == null){
			try{
				if(needScale){
					realSize[0] *= 0.8;
					realSize[1] *= 0.8;
					bm = decodeScaledBitmapKeepWHRateBySpecWH(path, realSize[0], realSize[1]);
				}else{
					bm = BitmapFactory.decodeFile(path);
					if(bm == null){
						break;
					}
				}
				
			}catch(OutOfMemoryError oom){
				ALog.alog("Tool", "OutOfMemory Exception : file is too large, should scale");
				bm = null;
				needScale = true;
			}catch(NullPointerException np){
				bm = null;
				break;
			}
		}
		
		return bm;
	}
	
	/**
	 * decode bitmap safe. with the first SampleSize, if exception, SampleSize ++
	 * @param path fill full path
	 * @param SampleSize sample
	 * @return Bitmap of bitmap
	 */
	public static Bitmap decodeBitmapFileSafe(String path, int SampleSize){
		Bitmap bm = null;
		try{
			SampleSize = SampleSize < 1?1:SampleSize;
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = SampleSize;
			bm = BitmapFactory.decodeFile(path, opts);
		}catch(OutOfMemoryError oom){
			return decodeBitmapFileSafe(path, SampleSize + 1);
		}
		return bm;
	}

	public static Bitmap decodeBitmapSafe(String path, int tarWidth, int tarHeight){
        int size[] = decodeBitmapBoundsOnly(path);
        if(size == null) return null;
        int sampleSizeW = tarWidth <= 0 ? Integer.MAX_VALUE : size[0]/tarWidth;
        int sampleSizeH = tarHeight <= 0 ? Integer.MAX_VALUE : size[1]/tarHeight;
		if(tarWidth <= 0 && tarHeight <= 0){
			sampleSizeH = 1;
		}
		//ALog.d(TAG, "decodeBitmapSafe sampleSizeW(" + sampleSizeW + "),sampleSizeH(" + sampleSizeH + "), tarWidth(" + tarWidth + "),tarHeight(" + tarHeight + ")");
        return decodeBitmapFileSafe(path, Math.min(sampleSizeW, sampleSizeH));
    }
	
	/**
	 * this method will return a scaled image, and will keep the width and height rate.
	 * @param path:String; abs path of the file.
	 * @param w:int;  width you want
	 * @param h:int; height you want
	 * @return scaled bitmap
	 */
	public static Bitmap decodeScaledBitmapKeepWHRateBySpecWH(String path, int w, int h){
		int size[] = decodeBitmapBoundsOnly(path);
		if(size == null) return null;
		float r1 = size[0]/(float)size[1];
        float r2, rate;
        if(w <= 0 || h <= 0){
            rate = r1;
        }else {
            r2 = w / (float) h;
            rate = r1 > r2 ? w/(float)size[0] : h/(float)size[1];
        }
		BitmapFactory.Options opts = new BitmapFactory.Options();
		Bitmap temp = BitmapFactory.decodeFile(path, opts);
        Bitmap bm = Bitmap.createScaledBitmap(temp, (int)(size[0] * rate), (int)(size[1] * rate), false);
        temp.recycle();
        return bm;
	}

	/**
	 * this method will return a scaled image, and will keep the width and height rate.
	 * @param w:int;  width you want
	 * @param h:int; height you want
	 * @return Bitmap
	 */
	public static Bitmap decodeScaledBitmapKeepWHRateBySpecWH(Bitmap bm, int w, int h){
		Bitmap temp = null;
		int size[] = {bm.getWidth(), bm.getHeight()};
		if(size == null) return null;
		float r1 = size[0]/(float)size[1];
		float r2 = w/(float)h;
		float rate = r1 > r2 ? w/(float)size[0] : h/(float)size[1];
		//BitmapFactory.Options opts = new BitmapFactory.Options();
		//opts.inSampleSize = (int)(1/rate);
		ALog.alog("BitmapUtils", "decodeScaledBitmapKeepWHRateBySpecWH , rate = " + rate);
		temp = bm;
		//if(size[0] > w || size[1] > h){
			Bitmap bm2 = Bitmap.createScaledBitmap(temp, (int)(size[0] * rate), (int)(size[1] * rate), false);
			temp.recycle();
			return bm2;
		//}else{
		//	return temp;
		//}
	}
	
	/**
	 * mode: 0=normal, 1=autoscale, 2=fitscreen
	 * @param bmWidth bitmap width
	 * @param bmHeight bitmap height
	 * @param viewWidth view width
	 * @param viewHeight view height
	 * @return mode values
	 */
	public static float[] getScaleByMode(int bmWidth, int bmHeight, int viewWidth, int viewHeight){
		float[] scale = {1, 1, 1};
		float bmRate = bmWidth/(float)bmHeight;
		float viRate = viewWidth/(float)viewHeight;
		//if(mode == 1){
			scale[1] = bmRate <= viRate ? viewWidth/(float)bmWidth : viewHeight/(float)bmHeight;
		//}else if(mode == 2){
			scale[2] = bmRate >= viRate ? viewWidth/(float)bmWidth : viewHeight/(float)bmHeight;
		//}
		//ALog.alog("BitmapUtils", "bmRate = " + bmWidth + "/" + bmHeight + " = "+ bmRate + 
		//		", viRate = " + viewWidth + "/" + viewHeight + " = " + viRate + ", scale = " + scale);
		return scale;
	}
	/**
	 * get images width and height.
	 * @param path:String; abs path of the file.
	 * @return int[2], int[0] = width, int[1] = height;
	 */
	public static int[] decodeBitmapBoundsOnly(String path){
		try{
			File f = new File(path);
			if(!f.exists()) return null;
			int[] size = new int[2];
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			size[0] = opts.outWidth;
			size[1] = opts.outHeight;
			return size;
		}catch(Exception e){
			return null;
		}
	}
	
	
	/**
	 * get images width and height.
	 * @param is: InputStram of file
	 * @return int[2], int[0] = width, int[1] = height;
	 */
	public static int[] decodeBitmapBoundsOnly(InputStream is){
		try{
			int[] size = new int[2];
			Rect area = new Rect();
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, area, opts);
			size[0] = opts.outWidth;
			size[1] = opts.outHeight;
			return size;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * clip a Bitmap from given area(Rect)
	 * @param path file path
	 * @param r area you want
	 * @return
	 */
	public static Bitmap clipBitmapWidthBounds(String path, Rect r){
		Bitmap bm = null;
		bm = Bitmap.createBitmap(BitmapFactory.decodeFile(path), r.left, r.top, r.width(), r.height());
		return bm;
	}
	
	/**
	 * clip an Bitmap from givien area(Rect)
	 * @param bitmap ORIGIN bimap
	 * @param r area you want
	 * @return
	 */
	public static Bitmap clipBitmapWidthBounds(Bitmap bitmap, Rect r){
		Bitmap bm = null;
		bm = bm.createBitmap(bitmap, r.left, r.top, r.width(), r.height());
		return bm;
	}
	
	/**
	 * compress a bitmap object to a byte arry
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm){   
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();     
	    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);     
	    return baos.toByteArray();   
	}
	
	/**
	 * input the ORIGIN rect and the scale rate, and will get a rect SCALED
	 * @param r
	 * @param scale
	 * @return
	 */
	public static Rect getScaledRectFromCenter(Rect r, float scale){
		Rect scaled;
		scaled = new Rect(r);
		int sx =((int)(r.width() * (scale-1)))>>1;
		int sy =((int)(r.height() * (scale-1)))>>1;
		scaled.left -= sx;
		scaled.right += sx;
		scaled.top -= sy;
		scaled.bottom += sy;
		return scaled;
	}
	
	/**
	 * input the ORIGIN rect and the scale rate, and will get a rect SCALED
	 * @param r
	 * @param scale
	 * @return
	 */
	public static void scaleRecdWithScaleValue(Rect r, float scale){
		int sx =((int)(r.width() * (scale-1)))>>1;
		int sy =((int)(r.height() * (scale-1)))>>1;
		r.left -= sx;
		r.right += sx;
		r.top -= sy;
		r.bottom += sy;
	}
	
	/** 
	 * get an bitmap scaled and keep the w/h rate
	 * @param path file path
	 * @param w width you want
	 * @param h height you want
	 * @return
	 */
	public static Bitmap decodeScaledBitmapFromPathBySpecWH(String path, int w, int h){
		Bitmap bm = null;
		int size[] = decodeBitmapBoundsOnly(path);
		float r1 = size[0]/(float)size[1];
		float r2 = w/(float)h;
		float rate = r1 > r2 ? w/(float)size[0] : h/(float)size[1];
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = (int)(1/rate);
		bm = BitmapFactory.decodeFile(path, opts);
		//bm = getScaledBitmapBySpecWH(bm, w, h);
		return bm;
	}
	
	/**
	 * get a bitmap scaled with width and height you want (not keep rate)
	 * @param src
	 * @param dstW
	 * @param dstH
	 * @return
	 */
	public static Bitmap getScaledBitmapBySpecWH(Bitmap src, int dstW, int dstH){
		Bitmap bm;
		bm = src.createScaledBitmap(src, dstW, dstH, false);
		return bm;
	}
	
	/**
	 * create an Bitmap from Drawable
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
	        
	        Bitmap bitmap = Bitmap.createBitmap(
	                                        drawable.getIntrinsicWidth(),
	                                        drawable.getIntrinsicHeight(),
	                                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
	                                                        : Bitmap.Config.RGB_565);
	        Canvas canvas = new Canvas(bitmap);
	        //canvas.setBitmap(bitmap);
	        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
	        drawable.draw(canvas);
	        return bitmap;
	}
	
	/**
	 * always we get a InputStream form assets, this can change it to a Bitmap
	 * @param is
	 * @return
	 */
	public static Bitmap decodeBitmapFromInputStream(InputStream is){
		Bitmap bm = null;
		bm = BitmapFactory.decodeStream(is);
		return bm;
	}
	
	/**
	 * always we get a InputStream form assets, this can change it to a Bitmap with Width and height
	 * @param is
	 * @return
	 */
	public static Bitmap decodeBitmapFromInputStream(InputStream is, int w, int h){
		Bitmap bm = null;
		bm = BitmapFactory.decodeStream(is);
	    int[] size = decodeBitmapBoundsOnly(is);
	    
	    BitmapFactory.Options opt = new BitmapFactory.Options();
	    int sampleSize = Math.min(size[0] /w, size[1]/h);
	    opt.inSampleSize = sampleSize;
	    
	    bm = BitmapFactory.decodeStream(is, null, opt);
		return bm;
	}
	
	/**
	 * save View to a Image
	 * @param v: also can use activity.getWindow()..getDecorView();
	 * @param fullPath: like '/mnt/sdcar/abcd.png'
	 * @param quality: 0 ~ 100
	 */
	public static void saveViewToImage(View v, String fullPath, int quality){
		Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

		v.draw(new Canvas(bmp));//bmp就是截取的图片了，可通过bmp.compress(CompressFormat.PNG, 100, new FileOutputStream(file));把图片保存为文件。
		
		try {
			bmp.compress(CompressFormat.PNG, quality, new FileOutputStream(fullPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

    public static boolean isBitmapAvailable(Bitmap bm){
        return bm != null && !bm.isRecycled();
    }
}
