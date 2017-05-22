package com.anson.acode;

/**
 * Created by anson on 17-4-2.
 * bitmap cache.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BitmapCache {
    private final static String TAG = "BitmapCache";
    private boolean D = false;
    public static final int MSG_LOAD_BITMAP_SUCCESS = 0xff0;
    public static final int MSG_LOAD_BITMAP_FAILED = 0xff1;
    private Hashtable<String, WeakReference<Bitmap>> bitmaps = new Hashtable<String, WeakReference<Bitmap>>();
//private Hashtable<String, Bitmap> bitmaps = new Hashtable<String, Bitmap>();

    private BitmapCache(){
        loadThread.start();
    }
    public static final String thumbPath = Environment.getExternalStorageDirectory() + "/.thumb/";
    private static BitmapCache instance;
    public static BitmapCache getInstance(){
        if(instance == null){
            instance = new BitmapCache();
            File f = new File(thumbPath);
            if(!f.exists()){
                ALog.d(TAG, "create thumb folder " + f.mkdirs());
            }
        }
        return instance;
    }

    /**
     * check the message is for me
     * @param clz class who call BitmapCache
     * @param key bitmap key.
     * @return true when message is for me.
     */
    public static boolean forMe(Class clz, String key){
        return key.startsWith(clz.getSimpleName() + ";;");
    }

    /**
     * get bitmap from local array.
     * @param key key for bitmap.
     * @return bitmap, null when could NOT found
     */
    public Bitmap getBitmap(String key){
        if(bitmaps.get(key) != null){
            WeakReference<Bitmap> wb = bitmaps.get(key);
            Bitmap bm = wb.get();
            if(bm != null && !bm.isRecycled()){
                return bm;
            }else{
                if(bm == null) {
                    ALog.w(TAG, key + " bm is NULL");
                }else{
                    ALog.w(TAG, key + " bm is Recycled");
                }
            }
            ///return bitmaps.get(key);
        }

        return null;
    }

    /**
     * load bitmap, and enqueue a task when bitmap could NOT found
     * @param key key genterated by generateKey.
     * @param localFolder where you want to save file download from remote.
     * @param isVideo is a video file
     * @param deleteOld delete old thumbnail file.
     * @param reflectGap should be > 0 when you want to create a reflect bitmap.
     * @param height height you want.
     * @param h handler for send message to caller.
     */
    public void getBimapAndLoadIfNotExist(final String key,
                                          final String localFolder,//for network download.
                                          final boolean isVideo,
                                          final boolean deleteOld,
                                          final int reflectGap,
                                          final int height,
                                          final Handler h){
        getBimapAndLoadIfNotExist(key, localFolder, isVideo, deleteOld, reflectGap, height, h, true);
    }

    /**
     * load bitmap, and enqueue a task when bitmap could NOT found
     * @param key key genterated by generateKey.
     * @param localFolder where you want to save file download from remote.
     * @param isVideo is a video file
     * @param deleteOld delete old thumbnail file.
     * @param reflectGap should be > 0 when you want to create a reflect bitmap.
     * @param height height you want.
     * @param h handler for send message to caller.
     * @param priority load quick
     */
    public void getBimapAndLoadIfNotExist(final String key,
                                          final String localFolder,//for network download.
                                          final boolean isVideo,
                                          final boolean deleteOld,
                                          final int reflectGap,
                                          final int height,
                                          final Handler h,
                                          final boolean priority){
        Bitmap bm = getBitmap(key);
        if(bm != null){
            Message msg = new Message();
            msg.what = MSG_LOAD_BITMAP_SUCCESS;
            msg.obj = key;
            if(h != null)h.sendMessage(msg);
            return;
        }
        new Thread(){
            @Override
            public void run() {
                loadThread.addTask(key, localFolder, isVideo, deleteOld, height, reflectGap, h, MSG_LOAD_BITMAP_SUCCESS, MSG_LOAD_BITMAP_FAILED, priority);
            }
        }.start();

    }

    /**
     * generate key.
     * @param parent class of caller.
     * @param path path of file, or remote url.
     * @return String of key.
     */
    public static String generateBitmapKey(Class parent, String path){
        return parent.getSimpleName() + ";;" + path;
    }

    /**
     * release all bitmap cached.
     */
    public void release(){
        for (String k : bitmaps.keySet()) {
            recycleBitmap(k);
        }

        bitmaps.clear();
        loadThread.release();
    }

    private int cacheCount = 16;
    private int cacheIdx = 0;
    private Bitmap[] bmReference = new Bitmap[cacheCount];
    /** private methods **/
    private void putBitmap(String key, Bitmap bm){
        //ALog.d(TAG, "putBitmap(" + key + "-" + bm.getWidth() + "x" + bm.getHeight() + ")");
        bmReference[cacheIdx] = bm;
        cacheIdx ++;
        if(cacheIdx >= cacheCount)cacheIdx = 0;
        WeakReference<Bitmap> wb = new WeakReference<Bitmap>(bm);
        bitmaps.put(key, wb);
        /*bitmaps.put(key, bm);*/
    }


    private void recycleBitmap(String key){
        for(int i = 0; i < cacheCount; i ++){
            bmReference[i] = null;
        }
        Bitmap bm  = getBitmap(key);
        if(bm != null){
            bm.recycle();
        }
    }

    private LoadBitmapThread loadThread = new LoadBitmapThread();
    private class LoadBitmapThread extends Thread{
        volatile List<Task> tasks = new ArrayList<Task>();
        final Object lock = new Object();
        void addTask(String key,
                     String localFolder,
                     boolean isVideo, boolean deleteOld,
                     int height, int reflectGap, Handler h, int msgS, int msgF, boolean priority){
            if(execingKey.equals(key)){
                ALog.w(TAG, "addTask(" + key + ") failed, because task is running!!!");
                return;
            }

            synchronized (lock){
                Task et = null;
                for(Task t : tasks){
                    if(t.key.equals(key)){
                        et = t;
                        break;
                    }
                }

                //task already exists, if priority, move task to end.
                if(et != null){
                    //move to end.
                    if(priority) {
                        tasks.remove(et);
                        tasks.add(0, et);
                    }
                }else {
                    Task t = new Task();
                    t.key = key;
                    t.folder = localFolder;
                    t.isVideo = isVideo;
                    t.deleteOld = deleteOld;
                    t.h = new WeakReference<Handler>(h);
                    t.height = height;
                    t.reflecteGap = reflectGap;
                    t.ms = msgS;
                    t.mf = msgF;
                    tasks.add(t);
                    lock.notify();
                }
            }
        }
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                Task t = null;
                synchronized (lock){
                    try {
                        //scan tasks, if no task, wait.
                        if(tasks.size() <= 0){
                            ALog.d(TAG, "__IDLE__");
                            lock.wait();
                        }else{
                            //find a task, dequeue from tasks
                            t = tasks.remove(0);//tasks.size() - 1);//get last one....
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //find task, start it.
                if(t != null){
                    execTask(t);
                }
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            synchronized (lock){
                lock.notifyAll();
            }
        }

        void release(){
            interrupt();
        }

        String execingKey = "";
        void execTask(Task t){
            if(getBitmap(t.key) != null)return;
            Handler h = t.h.get();
            if(h == null){
                ALog.w(TAG, "execTask Handler was null....do NOT get " + t.key);
                return;
            }else{
                if(D)ALog.d(TAG, "execTask loading " + t.key);
            }
            execingKey = t.key;
            String [] sp = t.key.split(";;");
            File f;
            String domain = null;
            //check if network url
            if(sp[1].startsWith("http://") || sp[1].startsWith("https://")){
                //try load download image to local;
                domain = sp[1].replaceAll("http://", "");
                domain = domain.replaceAll("https://", "");
                int idx = domain.lastIndexOf("/");
                if(idx > 0) {
                    domain = domain.substring(0, idx);
                    domain = domain.replaceAll("/", "_");
                }
                f = new File(t.folder + "/" + FileUtils.pickFileName(sp[1]));
                if(!f.exists()){
                    ALog.d(TAG, "go download file ");
                    HttpUtilsAndroid.downloadFileFromUrl(sp[1], t.folder, f.getName());
                }
            }else{
                f = new File(sp[1]);
            }


            //start load from local file.
            if(f.exists()){
                Bitmap bm = null;
                //load from thumb folder.
                File thumb = domain != null ?
                    new File(thumbPath + "/" + f.getName() + "-" + domain)
                    : new File(thumbPath + "/" + f.getName() + "-" + f.lastModified());
                //ALog.d(TAG, "thumb(" + thumb.getName() + "," + thumb.exists());
                if(thumb.exists()){
                    if(t.deleteOld){
                        ALog.d(TAG, "delete old thumb " + thumb.delete());
                    }else {
                        bm = BitmapFactory.decodeFile(thumb.getAbsolutePath());
                    }
                }
                if(bm == null){
                    if (!t.isVideo) {
                        if(domain != null){
                            //decode bitmap from image file.
                            bm = BitmapUtils.decodeBitmapSafe(f.getAbsolutePath(),
                                    0, t.height);
                        }else {
                            bm = BitmapFactory.decodeFile(f.getAbsolutePath());
                        }
                    } else {//is a video, go get a video thumbnail.
                        bm = getVideoThumbnail(f.getAbsolutePath(), 0);
                    }
                }else{
                    putBitmap(t.key, bm);
                    Message m = new Message();
                    m.what = t.ms;
                    m.obj = t.key;
                    h.sendMessage(m);
                    execingKey = "";
                    return;
                }

                //fixed size;
                if (bm != null) {
                    if(t.height > 0 && t.height > bm.getHeight()) {
                        int width = t.height * bm.getWidth() / bm.getHeight();
                        bm = Bitmap.createScaledBitmap(bm, width, t.height, false);
                    }

                    if(t.reflecteGap >= 0){
                        bm = createReflectedImage(bm, t.reflecteGap, t.height / 4);
                    }

                    //save to local
                    if(!thumb.exists()){
                        try {
                            bm.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(thumb));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    putBitmap(t.key, bm);
                    Message m = new Message();
                    m.what = t.ms;
                    m.obj = t.key;
                    h.sendMessage(m);
                }else{
                    putBitmap(t.key, Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));
                    h.sendMessage(h.obtainMessage(t.mf, t.key));
                }
            }else{
                h.sendMessage(h.obtainMessage(t.mf, t.key));
            }
            execingKey = "";
        }
    }

    private class Task{
        String key;
        String folder;
        boolean isVideo;
        boolean deleteOld;
        WeakReference<Handler> h;
        int ms, mf;
        int height;
        int reflecteGap;
    }


    /**
     * create reflected image, with gap and special reflect height.
     * @param src source bitmap
     * @param gap gap between reflect
     * @param reflectHeight hight of reflect image
     * @return reflected bitmap
     */
    private static Bitmap createReflectedImage(Bitmap src, int gap, int reflectHeight) {
        // The gap we want between the reflection and the original image

        int width = src.getWidth();
        int height = src.getHeight();

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(src, 0, height / 2, width,
                height / 2, matrix, false);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + reflectHeight),
                Bitmap.Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(src, 0, 0, null);
        // Draw in the gap
        ///Paint defaultPaint = new Paint();
        ///canvas.drawRect(0, height, width, height + gap, defaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + gap, null);

        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, src.getHeight(), 0,
                bitmapWithReflection.getHeight(), 0x7fffffff, 0x00ffffff,
                Shader.TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + gap, paint);

        return bitmapWithReflection;
    }

    //获取视频缩略图
    private static Bitmap getVideoThumbnail(String filePath,long timeUs) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(timeUs);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
