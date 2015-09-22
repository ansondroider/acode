package com.anson.acode;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
public class NavigateView extends ViewGroup {
	private final int DURATION = 500;
	private final int MSG_MOVESBS = 30;
	private int currentScreen = 0;
	private boolean inited = false;
	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case MSG_MOVESBS:
				moveStepByStep();
				break;
			}
		};
	};
	
	GestureDetector detector;
	
	/** Constructions ****/
	public NavigateView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	public NavigateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}
	public NavigateView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}
	public void setScreen(int i){
		currentScreen = i;
	}
	int guessDistance = 0;
	void init(){
		detector = new GestureDetector(new OnGestureListener() {
				
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					// TODO Auto-generated method stub
					//log("onSingleTapUp");
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
					updateOffset((int)(offsetXY - distanceX));
					//requestLayout();
					return true;
					
				}
				
				@Override
				public void onLongPress(MotionEvent e) {
					// TODO Auto-generated method stub
					log("onLongPress");
					
				}
				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						float velocityY) {
					// TODO Auto-generated method stub
					//log("onFling" + ", velocityX = " + velocityX + ", velocityY = " + velocityY);
					guessDistance = (int)velocityX/8;
					onTouchRelease();
					
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

	private int mWidth = 0;
	private int mHeight = 0;
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
			updateOffset(currentScreen * -mWidth);
		}
	}
	
	public void postForceLayout(){
		layouted = false;
		requestLayout();
	}
	private int offsetXY = 0;
	/** layout all elements **/
	boolean layouted = false;
	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		if(layouted && mHeight == (t-b))return;
		int childcount = getChildCount() -1;
		if(viewLeft == null || viewLeft.length != childcount){
			viewLeft = new int[childcount + 1];
		}
		if(childcount > -1){
			for(int i=0; i<=childcount; i++){
				View v = getChildAt(i);
				//int mw = v.getWidth();
				//int mh = v.getHeight();
				int left = i * mWidth;
				//v.layout(left, 0, left + mw, mh);
				v.layout(left, 0, (i + 1)*mWidth, mHeight);
				viewLeft[i] = left - getScrollX();
			}
		}
		invalidate();
		layouted = true;
	}
	@Override
	public void scrollTo(int x, int y) {
		// TODO Auto-generated method stub
		super.scrollTo(x, y);
		int childcount = getChildCount() -1;
		if(viewLeft == null || viewLeft.length != childcount){
			viewLeft = new int[childcount + 1];
		}
		if(childcount > -1){
			for(int i=0; i<=childcount; i++){
				int left = i * mWidth + offsetXY;
				viewLeft[i] = left;
			}
		}
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
	
	int viewLeft[];
	void onTouchRelease(){
		int nidx = 0;
		int defDis = Integer.MAX_VALUE;
		for(int i=0; i<viewLeft.length;i++){
			int left = viewLeft[i] + guessDistance;
			int abs = Math.abs(left);
			if(defDis > abs){
				defDis = abs;
				nidx = i;
			}
		}
		//updateOffset(viewLeft[0]);
		//log("offsetXY = " + offsetXY + ", dis = " + viewLeft[nidx]);
		startTranslation(-viewLeft[nidx], DURATION);
	}
	
	long startTime = 0;
	long endTime = 0;
	int origin = 0;
	int target = 0;
	int mDis = 0;
	void startTranslation(int dis, int time){
		startTime = System.currentTimeMillis();
		endTime = startTime + time;
		origin = offsetXY;
		target = offsetXY + dis;
		mDis = dis;
		h.sendEmptyMessage(MSG_MOVESBS);
	}
	
	boolean moveStepByStep(){
		long cTime = System.currentTimeMillis();
		if(cTime < endTime){
			long time = cTime - startTime;
			updateOffset(origin + (int)(mDis * AnimationHelper.getMoveRate((int)time, DURATION, false)));
			//requestLayout();
			h.sendEmptyMessage(MSG_MOVESBS);
			return false;
		}else{
			updateOffset(target);
			origin = offsetXY;
			//requestLayout();
			return true;
		}
	}
	
	public void scrollToScreen(int screen){
		int left0 = viewLeft[0];
		ALog.alog("NavigateView", "scrollToScreen left0 = " + left0);
		int curPage = -left0 / mWidth;
		int dis = -(screen - curPage)*mWidth;
		int nidx = 0;
		int defDis = Integer.MAX_VALUE;
		for(int i=0; i<viewLeft.length;i++){
			int left = viewLeft[i] + dis;
			int abs = Math.abs(left);
			if(defDis > abs){
				defDis = abs;
				nidx = i;
			}
		}
		updateOffset(viewLeft[0]);
		//log("offsetXY = " + offsetXY + ", dis = " + viewLeft[nidx]);
		startTranslation(-viewLeft[nidx], DURATION);
	}
	
	public void moveToNext(){
		int nidx = 0;
		int defDis = Integer.MAX_VALUE;
		for(int i=0; i<viewLeft.length;i++){
			int left = viewLeft[i] - mWidth;
			int abs = Math.abs(left);
			if(defDis > abs){
				defDis = abs;
				nidx = i;
			}
		}
		updateOffset(viewLeft[0]);
		//log("offsetXY = " + offsetXY + ", dis = " + viewLeft[nidx]);
		startTranslation(-viewLeft[nidx], DURATION);
	}
	
	public boolean moveToPre(){
		if(viewLeft[0] > -10)return false;
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
		updateOffset(viewLeft[0]);
		//log("offsetXY = " + offsetXY + ", dis = " + viewLeft[nidx]);
		startTranslation(-viewLeft[nidx], DURATION);
		return true;
	}
	
	
	void log(int i){
		android.util.Log.d("NavigateView", "ALog > " + i);
	}
	void log(String s){
		android.util.Log.d("NavigateView", "ALog > " + s);
	}
	
	private void updateOffset(int off){
		offsetXY = off;
		scrollTo(-offsetXY, 0);
		for(ScreenSwitchListener l:listeners){
			l.onScroll(offsetXY);
		}
		invalidate();
	}
	
	private void updateScreen(int screen){
		for(ScreenSwitchListener l:listeners){
			l.switchToScreen(screen);
		}
	}
	
	public int getScreen(){
		return origin != offsetXY ? -target /mWidth:getScrollX()/mWidth;
	}
	
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
