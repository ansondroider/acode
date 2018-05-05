package com.anson.acode.download;

import com.anson.acode.ALog;
import com.anson.acode.HttpUtilsAndroid;


/**
 * Created by anson on 16-6-3.
 * Thread use to down task.
 * 1. can cancel by client
 * 2. can get progress.
 */
public abstract class DownloadThread extends Thread implements HttpUtilsAndroid.HttpProgressListener{
    String TAG = "DownloadThread";
    boolean canceled = false;
    String progress = "0/0";
    int totalFileSize = 0;
    int completed = 0;
    long curPro, curTotal;
    String url = "";
    DownloadThreadFactory factory;
    HttpUtilsAndroid.HttpProgressListener progressListener;
    public DownloadThread(Task task){
        factory = DownloadThreadFactory.getFactory();
        setName(task.getTaskId());
        totalFileSize = task.getUrls().size();
        factory.addThread(task.getTaskId(), this);
    }


    public DownloadThread(String url, HttpUtilsAndroid.HttpProgressListener proListener){
        this.url = url;
        setProgressListener(proListener);
        factory = DownloadThreadFactory.getFactory();
        totalFileSize = 1;
        setName(url);
        factory.addThread(getName(), this);
    }

    public void setProgressListener(HttpUtilsAndroid.HttpProgressListener proListener){
        this.progressListener = proListener;
        //when add listener, notify progress immediate
        if(progressListener != null){
            progressListener.onProgress(curPro, curTotal);
        }
    }
    @Override
    public final void run() {
        execute();
        factory.deleteThread(getName());
    }

    abstract public void execute();

    public void completeOne(){
        completed ++;
    }

    public void cancel(){
        canceled = true;
    }

    void updateProgress(long progress, long total){
        this.progress = progress + "/" + total;
    }

    public String getProgress(){
        return progress;
    }

    @Override
    public boolean canceled() {
        if(progressListener != null){
            return progressListener.canceled();
        }
        return canceled;
    }

    @Override
    public void onRequestStart() {
        if(progressListener != null){
            progressListener.onRequestStart();
        }
    }

    @Override
    public void onResponse(int code) {
        ALog.d(TAG, "onResponse " + code);
        if(progressListener != null){
            progressListener.onResponse(code);
        }
    }

    @Override
    public void onFinish() {
        if(progressListener != null){
            progressListener.onFinish();
        }
    }

    @Override
    public void onProgress(long progress, long size) {
        if(progressListener != null){
            progressListener.onProgress(progress, size);
        }
        curPro = progress;
        curTotal = size;
        updateProgress(progress, size);
    }
}
