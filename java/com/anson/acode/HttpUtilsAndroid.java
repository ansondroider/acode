package com.anson.acode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.anson.acode.download.DownloadThread;
import com.anson.acode.multipart.FilePart;
import com.anson.acode.multipart.Part;
import com.anson.acode.multipart.ProgressMultipartEntity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtilsAndroid {
	public static final String TAG = "HttpUtilsAndroid";
	static final String defaultProtocal = "http://";
	static final String protocalSpec = "://";
	static final int CONNECTION_TIMEOUT = 60 * 1000;// 1 mins
	static final int CACHE_SIZE = 8 * 1024;
	
	/**
	 * getHttpClient with connection timeout 

	public static HttpClient getHttpClient(int timeout) {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        //Maybe need ClientConnectionManager
        return new DefaultHttpClient(params);
    }*/
	
	/** 
	 * getHttpClient by Default;

	public static HttpClient getHttpClient() {
        return getHttpClient(CONNECTION_TIMEOUT);
    }
	*/
	
	/**
	 * request.setHeader();
	 * can add request Header to request;
	 * request.setEntry();
	 * can add entry to request, but remember to set the ContentType to request.
	 */
	
	/** get Request
	public static HttpGet getGetRequest(String url){
		HttpGet request;
		if(url.contains(protocalSpec)){
            request = new HttpGet(url);
        }else{
            //ALog.d(TAG, "getGetRequest " + (defaultProtocal + url));
            request = new HttpGet(defaultProtocal + url);
        }
		return request;
	}*/
	
	/** post Requst
	public static HttpPost getPostRequest(String url){
		HttpPost request;
		if(url.contains(protocalSpec)){
			request = new HttpPost(url);
		}else{
			request = new HttpPost(defaultProtocal + url);
		}
		return request;
	}*/
	
	/** put Request
	public static HttpPut getPutRequest(String url){
		HttpPut request;
		if(url.contains(protocalSpec)){
			request = new HttpPut(url);
		}else{
			request = new HttpPut(defaultProtocal + url);
		}
		return request;
	}*/
	
	/**
	 * load content from special url, and will send the message to handler;
	 * @param url request url
     * @return content from url
	 */
	public static String getContentFromURL(final String url){
		return getContentFromURL(url, "GB2312");
	}
	
	/**
	 * get the content from url and encode with the special encode, like GB2312, UTF-8 etc.
	 * @param url request url
	 * @param encode endoding, eg: GBK, UTF-8
	 * @return content from request
	 */
	
	public static String getContentFromURL(final String url, String encode){
		return getContentFromURL(url, encode, null, null);
	}
	
	/**
	 * get response string from url, with encode, params, paramsvalues
	 * @param url request url
	 * @param encode eg: utf-8, gbk
	 * @param params params headers
	 * @param values params headers
	 * @return string content
	 */
	public static String getContentFromURL(final String url, String encode, String[] params, String[] values){
		String result;
        try {
            URLConnection urlConn = new URL(url).openConnection();
            urlConn.setConnectTimeout(60 * 1000);
            if(params != null && values != null){
                if(params.length == values.length){
                    //HttpParams hp = new BasicHttpParams();
                    for(int i=0; i<params.length; i++){
                        urlConn.setRequestProperty(params[i], values[i]);
                    }
                }
            }
            urlConn.connect();
            byte[] bytes = HttpUtilsAndroid.getResponseEntityBytes(urlConn, null);
            result =new String(bytes, encode);
        } catch (IOException e) {
            result = e.getMessage();
        }
		return result;
	}
	
	/**
	 * request will pretend as Chrome;
	 * @param url request request
	 * @param encode eg: gbk utf-8
	 * @return string content
	 */
	public static String getContentFromURL_CHROME(final String url, String encode){
		String params[] = {"User-Agent"};
		String values[] = {"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7"};
		return getContentFromURL(url, encode, params, values);
	}
	
	/**
	 * request will pretend as Chrome;
	 * @param url request url
	 * @param encode eg: utf-8, gbk
	 * @return string content
	 */
	public static String getContentFromURL_CHROME(final String url, String encode, String params[], String values[]){
		String[] realParams = new String[params.length + 1];
		System.arraycopy(params, 0, realParams, 0, params.length);
		String[] realValues = new String[values.length + 1];
		System.arraycopy(values, 0, realValues, 0, values.length);
		
		realParams[params.length] = "User-Agent";
		realValues[values.length] = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7";
		return getContentFromURL(url, encode, realParams, realValues);
	}
	
	/**
	 * load content from special url, and will send the message to handler;
	 * @param url request url
	 */
	public static byte[] getByteContentFromURL(final String url){
		byte[] bytes = null;
		try {
		    URLConnection urlConn = new URL(url).openConnection();
		    urlConn.connect();
			bytes = HttpUtilsAndroid.getResponseEntityBytes(urlConn, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public static String getContentFromURLWithHeaders(final String url){
		return getContentFromURL_CHROME(url, "GBK");
	}
	
	/**
	 * download file from url, and report progress by HttpUtilsAndroid.HttpProgressListener;
	 * @param url request url
	 * @param localFolder local folder
     * @param fileName local file name.
    */
	public static void downloadFileFromUrl(String url, String localFolder, String fileName, HttpProgressListener progressLis){
    	boolean hasListener = progressLis != null;
		try {
            if(null != progressLis)progressLis.onRequestStart();
            URLConnection urlConn = new URL(url).openConnection();
            urlConn.setConnectTimeout(60 * 1000);
            urlConn.connect();
	    	int resCode = ((HttpURLConnection)urlConn).getResponseCode();
	    	if(200 != resCode){
	    		ALog.e("ERROR in download file response:" + resCode);
		    	if(hasListener)progressLis.onResponse(resCode);
		    	return;
	    	}
	    	long length = urlConn.getContentLength();
	    	long progress = 0;
	    	InputStream is = urlConn.getInputStream();
			File lf = new File(localFolder);
			if(!lf.exists()) {
                boolean b = lf.mkdirs();
                if(!b)ALog.w("create " + lf + " failed!");
            }
            lf = new File(localFolder + "/" + fileName);
	    	OutputStream fos = getOutputStream(lf);
	    	int readed;
	    	byte[] buffer = new byte[CACHE_SIZE];
	    	while((readed = is.read(buffer)) > 0){
	    		fos.write(buffer, 0, readed);
	    		progress += readed;
	    		if(hasListener) {
                    progressLis.onProgress(progress, length);
                }
	    	}
	    	if(hasListener)progressLis.onResponse(resCode);
	    	fos.flush();
	    	is.close();
	    	fos.close();
            if(hasListener)progressLis.onFinish();
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public static void downloadFileFromUrl(String url, String localFolder, String fileName, String[] headers){
		byte[] data;
		OutputStream fos = null;
		try {
			URLConnection urlConn = new URL(url).openConnection();
			urlConn.setConnectTimeout(60 * 1000);
            if(headers != null && headers.length > 0){

                for(String s : headers){
                    String h[] = s.split(";;");
                    urlConn.setRequestProperty(h[0], h[1]);
                }
            }
            urlConn.connect();
			data = HttpUtilsAndroid.getResponseEntityBytes(urlConn, null);
			File f = new File(localFolder);
			if(!f.exists()) {
                boolean b = f.mkdirs();
                if(!b)ALog.d(TAG, "create folder " + f.getAbsolutePath() + " failed");
            }
			fos = getOutputStream(f.getAbsoluteFile() + "/" + fileName);
			fos.write(data);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

    public static void downloadFileFromUrlByDownloadThread(String url, String localFolder, String fileName, DownloadThread thread){
        byte[] data;
        OutputStream fos = null;
        try {
            if(new File(localFolder + "/" + fileName).exists()){
                ALog.d(TAG, "file exists ? should ignore ???");
                return;
            }
            URLConnection urlConn = new URL(url).openConnection();
            urlConn.setConnectTimeout(60 * 1000);
            urlConn.connect();
            if(thread != null)thread.onRequestStart();
            data = HttpUtilsAndroid.getResponseEntityBytes(urlConn, thread);
            File f = new File(localFolder);
            if(!f.exists()) {
                boolean b = f.mkdirs();
                if(!b)ALog.d(TAG, "create folder " + f.getAbsolutePath() + " failed");
            }
            fos = getOutputStream(f.getAbsoluteFile() + "/" + fileName);
            fos.write(data);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	public static void downloadFileFromUrlWidthHeaders(String url, String localPath, String fileName, String header, String headerValue){
		byte[] data;
		OutputStream fos = null;
		try {
            URLConnection urlConn = new URL(url).openConnection();
            urlConn.setConnectTimeout(60 * 1000);
            urlConn.connect();
			urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7");
			data = HttpUtilsAndroid.getResponseEntityBytes(urlConn, null);
			File f = new File(localPath);
			if(!f.exists())
				f.mkdirs();
			fos = getOutputStream(f.getAbsoluteFile() + "/" + fileName);
			fos.write(data);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * handle the response entity.
	 * we can get the entity from response. and then convert to byte[].
	 * if you want to see the content by string : String s = new String(byte[]);
	 * if you want to write the content to a file: OutputStream.write(byte[], started, end);
	 * @param urlConn URLConnection from request
	 * @return byte[]
	 */
	public static byte[] getResponseEntityBytes(URLConnection urlConn, HttpProgressListener cb){
		long contentLength = urlConn.getContentLength();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = null;
		byte[] buffer = new byte[CACHE_SIZE];
		long currentLength = 0;
		try{
            if(cb != null)cb.onResponse(((HttpURLConnection)urlConn).getResponseCode());

            is = urlConn.getInputStream();
			int i;
			for(;;){
                if(cb != null && cb.canceled()){
                    ALog.w("request canceled !!!");
                    break;
                }
                i = is.read(buffer);
                //ALog.d(TAG, "getResponseEntityBytes fall in loop read " + i + "," + is.available() + ", " + contentLength);
                if(i == -1){
					if(contentLength > 0 && contentLength > currentLength){
						Thread.sleep(10);
					}else if(contentLength <= 0){
                        //OK, we could NOT found content from InputStream, so, just skip
						break;
					}
				}else{
					bos.write(buffer, 0, i);
					currentLength += i >= 0 ? i:0;
					if(currentLength == contentLength){
						if(cb != null)cb.onProgress(currentLength, contentLength);
						break;
					}
				}
				if(cb != null)cb.onProgress(currentLength, contentLength);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
                if(cb != null)cb.onFinish();
				bos.flush();
				bos.close();
				if(is != null)is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bos.toByteArray();
	}
	
	/**
	 * always we need to know the response is 200 OK ?
	 * we can check this and get the reason cause the error.
	 * @param response HttpResponse from request
	 * @return response code

	public static int getResponseStatusCode(HttpResponse response){
		return response.getStatusLine().getStatusCode();
	}
		 */
	
	/**
	 * send message to the given handler.
	 * @param h Handler, handler you want to send, maybe null, will return
	 * @param what Integer, message type you want to send
	 * @param result String, something you want to attached to send to Handler.
	 * @param delay Integer, delay time in millionsecond.
	 */
	public static void sendMessageToHandler(Handler h, int what, String result, int delay){
		if(h == null)return;
		Message msg = h.obtainMessage();
		msg.what = what;
		if(result != null)msg.obj = result;
		if(delay > 0) h.sendMessageDelayed(msg, delay);
		else h.sendMessage(msg);
	}
	
	
	/**
	 * get current system avaliable network type, maybe WIFI(ConnectivityManager.TYPE_WIFI), maybe MOBILE
	 * return -1 when no network
	 * @param context context to getSystemService
	 * @return type of network available
	 */
	public static int checkNetworkEnv(Context context){
		int ava = -1;
		ConnectivityManager cm  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressLint("MissingPermission")
        NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni != null){
			ava = ni.getType();
		}
		return ava;
	}
	
	/**
	 * check type is WIFI or not
	 * @param type type you get
	 * @return true when type is wifi
	 */
	public static boolean isWIFI(int type){
		return ConnectivityManager.TYPE_WIFI == type;
	}

	/**
	 * return path for image saved
	 * eg. url = http://www.baidu.com/pic/logo.png
	 * localpath = /mnt/sdcard/baidu/
	 * return /mnt/sdcard/baidu/pic
	 * @param url request url
	 * @param localpath local folder path
	 * @return String of local path (abs)
	 */
	public static String generateSavepath(String url, String localpath){
		String url1 = url.substring(url.indexOf("//") + 2);
		String[] folders = url1.split("/");
		StringBuilder sb = new StringBuilder(localpath);
		for(int i=1; i<folders.length-1; i++){
			sb.append("/").append(folders[i]);
		}
		
		return sb.toString();
	}
	
	/**
	 * make support mulitpart upload
	 * @param url request url
	 * @param remotePath remote path
	 * @param localFile local file.
	 */
	public static void uploadByMulitpart(String url, String remotePath, String localFile, HttpProgressListener progressListener){
		//boundary=----------ThIs_Is_tHe_bouNdaRY_$
		int resultCode = 404;
		try {
            URLConnection urlConn = new URL(url).openConnection();
            urlConn.setConnectTimeout(60 * 1000);
            urlConn.setRequestProperty("Accept-Encoding", "identity");
            urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=----------ThIs_Is_tHe_bouNdaRY_$");
            urlConn.setRequestProperty("User-Agent", "klive");
            urlConn.connect();
            if(urlConn instanceof HttpURLConnection){
                ((HttpURLConnection) urlConn).setRequestMethod("POST");
            }

			FilePart p = new FilePart(FileUtils.pickFileName(localFile), new File(localFile));
			p.setTransferEncoding("binary");
			p.setContentType("application/octet-stream");
			ProgressMultipartEntity reqEntity = new ProgressMultipartEntity(new Part[]{p}, new File(localFile).length(), progressListener);
            if(urlConn instanceof HttpURLConnection) {
                reqEntity.writeTo(urlConn.getOutputStream());
            }
            if(null != progressListener)progressListener.onRequestStart();
			String resStr = new String(HttpUtilsAndroid.getResponseEntityBytes(urlConn, null));
			ALog.d("uploadByMulitpart", "resStr = " + resStr);
			resultCode = ((HttpURLConnection)urlConn).getResponseCode();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != progressListener)progressListener.onResponse(resultCode);
		}
		
	}
	
	public static interface HttpProgressListener{
        boolean canceled();
        void onRequestStart();
		void onResponse(int code);
		void onProgress(long progress, long size);
        void onFinish();
	}

	public static byte[] isUrlExists(String url, String[] params, int requestLen){
        try {
            URLConnection urlConn = new URL(url).openConnection();
            if(params != null && params.length > 0){
                for(String s : params){
                    String ss[] = s.split(";;");
                    urlConn.setRequestProperty(ss[0], ss[1]);
                }
            }
            long len =  urlConn.getInputStream().available();
            byte[] read = new byte[Math.min(requestLen, (int)len)];
            urlConn.getInputStream().read(read);
            urlConn.getInputStream().close();
            return read;

        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static String INTERNAL_SD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static OutputStream getOutputStream(String f) throws FileNotFoundException {
    	//ALog.i(TAG, "getOutputStream(" + f + ")");
	    if(f.startsWith(INTERNAL_SD)){
	        return new FileOutputStream(f);
        }else{
	        return TFCardUtils.getFileOutputStream(new File(f));
        }
    }
    public static OutputStream getOutputStream(File f) throws FileNotFoundException {
		//ALog.i(TAG, "getOutputStream(" + f + ")");
        if(f.getAbsolutePath().startsWith(INTERNAL_SD)){
            return new FileOutputStream(f);
        }else{
            return TFCardUtils.getFileOutputStream(f);
        }
    }
}
