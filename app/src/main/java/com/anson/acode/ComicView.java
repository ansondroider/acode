package com.anson.acode;

import com.anson.acode.NavigateView.ScreenSwitchListener;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.MeasureSpec;
import android.widget.ImageView.ScaleType;

public class ComicView extends ViewGroup implements XImageView.ClickListener{
	GestureDetector detector;
	private final int DURATION = 500;
	final int MSG_MOVESBS = 30;
	OverscrollView ov;
	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case MSG_MOVESBS:
				moveStepByStep();
				break;
			}
		};
	};
	public ComicView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}
	public ComicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	public ComicView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	int guessDistance = 0;
	void init(){
		detector = new GestureDetector(new OnGestureListener() {
				
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					// TODO Auto-generated method stub
					//log("onSingleTapUp");
					ov.touchRelease();
					return false;
				}
				
				@Override
				public void onShowPress(MotionEvent e) {
					// TODO Auto-generated method stub
					//log("onShowPress");
					
				}
				
				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
						float distanceY) {
					// TODO Auto-generated method stub
					//log("onScroll, disX = " + distanceX + ", disY = " +distanceY);
					if(Math.abs(distanceX) < Math.abs(distanceY))return false;
					/*if(Math.abs(distanceX) < Math.abs(distanceY)) {
						e1.setAction(MotionEvent.ACTION_DOWN);
						dispatchTouchEvent(e1);
						return false;
					}*/
					postOffset((int)distanceX);
					//requestLayout();
					return true;
					
				}
				
				@Override
				public void onLongPress(MotionEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						float velocityY) {
					// TODO Auto-generated method stub
					//log("onFling" + ", velocityX = " + velocityX + ", velocityY = " + velocityY);
					guessDistance = (int)velocityX/8;
					//onTouchRelease();
					
					return false;
				}
				
				@Override
				public boolean onDown(MotionEvent e) {
					// TODO Auto-generated method stub
					//log("onDown");
					return false;
				}
			});
	}
	
	/** touch Event handle ***/
	private int ox = 0;
	private int oy = 0;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		boolean spend = false;
		spend = detector.onTouchEvent(ev);
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			//log("onInterceptTouchEvent.ACTION_DOWN, " + spend);
			break;
		case MotionEvent.ACTION_MOVE:
			//log("onInterceptTouchEvent.ACTION_MOVE, " + spend);
			//detector.onTouchEvent(ev);
			break;
		case MotionEvent.ACTION_UP:
			//log("onInterceptTouchEvent.ACTION_UP, " + spend);
			//onTouchRelease();
			ov.clear();
			break;
		case MotionEvent.ACTION_CANCEL:
			//log("onInterceptTouchEvent.ACTION_CANCEL, " + spend);
			//onTouchRelease();
			break;
		}
		
		return spend;
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		boolean spend = false;
		spend = detector.onTouchEvent(event);
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			//log("onTouchEvent.ACTION_DOWN, " + spend);
			spend = true;
			break;
		case MotionEvent.ACTION_MOVE:
			//log("onTouchEvent.ACTION_MOVE, " + spend);
			detector.onTouchEvent(event);
			break;
		case MotionEvent.ACTION_UP:
			//log("onTouchEvent.ACTION_UP, " + spend);
			//onTouchRelease();
			break;
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			onTouchRelease();
		}
		return spend;
	}
	
	void onTouchRelease(){
		int nidx = 0;
		int defDis = Integer.MAX_VALUE;
		final int count = pages.length;
		final int sx = getScrollX();
		for(int i=0; i<count;i++){
			int left = getChildAt(i).getLeft() - sx + guessDistance;
			//ALog.alog("ComicView", " i = " + i + " = " + left);
			int abs = Math.abs(left);
			if(defDis > abs){
				defDis = abs;
				nidx = i;
			}
		}
		startTranslation(getChildAt(nidx).getLeft()- getScrollX(), DURATION);
		if(sListener != null){
			//ALog.alog("ComicView", "onTouchRelease() sx = " + sx);
			if(sx > 0)sListener.onOverScroll(true);
			if(sx < -mWidth * (count-1)) sListener.onOverScroll(false);
		}
		ov.touchRelease();
	}
	
	long startTime = 0;
	long endTime = 0;
	int origin = 0;
	int target = 0;
	int mDis = 0;
	void startTranslation(int dis, int time){
		startTime = System.currentTimeMillis();
		endTime = startTime + time;
		origin = getScrollX();
		target = getScrollX() + dis;
		mDis = dis;
		h.sendEmptyMessage(MSG_MOVESBS);
	}
	
	boolean moveStepByStep(){
		long cTime = System.currentTimeMillis();
		if(cTime < endTime){
			long time = cTime - startTime;
			scrollTo(origin + (int)(mDis * AnimationHelper.getMoveRate((int)time, DURATION, false)),0);
			//requestLayout();
			h.sendEmptyMessage(MSG_MOVESBS);
			return false;
		}else{
			scrollTo(target, 0);
			origin = getScrollX();
			//requestLayout();
			return true;
		}
	}
	
	public void scrollToScreen(int screen){
		screen = screen < 1 ? 1 : (screen > pages.length ? pages.length:screen);
		int left = getChildAt(screen -1).getLeft() - getScrollX();
		startTranslation(left, DURATION);
	}
	
	public int getCurrentScreen(){
		return Math.abs(getScrollX() /mWidth) + 1;
	}

	private int mWidth = 0;
	private int mHeight = 0;
	
	public interface ScrollListener{
		public void onOffsetChanged(int offset);
		public void onOverScroll(boolean isOverLeft);
	}
	
	ScrollListener sListener;
	public void setScrollListener(ScrollListener lis){
		sListener = lis;
	}
	
	private void postOffset(int offset){
		if(ov != null)ov.clear();
		scrollBy(offset, 0);
	}
	@Override
	public void scrollBy(int x, int y) {
		// TODO Auto-generated method stub
		super.scrollBy(x, y);
		if(null != sListener){
			sListener.onOffsetChanged(getScrollX());
		}
		invalidate();
	}
	@Override
	public void scrollTo(int x, int y) {
		// TODO Auto-generated method stub
		super.scrollTo(x, y);
		if(null != sListener){
			sListener.onOffsetChanged(getScrollX());
		}
		postInvalidate();
	}
	
	XImageView[] pages;
	
	public void setPageCount(int count, OnLongClickListener longListener, int viewMode, OverscrollView ov){
		pages = new XImageView[count];
		for(int i= 0; i<count;i++){
			XImageView xiv = new XImageView(getContext());
			xiv.setOnLongClickListener(longListener);
			xiv.setIndex(i);
			xiv.setViewMode(viewMode);
			xiv.setOverScrollView(ov);
			xiv.setClickListener(this);
			pages[i] = xiv;
			addView(xiv);
		}
		this.ov = ov;
		requestLayoutForce(true);
	}
	
	public void setPage(int page){
		scrollToScreen(page);
	}
	
	boolean inited = false;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
		int count = getChildCount();
		for(int i=0; i<count; i++){
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		
		if(!inited && mWidth > 0 && mHeight > 0){
			inited = true;
			postOffset(0);
		}
	}
	
	/** layout all elements **/
	boolean layouted = false;
	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if(layouted && mHeight == (t-b))return;
		int childcount = getChildCount() -1;
		if(childcount > -1){
			for(int i=0; i<=childcount; i++){
				View v = getChildAt(i);
				//int mw = v.getWidth();
				//int mh = v.getHeight();
				int left = -i * mWidth;
				//v.layout(left, 0, left + mw, mh);
				v.layout(left, 0, left + mWidth, mHeight);
			}
		}
		invalidate();
		layouted = true;
	}
	
	void requestLayoutForce(boolean force){
		if(force){
			layouted = false;
		}
		requestLayout();
	}
	
	@Override
	public void onClick(float x, float y) {
		// TODO Auto-generated method stub
		int halfW = mWidth >> 1;
		int curIdx = -getScrollX() / mWidth + 1;
		int tarIdx = x > halfW ? curIdx-1:curIdx+1;
		ALog.alog("ComicView", "onCLick curIdx = " + curIdx + ", tarIDx = " + tarIdx);
		scrollToScreen(tarIdx);
	}

}
