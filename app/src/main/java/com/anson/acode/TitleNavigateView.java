package com.anson.acode;

import java.util.Currency;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class TitleNavigateView extends ViewGroup implements NavigateView.ScreenSwitchListener {
	boolean inited = false;
	boolean titlesInited = false;
	int mWidth = 0;
	int mHeight = 0;
	int offsetX = 0;
	String TAG = "TitleNavigateView";
	Title[] titles;
	public TitleNavigateView(Context context) {
		super(context);
	}
	public TitleNavigateView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public TitleNavigateView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		//ALog.alog(TAG, "onAttatchedToWindow");
		initTitlesFrom();
	}
	void init() {
		//ALog.alog(TAG, "init()" + offsetX);
		inited = true;
		//initTitlesFrom();
	}
	public void initTitlesFrom(){
		titles = new Title[getChildCount()];
		for(int i=0; i<titles.length; i ++){
			Title t = new Title();
			t.title = "";
			t.pageIdx = i;
			t.offset = 0;
			titles[i] = t;
		}
		
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		//ALog.alog(TAG,"onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
		if(!titlesInited){
			boolean fini = true;
			for(int i=0; i<getChildCount(); i++){
				getChildAt(i).measure(mWidth, heightMeasureSpec);
				int wid = getChildAt(i).getMeasuredWidth();
				if(titles != null)titles[i].updateWidth(wid);
				//ALog.alog(TAG,"onMeasure wid = " + wid);
				fini = fini && wid > 0;
			}
			if(fini && titles != null){
				titlesInited = fini;
				//ALog.alog(TAG, "onMeasure, call updateChildrenOffset");
				updateChildrenOffset(offsetX);
			}
		}
		
		if(!inited && mWidth > 0 && mHeight > 0){
			init();
		}
	}
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		for(int i=0; i<getChildCount(); i++){
			View v = getChildAt(i);
			int w = v.getMeasuredWidth();
			int h = v.getMeasuredHeight();
			
			int left = titles[i].offset;
			v.layout(left, 0, left + w, h);
		}
	}
	
	int getCurrentScreen(){
		return -offsetX/mWidth;
	}
	@Override
	public void switchToScreen(int screen) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onScroll(int offset) {
		// TODO Auto-generated method stub
		//offsetX = (offset >> 1);
		updateChildrenOffset(offset);
		requestLayout();
	}
	
	void updateChildrenOffset(int offset){
		offsetX = offset;
		//if(titles == null)return;
		for(Title t:titles){
			t.updateOffset(offset);
		}
		
	}
	
	class Title{
		int pageIdx = 0;
		String title = "";
		int offset = 0;
		int width = 0;
		int center = 0;
		void updateWidth(int w){
			width = w;
			center = mWidth - w >> 1;
		}
		
		int getRight(){
			return offset + width;
		}
		int getLeft(){
			return offset;
		}
		void updateOffset(int os){
			
			int halfW = mWidth >> 1;
			int ext = os + (pageIdx*mWidth);			
			int mostR = halfW - (pageIdx * mWidth);//w/2  -w/2, -3w/2
			int mostL = mostR - mWidth;
			
			//ALog.alog(TAG, "id = " + pageIdx + ", " + mostL + " < " + os + " < " + mostR + " ext = " + ext);
			if(mostL < os && os < mostR){
				
				offset = halfW + ext - width/2;
				offset = offset > mWidth - width ? mWidth - width :offset;
				offset = offset < 0 ? 0:offset;
				if(pageIdx == 0){
					//if(os > halfW-width/2) offset = mWidth - width;
					//if(os < -halfW + width/2) offset = 0;
				}
			}else if(mostL - mWidth < os && os < mostL){
				if(titles.length > pageIdx + 1){
					Title t = titles[pageIdx + 1];
					int o = t.offset;
					if(o < width){
						offset = o-width;
					}else{
						offset = 0;
					}
				}else{
					offset = 0;
				}
				
			}else if(os > mostR && os < mostR + mWidth){
				//if(pageIdx == 1)ALog.alog(TAG, "updateOffset 001");
				//if(pageIdx == 1)ALog.alog(TAG, "updateOffset2 " + mostR + " < " + os + " < " + (mostR + mWidth));
				if(pageIdx > 0){
					//if(pageIdx == 1)ALog.alog(TAG, "updateOffset 002");
					Title t = titles[pageIdx -1];
					int o = t.offset + t.width;
					if(o > mWidth - width){
						//if(pageIdx == 1)ALog.alog(TAG, "updateOffset 003");
						offset = o;
					}else{
						//if(pageIdx == 1)ALog.alog(TAG, "updateOffset 004");
						offset = mWidth - width;
					}
				}else{
					offset = mWidth - width;
				}
			}else offset = mWidth;
		}
	}
		
	

}
