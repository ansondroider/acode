package com.anson.acode.view;
import com.anson.acode.ALog;
import com.anson.acode.FileUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

public class GifView  extends View {
	Movie mMovie = null;
	long mMovieStart = 0;
	int timeLength = 0;
	int id = 0;
	int mWidth = 0;
	int mHeight = 0;
	int gifWidth = 0;
	int gifHeight = 0;
	
	onProgressChangeListener mOnProgressChangeListener;
	public GifView(Context context) {
		super(context);
	}
	
	public GifView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	public void setOnProgressChangeListener(onProgressChangeListener listener){
		mOnProgressChangeListener = listener;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		if(w > 0 && h > 0){
			mWidth = w;
			mHeight = h;
		}
	}
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		super.onVisibilityChanged(changedView, visibility);
		if(visibility != View.VISIBLE){
			stop();
		}
	}
	
	public void playGif(String file){
		final byte[] gifdata = FileUtils.getBytesFromLocalFile(file);
		//new Handler().postDelayed(new Runnable(){
		//	public void run(){if(gifdata != null){
		ALog.alog("length = " + gifdata.length);
		playGif(gifdata, 0, gifdata.length);
	}
	public void playGif(int id){
		mMovie = Movie.decodeStream(getResources().openRawResource(id));
		gifWidth = mMovie.width();
		gifHeight = mMovie.height();
		invalidate();
	}
	public void playGif(byte[] data, int offset, int length){
		stop();
		mMovie = Movie.decodeByteArray(data, offset, length);
		gifWidth = mMovie.width();
		gifHeight = mMovie.height();
		ALog.alog("gifWidth=" + gifWidth + ", gifHeight=" + gifHeight + ", time = " + timeLength);
		invalidate();
	}
	public void onDraw(Canvas canvas) {
		//ALog.alog("onDraw()");
		if(mMovie == null){
			ALog.alog("onDraw() mMovie == null ");
			return;
		}
		timeLength = mMovie.duration();
		//ALog.alog("onDraw() timeLength = " + timeLength);
		timeLength = timeLength < 1 ? 1:timeLength;
		long now = android.os.SystemClock.uptimeMillis();
		
		if (mMovieStart == 0) { // first time
			mMovieStart = now;
		}
		if (mMovie != null) {

			int relTime = (int) ((now - mMovieStart) % timeLength);				
			mMovie.setTime(relTime);
			mMovie.draw(canvas, (mWidth - gifWidth) >> 1, (mHeight - gifHeight) >> 1);
			invalidate();
			if(mOnProgressChangeListener != null){
				mOnProgressChangeListener.onProgressChange(relTime, timeLength);
			}
		}
	}
	
	public void stop(){
		if(mMovie != null){
			//mMovie.setTime(Integer.MAX_VALUE);
			//mMovie = null;
		}
	}
	
	public static interface onProgressChangeListener {
		public void onProgressChange(int progress, int total);
	}
	

}