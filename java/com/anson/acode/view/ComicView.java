package com.anson.acode.view;

import com.anson.acode.ALog;
import com.anson.acode.AnimationHelper;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;

import java.lang.ref.WeakReference;

public class ComicView extends ViewGroup implements XImageView.ClickListener{
    final String TAG = "ComicView";
	GestureDetector detector;
	int DURATION = 800;
    private int duration = DURATION;
	final static int MSG_MOVESBS = 20;
    //sometimes, we just scroll, do NOT call callback.
    boolean cancelScrollListener = false;
	OverscrollView ov;
	H h;
    static class H extends Handler{
        WeakReference<ComicView> a;
        H(ComicView cv){
            a = new WeakReference<ComicView>(cv);
        }
		public void handleMessage(android.os.Message msg) {
            if(a.get() == null) return;
			switch(msg.what){
			case MSG_MOVESBS:
				a.get().moveStepByStep();
				break;
			}
		}
	}
	public ComicView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public ComicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public ComicView(Context context) {
		super(context);
		init();
	}
	int guessDistance = 0;
	void init(){
        Resources res = getResources();
        DURATION = (int)(res.getDisplayMetrics().widthPixels/res.getDisplayMetrics().density);
        h = new H(this);
		detector = new GestureDetector(getContext(), new OnGestureListener() {
				
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					//log("onSingleTapUp");
                    if(ov != null)ov.touchRelease();
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
					postOffset((int)distanceX);
					//requestLayout();
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
					//onTouchRelease();
					
					return false;
				}
				
				@Override
				public boolean onDown(MotionEvent e) {
					//log("onDown");
					return false;
				}
			});
	}
	
	/** touch Event handle ***/
	//private int ox = 0;
	//private int oy = 0;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean spend = detector.onTouchEvent(ev);
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
            if(isAnimating) {
                h.removeMessages(MSG_MOVESBS);
                isAnimating = false;
                return true;
            }
			//log("onInterceptTouchEvent.ACTION_DOWN, " + spend);
			break;
		case MotionEvent.ACTION_MOVE:
			//log("onInterceptTouchEvent.ACTION_MOVE, " + spend);
			//detector.onTouchEvent(ev);
			break;
		case MotionEvent.ACTION_UP:
			//log("onInterceptTouchEvent.ACTION_UP, " + spend);
			//onTouchRelease();
            if(ov != null)ov.clear();
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
		boolean spend = detector.onTouchEvent(event);
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
		final int count = pageCount;///pages.length;
		final int sx = getScrollX();
		for(int i=0; i<count;i++){
            if(getChildAt(i) == null)continue;
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
        if(ov != null)ov.touchRelease();
	}
	
	long startTime = 0;
	long endTime = 0;
	int origin = 0;
	int target = 0;
	int mDis = 0;
    boolean isAnimating = false;
	void startTranslation(int dis, int time){
        isAnimating = true;
		startTime = System.currentTimeMillis();
		origin = getScrollX();
        target = getScrollX() + dis;
        mDis = dis;
        duration = (int)Math.abs(DURATION * mDis / (float)getWidth());
        duration = Math.min(DURATION, duration);
        endTime = startTime + duration;
        h.sendEmptyMessage(MSG_MOVESBS);
	}
	
	boolean moveStepByStep(){
		long cTime = System.currentTimeMillis();
		if(cTime < endTime){
			long time = cTime - startTime;
            float passed = AnimationHelper.getMoveRate((int)time, duration, false);
			scrollTo(origin + (int)(mDis * passed), 0);
            ALog.d(TAG, "moveStepByStep time(" + time + "), passed(" + passed + "), scrollX(" + getScrollX() + ")");
			//requestLayout();
			h.sendEmptyMessage(MSG_MOVESBS);
			return false;
		}else{
            cancelScrollListener = false;
            scrollTo(target, 0);
			origin = getScrollX();
            isAnimating = false;
			//requestLayout();
			return true;
		}
	}
	
	public void scrollToScreen(int screen){
		///screen = screen < 1 ? 1 : (screen > pages.length ? pages.length:screen);
        screen = screen < 1 ? 1 : (screen > pageCount ? pageCount:screen);
		int left = (screen - 1) * -mWidth - getScrollX();
		startTranslation(left, DURATION);
	}
	
	public int getCurrentScreen(){
		return currentScreen;
	}

	private int mWidth = 0;
	private int mHeight = 0;
    private int hw = 0;
	
	public interface ScrollListener{
		void onOffsetChanged(int offset);
		void onOverScroll(boolean isOverLeft);
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
		super.scrollBy(x, y);
		if(null != sListener && !cancelScrollListener){
			sListener.onOffsetChanged(getScrollX());
		}
        updateScreen();
	}

    int currentScreen = 0;
    void updateScreen(){
        //page i: -(1/2 + i) * W/2
        //           < x <=
        //         (1/2 -1) * W/2
        int sx = getScrollX();
        //ALog.d(TAG, "sx:hw:mWidth" + sx + ":" + hw + ":" + mWidth);
        int screen;
        if(sx > -hw){
            screen = -(sx + hw)/mWidth;
        }else{
            screen = -(sx - hw)/mWidth;
        }
        //onPageChanged...
        if(screen != currentScreen){
            currentScreen = screen;
            ALog.d(TAG, "currentScreen=" + currentScreen);
        }
    }
	@Override
	public void scrollTo(int x, int y) {
        //ALog.d(TAG, "scrollTo " + x);
		super.scrollTo(x, y);
		if(null != sListener && !cancelScrollListener){
			sListener.onOffsetChanged(getScrollX());
		}
        updateScreen();
	}
	
	int pageCount = 0;
    int viewMode = XImageView.VMODE_NORMAL;
    XImageView.OnViewModeChanged modeChangedLis;
    OnLongClickListener longLis;
    XImageView[] currentViews;
	public void setPageCount(int count, OnLongClickListener longListener, int viewMode, OverscrollView ov, XImageView.OnViewModeChanged modeChangeLis){
        pageCount = count;
        this.viewMode = viewMode;
        modeChangedLis = modeChangeLis;
        longLis = longListener;
        this.ov = ov;
        currentViews = new XImageView[pageCount];
        //some chapter may be just one page
        for(int i= 0; i < Math.min(2, pageCount); i++){
            addView(createImageView(i));
        }
		requestLayoutForce(true);
	}

    public void setViewMode(int mode){
        viewMode = mode;
        for(int i = 0; i < pageCount; i ++){
            XImageView v = currentViews[i];
            if(v != null){
                v.setViewMode(mode);
            }
        }
    }

    XImageView createImageView(int index){
        XImageView xiv = new XImageView(getContext());
        xiv.setOnLongClickListener(longLis);
        xiv.setIndex(index);
        xiv.setViewMode(viewMode);
        xiv.setOnViewModeChangedListener(modeChangedLis);
        xiv.setOverScrollView(ov);
        xiv.setClickListener(this);
        return xiv;
    }

    public View getViewAndAddToShow(int position){
        XImageView v = currentViews[position];
        if(v == null){
            v = createImageView(position);
            this.addView(v);
        }
        //requestLayout();
        return v;
    }

    public void recycleView(int position){
        XImageView v = currentViews[position];
        if(v != null){
            v.recycle();
            removeView(v);
            currentViews[position] = null;
            logViewsState();
        }
    }

    public void recycle(){
        for(int i = 0; i < pageCount; i ++){
            recycleView(i);
        }
        currentViews = null;
        h.removeMessages(MSG_MOVESBS);
        scrollTo(0, 0);
    }

    void logViewsState(){
        StringBuilder sb = new StringBuilder();
        sb.append("currentViews:\n");
        for(int i = 0; i < pageCount; i ++){
            sb.append(currentViews[i] == null ? "0" : "1").append(", ");
        }

        ALog.d(TAG, sb.toString());
    }

    @Override
    public void addView(View child) {
        XImageView xiv = (XImageView)child;
        currentViews[xiv.getIndex()] = xiv;
        //ALog.d(TAG, "addView " + xiv.getIndex());
        super.addView(child);
    }

    public void setPage(int page){
        cancelScrollListener = true;
		scrollToScreen(page);
	}
	
	boolean inited = false;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
        hw = mWidth/2;
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
		if(layouted && mHeight == (t-b))return;
		int childcount = getChildCount() -1;
		if(childcount > -1){
			for(int i=0; i<=childcount; i++){
				XImageView v = (XImageView)getChildAt(i);
				int left = v.getIndex() * - mWidth;
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
		int halfW = mWidth >> 1;
		int curIdx = -getScrollX() / mWidth + 1;
		int tarIdx = x > halfW ? curIdx-1:curIdx+1;
		ALog.alog("ComicView", "onCLick curIdx = " + curIdx + ", tarIDx = " + tarIdx);
		scrollToScreen(tarIdx);
	}

}
