package com.anson.acode.view;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.anson.acode.ALog;
import com.anson.acode.BitmapUtils;
import com.anson.acode.R;

public class XImageViewAsync extends ImageView {
	private String imgPath;
	private String TAG = "XImageViewAsync";
	private Bitmap mImage = null;
    boolean D = false;
	Handler h = new Handler();
	public XImageViewAsync(Context context) {
		super(context);
	}
	public XImageViewAsync(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public XImageViewAsync(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	boolean needLoad = false;
	String savePath = "";
	String[] header = null;
	String[] headerValue = null;
	String fileName = "";
	boolean save;
	public void updateInfo(String savePath, String []header, String[] headerValue, String fileName, boolean save){
		this.savePath = savePath;
		this.header = header;
		this.headerValue = headerValue;
		this.fileName = fileName;
		this.save = save;
	}
	public void setImagePath(final String url){
		//ALog.alog(TAG, "setImagePath(" + url + "), imagePath = " + imgPath);
		if(url != null && url.length() > 4){
			if(!url.equals(imgPath)){
                setImageResource(R.drawable.thumb);
                stopLoad();
				imgPath = url;
				needLoad = true;
			}
		}else{
            setImageResource(R.drawable.thumb);
            imgPath = url;
			needLoad = false;
		}
	}
	public void startLoadIfNeed(){
		if(needLoad && imgPath != null){
			if(D)ALog.alog(TAG, "startLoadIfNeed");
			loadTask = new Task();
			loadTask.execute(0);
			needLoad = false;
		}
	}
	
	public void forceLoadImage(){
		if(imgPath != null){
			loadTask = new Task();
			loadTask.execute(0);
		}
	}
	public void stopLoad(){
		needLoad = false;
		if(loadTask != null)loadTask.cancel(true);
	}
	public Bitmap getBitmap(){
		return mImage;
	}
	void updateImage(){
		//ALog.alog(TAG, "updateImage");
        if(nxtBm != null){
		    this.setImageBitmap(nxtBm);
		    startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        }else{
            setImageResource(R.drawable.thumb);
        }
	}
	
	public void setImageBitmap(Bitmap bm) {
		Bitmap pre = getBitmap();
		mImage = bm;
		super.setImageBitmap(bm);
		if(pre != null && pre != mImage){
			pre.recycle();
		}
	}
	
	protected void onDraw(android.graphics.Canvas canvas) {
		try{
			super.onDraw(canvas);
		}catch(Exception e){
			ALog.alog(TAG, "error onDraw()");
			updateImage();
		}
	}
	
	Task loadTask = null;
	Bitmap nxtBm = null;
	class Task extends AsyncTask<Integer, String, Integer>{
		
		@Override
		protected Integer doInBackground(Integer... params) {
			if(imgPath != null && imgPath.length() > 4){
				//ALog.alog(TAG, "doInBackground");
				if(imgPath.startsWith("/")){
					//loadLocal
					nxtBm = BitmapUtils.getBitmapFromLocal(imgPath);
				}else{
					File f = new File(savePath + "/" + fileName);
					if(f.exists()){
						nxtBm = BitmapUtils.getBitmapFromLocal(f.getAbsolutePath());
					}else{
						ALog.alog(TAG, "loadOnline");
						nxtBm = BitmapUtils.getBitmapFromUrlWithHeaders(imgPath, savePath, fileName, save, header, headerValue);
					}
				}
			}
			if(nxtBm != null){
				h.post(new Runnable() {
					
					@Override
					public void run() {
						updateImage();
					}
				});
			}
			return 0;
		}
		
	}
	
}
