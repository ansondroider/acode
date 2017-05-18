package com.anson.acode.download;

import java.util.Hashtable;

/**
 * Created by anson on 16-6-7.
 * class save all download thread.
 */
public class DownloadThreadFactory {
    private static DownloadThreadFactory factory = null;
    public static DownloadThreadFactory getFactory(){
        if(factory == null){
            factory = new DownloadThreadFactory();
        }

        return factory;
    }

    Hashtable<String, DownloadThread> threads;
    private DownloadThreadFactory(){
        threads = new Hashtable<String, DownloadThread>();
    }

    protected void deleteThread(String id){
        if(threads.containsKey(id)){
            threads.remove(id);
        }
    }

    /**
     * get download exists.
     * @param id if is a task download, just a UUID of taskID. else if is a single file, just the url....
     * @return DownloadThread of Task or Single file.
     */
    public DownloadThread findThread(String id){
        if(threads.containsKey(id)){
            DownloadThread dt = threads.get(id);
            if(!dt.canceled()){
                return threads.get(id);
            }
        }

        return null;
    }

    protected void addThread(String id, DownloadThread thread){
        threads.put(id, thread);
    }
}
