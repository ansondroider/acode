package com.anson.acode.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.anson.acode.ALog;
import com.anson.acode.AnimationHelper;

public class NavigateView extends ViewGroup {
	private int DURATION = 750;
    private int duration = DURATION;
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
	}
	public NavigateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public NavigateView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public void setScreen(int i){
		currentScreen = i;
	}
	int guessDistance = 0;
    float velX;
    int scrollDir = -1;//-1:nospecial; 0:LNR; 1:TNB
	void init(){
        Resources res = getResources();
        DURATION = (int)(res.getDisplayMetrics().widthPixels/res.getDisplayMetrics().density);
        detector = new GestureDetector(new OnGestureListener() {
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return false;
				}

				@Override
				public void onShowPress(MotionEvent e) {}
				
				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
						float distanceY) {
                    if(!scrollEnabled)return false;
					//log("onScroll, disX = " + distanceX + ", disY = " +distanceY);
                    if(scrollDir == -1)scrollDir = Math.abs(distanceX) < Math.abs(distanceY) ? 1 : 0;
					if(scrollDir != 0)return false;

					updateOffset((int)(offsetXY - distanceX));
					return true;
					
				}
				
				@Override
				public void onLongPress(MotionEvent e) {}
				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						float velocityY) {
                    if(!scrollEnabled)return false;
					//log("onFling" + ", velocityX = " + velocityX + ", velocityY = " + velocityY);
                    velX = velocityX;
					guessDistance = scrollDir == 0 ? (int)velocityX/8 : 0;
					onTouchRelease();
					
					return false;
				}
				
				@Override
				public boolean onDown(MotionEvent e) {
                    scrollDir = -1;
					return false;
				}
			});
	}

	private int mWidth = 0;
	private int mHeight = 0;
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

    boolean scrollEnabled = true;
    public void disableScroll(){
        scrollEnabled = false;
    }

    public void enableScroll(){
        scrollEnabled = true;
    }
	
	
	/** touch Event handle ***/
	private int ox = 0;
	private int oy = 0;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean spend = false;
		spend = detector.onTouchEvent(ev);
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
            h.removeMessages(MSG_MOVESBS);
            //log("onInterceptTouchEvent.ACTION_DOWN, " + spend);
			break;
		case MotionEvent.ACTION_MOVE:
			//log("onInterceptTouchEvent.ACTION_MOVE, " + spend);
			//detector.onTouchEvent(ev);
			break;
		case MotionEvent.ACTION_UP:
			//log("onInterceptTouchEvent.ACTION_UP, " + spend);
			break;
		case MotionEvent.ACTION_CANCEL:
			//log("onInterceptTouchEvent.ACTION_CANCEL, " + spend);
			break;
		}
		
		return spend;
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
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
		startTranslation(-viewLeft[nidx]);
	}
	
	long startTime = 0;
	long endTime = 0;
	int origin = 0;
	int target = 0;
	int mDis = 0;
	void startTranslation(int dis){
		startTime = System.currentTimeMillis();
		origin = offsetXY;
		target = offsetXY + dis;
		mDis = dis;
        duration = DURATION * Math.abs(mDis) / mWidth;
        endTime = startTime + duration;
        h.sendEmptyMessage(MSG_MOVESBS);
	}
	
	boolean moveStepByStep(){
		long cTime = System.currentTimeMillis();
		if(cTime < endTime){
			long time = cTime - startTime;
			updateOffset(origin + (int)(mDis * AnimationHelper.getMoveRate((int)time, duration, false)));
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

    public boolean isScrollEnd(){
        //java.lang.ArithmeticException: divide by zero
        return 0 < getWidth() && getScrollX() % getWidth() == 0;
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
		startTranslation(-viewLeft[nidx]);
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
		startTranslation(-viewLeft[nidx]);
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
		startTranslation(-viewLeft[nidx]);
		return true;
	}

	private void updateOffset(int off){
		offsetXY = off;
		scrollTo(-offsetXY, 0);
		for(ScreenSwitchListener l:listeners){
			l.onScroll(offsetXY);
            int screen = -(offsetXY - mWidth/2) / mWidth;
            screen = screen < 0 ? 0 : screen;
            screen = screen > getChildCount() - 1 ? getChildCount() - 1 : screen;
            //ALog.d("updateOffset screen=" + screen + "," + offsetXY);
            if(currentScreen != screen){
                currentScreen = screen;
                l.switchToScreen(currentScreen);
            }
		}
		invalidate();
	}

	public int getScreen(){
		return origin != offsetXY ?
                -target /mWidth :
                getScrollX()/mWidth;
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
		 * @param screen screen index
		 */
		void switchToScreen(int screen);
		
		/**
		 * the offset is the value of all children layout offset in X
		 * @param offset offset of X
		 */
		void onScroll(int offset);
	}
}
