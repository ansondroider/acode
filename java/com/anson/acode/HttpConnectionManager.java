package com.anson.acode;


import java.net.HttpURLConnection;
import java.util.ArrayList;

public class HttpConnectionManager {

    static ArrayList<HttpURLConnection> requests;

	public static HttpConnectionManager mgr = null;
	public static HttpConnectionManager getInstance(){
		if(mgr == null) {
            mgr = new HttpConnectionManager();
        }
		return mgr;
	}
	
	public HttpConnectionManager(){
        requests = new ArrayList<HttpURLConnection>();
    }

    public void addRequest(HttpURLConnection req){
        requests.add(req);
    }

    public void forceStopRequest(HttpURLConnection req){
        req.disconnect();
        onRequestComplete(req);
    }

    public void onRequestComplete(HttpURLConnection req){
        requests.remove(req);
    }

    public void onExit(){
        for(HttpURLConnection r : requests){
            r.disconnect();
        }
    }
}
