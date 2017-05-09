package com.anson.acode;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.anson.acode.download.DownloadThread;
import com.anson.acode.multipart.FilePart;
import com.anson.acode.multipart.Part;
import com.anson.acode.multipart.ProgressMultipartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpUtilsAndroid {
	public static final String TAG = "HttpUtilsAndroid";
	static final String defaultProtocal = "http://";
	static final String protocalSpec = "://";
	static final int CONNECTION_TIMEOUT = 60 * 1000;// 1 mins
	static final int CACHE_SIZE = 8 * 1024;
	
	/**
	 * getHttpClient with connection timeout 
	 */
	public static HttpClient getHttpClient(int timeout) {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        //Maybe need ClientConnectionManager
        return new DefaultHttpClient(params);
    }
	
	/** 
	 * getHttpClient by Default;
	 */
	public static HttpClient getHttpClient() {
        return getHttpClient(CONNECTION_TIMEOUT);
    }
	
	
	/**
	 * request.setHeader();
	 * can add request Header to request;
	 * request.setEntry();
	 * can add entry to request, but remember to set the ContentType to request.
	 */
	
	/** get Request */
	public static HttpGet getGetRequest(String url){
		HttpGet request;
		if(url.contains(protocalSpec)){
            request = new HttpGet(url);
        }else{
            //ALog.d(TAG, "getGetRequest " + (defaultProtocal + url));
            request = new HttpGet(defaultProtocal + url);
        }
		return request;
	}
	
	/** post Requst */
	public static HttpPost getPostRequest(String url){
		HttpPost request;
		if(url.contains(protocalSpec)){
			request = new HttpPost(url);
		}else{
			request = new HttpPost(defaultProtocal + url);
		}
		return request;
	}
	
	/** put Request */
	public static HttpPut getPutRequest(String url){
		HttpPut request;
		if(url.contains(protocalSpec)){
			request = new HttpPut(url);
		}else{
			request = new HttpPut(defaultProtocal + url);
		}
		return request;
	}
	
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
				HttpClient client = HttpUtilsAndroid.getHttpClient(60 * 1000);
				try {
					HttpGet req = HttpUtilsAndroid.getGetRequest(url);
					if(params != null && values != null){
						if(params.length == values.length){
							HttpParams hp = new BasicHttpParams();
							for(int i=0; i<params.length; i++){
								hp.setParameter(params[i], values[i]);
							}
							req.setParams(hp);
						}
					}
					byte[] bytes = HttpUtilsAndroid.getResponseEntityBytes(client.execute(req), null);
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
		HttpClient client = HttpUtilsAndroid.getHttpClient(60 * 1000);
		byte[] bytes = null;
		try {
			bytes = HttpUtilsAndroid.getResponseEntityBytes(client.execute(HttpUtilsAndroid.getGetRequest(url)), null);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
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
	 * @param progressLis HttpProgressListener
	 */
	public static void downloadFileFromUrl(String url, String localFolder, String fileName, HttpProgressListener progressLis){

		HttpClient client = getHttpClient();
    	HttpGet req = getGetRequest(url);
    	HttpResponse res;
    	boolean hasListener = progressLis != null;
		try {
            if(null != progressLis)progressLis.onRequestStart(req);
            res = client.execute(req);
	    	int resCode = res.getStatusLine().getStatusCode();
	    	if(200 != resCode){
	    		ALog.e("ERROR in download file response:" + resCode);
		    	if(hasListener)progressLis.onResponse(resCode);
		    	return;
	    	}
	    	long length = res.getEntity().getContentLength();
	    	long progress = 0;
	    	InputStream is = res.getEntity().getContent();
			File lf = new File(localFolder);
			if(!lf.exists()) {
                boolean b = lf.mkdirs();
                if(!b)ALog.w("create " + lf + " failed!");
            }
            lf = new File(localFolder + "/" + fileName);
	    	FileOutputStream fos = new FileOutputStream(lf);
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
	public static void downloadFileFromUrl(String url, String localFolder, String fileName){
		byte[] data;
		FileOutputStream fos = null;
		try {
			HttpClient client = HttpUtilsAndroid.getHttpClient(60 * 1000);
			data = HttpUtilsAndroid.getResponseEntityBytes(client.execute(HttpUtilsAndroid.getGetRequest(url)), null);
			File f = new File(localFolder);
			if(!f.exists()) {
                boolean b = f.mkdirs();
                if(!b)ALog.d(TAG, "create folder " + f.getAbsolutePath() + " failed");
            }
			fos = new FileOutputStream(f.getAbsoluteFile() + "/" + fileName);
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
        FileOutputStream fos = null;
        try {
            if(new File(localFolder + "/" + fileName).exists()){
                ALog.d(TAG, "file exists ? should ignore ???");
                return;
            }
            HttpClient client = HttpUtilsAndroid.getHttpClient(60 * 1000);
            HttpGet req = HttpUtilsAndroid.getGetRequest(url);
            if(thread != null)thread.onRequestStart(req);
            data = HttpUtilsAndroid.getResponseEntityBytes(client.execute(req), thread);
            File f = new File(localFolder);
            if(!f.exists()) {
                boolean b = f.mkdirs();
                if(!b)ALog.d(TAG, "create folder " + f.getAbsolutePath() + " failed");
            }
            fos = new FileOutputStream(f.getAbsoluteFile() + "/" + fileName);
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
		FileOutputStream fos = null;
		try {
			HttpClient client = HttpUtilsAndroid.getHttpClient(60 * 1000);
			HttpGet get = HttpUtilsAndroid.getGetRequest(url);
			get.addHeader(header, headerValue);
			get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7");
			data = HttpUtilsAndroid.getResponseEntityBytes(client.execute(get), null);
			File f = new File(localPath);
			if(!f.exists())
				f.mkdirs();
			fos = new FileOutputStream(f.getAbsoluteFile() + "/" + fileName);
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
	 * if you want to write the content to a file: FileOutputStream.write(byte[], started, end);
	 * @param response HttpResponse from request
	 * @return byte[]
	 */
	public static byte[] getResponseEntityBytes(HttpResponse response, HttpProgressListener cb){
		HttpEntity entity= response.getEntity();
        if(cb != null)cb.onResponse(response.getStatusLine().getStatusCode());
		long contentLength = entity.getContentLength();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = null;
		byte[] buffer = new byte[CACHE_SIZE];
		long currentLength = 0;
		try{
			is = entity.getContent();
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
	 */
	public static int getResponseStatusCode(HttpResponse response){
		return response.getStatusLine().getStatusCode();
	}
	
	
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
		HttpClient client = HttpUtilsAndroid.getHttpClient(60 * 1000);
		HttpPost post = HttpUtilsAndroid.getPostRequest(url);
		HttpParams params = post.getParams();
		params.setParameter("Accept-Encoding", "identity");
		params.setParameter("Content-Type", "multipart/form-data; boundary=----------ThIs_Is_tHe_bouNdaRY_$");
		params.setParameter("User-Agent", "klive");
		//boundary=----------ThIs_Is_tHe_bouNdaRY_$
		int resultCode = 404;
		try {
			FilePart p = new FilePart(FileUtils.pickFileName(localFile), new File(localFile));
			p.setTransferEncoding("binary");
			p.setContentType("application/octet-stream");
			ProgressMultipartEntity reqEntity = new ProgressMultipartEntity(new Part[]{p}, new File(localFile).length(), progressListener);
			post.setEntity(reqEntity);
            if(null != progressListener)progressListener.onRequestStart(post);
            HttpResponse response = client.execute(post);
			String resStr = new String(HttpUtilsAndroid.getResponseEntityBytes(response, null));
			ALog.d("uploadByMulitpart", "resStr = " + resStr);
			resultCode = response.getStatusLine().getStatusCode();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != progressListener)progressListener.onResponse(resultCode);
		}
		
	}
	
	public static interface HttpProgressListener{
        boolean canceled();
        void onRequestStart(HttpRequestBase req);
		void onResponse(int code);
		void onProgress(long progress, long size);
        void onFinish();
	}
}
