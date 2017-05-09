package com.anson.acode.view;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.text.GetChars;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.anson.acode.ALog;

public class NavigateViewEndless extends ViewGroup {
	final String tag = "NavigateViewEndless";
	
	GestureDetector detector;

	int origin = 0;
	int target = 0;
	int mDis = 0;
	int guessDistance = 0;
	int offsetX = 0;
	
	long startTime = 0;
	long endTime = 0;
	
	Page[] pages;
	private final int DURATION = 500;
	private final int MSG_MOVESBS = 30;
	private int currentScreen = 0;
	private int mWidth = 0;
	private int mHeight = 0;
	private boolean inited = false;
	private int maxPage = 1;
	
	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case MSG_MOVESBS:
				moveStepByStep();
				break;
			}
		};
	};
		
	/** Constructions ****/
	public NavigateViewEndless(Context context) {
		super(context);
		init();
	}
	public NavigateViewEndless(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public NavigateViewEndless(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init(){
		detector = new GestureDetector(new OnGestureListener() {
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					//log("onSingleTapUp");
					return false;
				}
				@Override
				public void onShowPress(MotionEvent e) {
					//log("onShowPress");
				}
				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
						float distanceY) {
					//log("onScroll, disX = " + distanceX + ", disY = " +distanceY);
					if(Math.abs(distanceX) < Math.abs(distanceY))return false;
					/*if(Math.abs(distanceX) < Math.abs(distanceY)) {
						e1.setAction(MotionEvent.ACTION_DOWN);
						dispatchTouchEvent(e1);
						return false;
					}*/
					updateOffset(offsetX-distanceX);
					requestLayout();
					return true;
					
				}
				
				@Override
				public void onLongPress(MotionEvent e) {					
				}
				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						float velocityY) {
					//log("onFling" + ", velocityX = " + velocityX + ", velocityY = " + velocityY);
					guessDistance = (int)velocityX/8;
					onTouchRelease();
					return false;
				}
				@Override
				public boolean onDown(MotionEvent e) {
					//log("onDown");
					return false;
				}
			});
	}
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		pages = new Page[getChildCount()];
		for(int i=0; i< pages.length; i++){
			Page p = new Page(getChildAt(i), i, i * mWidth, (1 + i) * mWidth);
			pages[i] = p;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
		int count = getChildCount();
		for(int i=0; i<count; i++){
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		if(!inited && mWidth > 0 && mHeight > 0){
			inited = true;
			for(int i=0; i< pages.length; i++){
				Page p = pages[i];
				p.left = p.idx * mWidth;
				p.right = p.left + mWidth;
			}
		}
	}
	
	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		for(int i=0; i<getChildCount(); i++){
			View v = getChildAt(i);
			Page p = pages[i];
			v.layout(p.left, t, p.right, b);
		}
	}
	
	/** touch Event handle ***/
	boolean handledByChildren = false;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		boolean spend = false;
		switch(ev.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_POINTER_DOWN:
			handledByChildren = true;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			handledByChildren = false;
			break;
		case MotionEvent.ACTION_DOWN:
			handledByChildren = false;
			guessDistance = 0;
			//log("onInterceptTouchEvent.ACTION_DOWN, " + spend);
			break;
		case MotionEvent.ACTION_MOVE:
			//log("onInterceptTouchEvent.ACTION_MOVE, " + spend);
			//detector.onTouchEvent(ev);
			break;
		case MotionEvent.ACTION_UP:
			//log("onInterceptTouchEvent.ACTION_UP, " + spend);
			//onTouchRelease();
			break;
		case MotionEvent.ACTION_CANCEL:
			//log("onInterceptTouchEvent.ACTION_CANCEL, " + spend);
			//onTouchRelease();
			break;
		}
		if(!handledByChildren)spend = detector.onTouchEvent(ev);
		
		return spend;
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		boolean spend = false;
		spend = detector.onTouchEvent(event);
		switch(event.getAction()){
		case MotionEvent.ACTION_MASK | MotionEvent.ACTION_DOWN:
			ALog.alog("onTouchEvent ACTION_MASK");
			break;
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
			onTouchRelease();
			break;
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			onTouchRelease();
		}
		return spend;
	}
	
	void onTouchRelease(){
		ALog.alog("left = " + pages[0].left + ", guessDistance = " + guessDistance);
		int nidx = 0;
		int defDis = Integer.MAX_VALUE;
		for(int i=0; i<pages.length;i++){
			int left = pages[i].left + guessDistance;
			int abs = Math.abs(left);
			if(defDis > abs){
				defDis = abs;
				nidx = i;
			}
		}
		startTranslation(nidx, DURATION);
	}
	

	void startTranslation(int idx, int time){
		startTime = System.currentTimeMillis();
		endTime = startTime + time;
		origin = pages[idx].left - pages[idx].idx * mWidth;
		target = -pages[idx].idx * mWidth;
		mDis = target - origin;
		h.sendEmptyMessage(MSG_MOVESBS);
	}
	
	boolean moveStepByStep(){
		long cTime = System.currentTimeMillis();
		if(cTime < endTime){
			long time = cTime - startTime;
			updateOffset(origin + (int)(mDis * time/DURATION));
			requestLayout();
			h.sendEmptyMessage(MSG_MOVESBS);
			return false;
		}else{
			updateOffset(target);
			requestLayout();
			return true;
		}
	}
	
	public void moveToNext(){
		int nidx = 0;
		int defDis = Integer.MAX_VALUE;
		/*for(int i=0; i<viewLeft.length;i++){
			int left = viewLeft[i] - mWidth;
			int abs = Math.abs(left);
			if(defDis > abs){
				defDis = abs;
				nidx = i;
			}
		}
		startTranslation(-viewLeft[nidx], DURATION);*/
	}
	
	public boolean moveToPre(){
		/*`if(viewLeft[0] > -10)return false;
		int nidx = 0;
		int defDis = Integer.MAX_VALUE;
		for(int i=0; i<viewLeft.length;i++){
			int left = viewLeft[i] + mWidth;
			int abs = Math.abs(left);
			if(defDis > abs){
				defDis = abs;
				nidx = i;
			}
		}
		startTranslation(-viewLeft[nidx], DURATION);*/
		return true;
	}
	
	void updateOffset(float offset){
		offsetX = (int)offset;
		if(listeners != null && listeners.size() > 0){
			for(ScreenSwitchListener l:listeners){
				l.onScroll(offsetX);
			}
		}
		
		//normal
		for(int i=0; i<getChildCount(); i++){
			int os = offsetX + i*mWidth;
			int l = getLeftFromOffset(os);
			pages[i].updateLeft(l);
			pages[i].updateIndex(getPageIdxFromOffset(offsetX, i));
		}
		
		//move to right edge.
		if(offsetX > 0){
			pages[0].updateLeft(offsetX < mWidth >> 1 ?offsetX:mWidth >> 1 );
			pages[1].updateLeft(mWidth);
		}
		
		//move to left edge.
		int maxOffset = -maxPage * mWidth + (mWidth >> 1);
		if(maxOffset < offsetX && offsetX < maxOffset + (mWidth >> 1)){
			if(maxPage % 2 == 0){
				pages[0].updateLeft(mWidth);
			}else{
				pages[1].updateLeft(mWidth);
			}
		}else if(offsetX <= maxOffset){
			
			if(maxPage % 2 == 0){
				pages[0].updateLeft(mWidth);
				pages[1].updateLeft(getLeftFromOffset(maxOffset + mWidth));
			}else{
				pages[0].updateLeft(getLeftFromOffset(maxOffset));
				pages[1].updateLeft(mWidth);
			}
		}
		
		requestLayout();
	}
	int getPageIdxFromOffset(int offset, int vIdx){
		int idx = 0;
		if(vIdx == 0){
			int pidx = (Math.abs(offset) + mWidth)/(mWidth << 1);
			if(offset >= 0){
				idx = -2 * pidx;
			}else{
				idx = 2 * (pidx);
			}
			idx = idx < 0? 0:idx;
		}if(vIdx == 1){
			int pidx = offset /(mWidth << 1);
			if(offset > 0){
				idx = -1 - (pidx << 1);
			}else{
				idx = 1 - (pidx << 1);
			}
			idx = idx < 0?1:idx;
		}
		return idx;
	}
	int getLeftFromOffset(int offset){
		int left = 0;
		int os = offset % (mWidth<<1);
		if(os >= 0){
			if(os < mWidth << 1){
				left = os;
			}else{
				left = os - (mWidth << 1);
			}
		}else{
			if(os > -mWidth){
				left = os;
			}else{
				left = os + (mWidth << 1);
			}
		}
		return left;
	}
	
	private void updateScreen(int screen){
		for(ScreenSwitchListener l:listeners){
			l.switchToScreen(screen);
		}
	}
	
	class Page {
		private View v; //current show view;
		private int idx;//index in pictures count;
		private int left;//left in window;
		private int right;//right in window;
		public Page(View view, int index, int left, int right){
			this.v = view;
			this.idx = index;
			this.left = left;
			this.right = right;
		}
		public void updateLeft(int l){
			this.left = l;
			this.right = l + mWidth;
		}
		
		public void updateIndex(int index){
/*			if(idx != index){
				if(v instanceof XImageView){
					((XImageView)v).onIndexChanged(index);
				}
			}
*/
			idx = index;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			StringBuffer sb = new StringBuffer();
			sb.append("Page[").append(idx).append("]: ");
			sb.append("idx_").append(idx).append(", left_").append(left);
			return sb.toString();
		}
	}
	
	
	
	/** OUTER METHODS */
	public void setMaxPage(int max){
		this.maxPage = max;
	}
	public void setScreen(int i){
		i-= 1;
		i = i < 0 ? 0:i;
		updateOffset(-i * mWidth);
	}
	
/*	public XImageView getCurrentView(){
		int idx = -offsetX /mWidth;
		for(int i=0; i < pages.length; i++){
			if(pages[i].idx == idx){
				return (XImageView)pages[i].v;
			}
		}
		
		return null;
	}
*/	
	/**
	 * if you want to know the pages scrolling or Page index is changed, 
	 * add a listener to NavigateView.
	 */
	ArrayList<ScreenSwitchListener> listeners = new ArrayList<ScreenSwitchListener>();
	public void addSwitchListener(ScreenSwitchListener lis){
		if(!listeners.contains(lis))listeners.add(lis);
	}
	public void removeSwitchListener(ScreenSwitchListener lis){
		if(listeners.contains(lis))listeners.remove(lis);
	}
	
	public interface ScreenSwitchListener{
		/**
		 * screen will change from 0 to X
		 * this methid is called when screen index changed.
		 * @param screen
		 */
		void switchToScreen(int screen);
		
		/**
		 * the offset is the value of all children layout offset in X
		 * @param offset
		 */
		void onScroll(int offset);
	}
	
	
}
