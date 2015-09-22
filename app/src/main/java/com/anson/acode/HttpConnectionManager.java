package com.anson.acode;

import org.apache.http.client.methods.HttpRequestBase;

import java.util.ArrayList;

public class HttpConnectionManager {

    static ArrayList<HttpRequestBase> requests;

	public static HttpConnectionManager mgr = null;
	public static HttpConnectionManager getInstance(){
		if(mgr == null) {
            mgr = new HttpConnectionManager();
        }
		return mgr;
	}
	
	public HttpConnectionManager(){
        requests = new ArrayList<HttpRequestBase>();
    }

    public void addRequest(HttpRequestBase req){
        requests.add(req);
    }

    public void forceStopRequest(HttpRequestBase req){
        req.abort();
        onRequestComplete(req);
    }

    public void onRequestComplete(HttpRequestBase req){
        requests.remove(req);
    }

    public void onExit(){
        for(HttpRequestBase r : requests){
            r.abort();
        }
    }
}
