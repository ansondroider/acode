package com.anson.acode.draw2D;

import java.util.ArrayList;

import com.anson.acode.ALog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

/**
 * summary:
 * the most feature is draw the ImageObject2 on the View;
 * this view is extends SurfaceView, in case that we want it draw dynamic well.
 * the E was some type extends ImageObject2.
 * 
 * features:
 * 1. draw by send a message;
 * 2. implement OnTouchEvent, and will call ImageObject2 whose was Touch on;
 * 3. load view components sync;
 * 
 * @author anson
 *
 * @param <E>
 */

public abstract class AL2DR3DView<E extends ImageObject2> extends SurfaceView implements SurfaceHolder.Callback {
	public ArrayList<E> objs = new ArrayList<E>();
	SurfaceHolder holder = null;
	boolean anti = false;
	E selected = null;
	Loading mLoad;
	boolean isLoading = false;
	boolean animationEnd = true;
	boolean TRANSLUCENT = false;
	static final int ANIMA_DELAYED = 5;
	static final int MSG_MOVE = 1;
	static final int MSG_LOAD_START = 11;
	static final int MSG_LOAD_COMPE = 12;
	static final int MSG_REFRESH = 55;
	static final int MSG_LONG_CLICK = 101;
	final int LONGCLICKED = 500;
	boolean isWaitingForLongClicked = false;
	String TAG = "AL2DR3DView";
	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case MSG_MOVE:
				moveObject();
				break;
			case MSG_LOAD_START:
				isLoading = true;
				break;
			case MSG_LOAD_COMPE:
				isLoading = false;
				postRefresh();
				break;
			case MSG_REFRESH:
				if(animationEnd){
					onMoveAnimationEnd();
				}
				//sendEmptyMessageDelayed(MSG_REFRESH, 100);
				break;
			case MSG_LONG_CLICK:
				if(isWaitingForLongClicked && selected != null){
					selected.onLongClicked();
				}
				postRefresh();
				break;
			}
		};
	};

	/** Construction Method */
	public AL2DR3DView(Context context) {
		super(context);
		init();
	}
	public AL2DR3DView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public AL2DR3DView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/** TouchEvent **/
	int ox = 0;
	int oy = 0;
	int downX = 0;
	int downY = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(isLoading)return false;
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			downX = ox = (int)event.getX();
			downY = oy = (int)event.getY();
			synchronized (objs) {
				int size = objs.size();
				for(int i = size-1; i >-1; i--){
					E e = objs.get(i);
					if(e.isTouchMe((int)event.getX(), (int)event.getY())){
						breakAnimation();
						selected = e;
						e.onActionDown(event);
						isWaitingForLongClicked = true;
						h.removeMessages(MSG_LONG_CLICK);
						h.sendEmptyMessageDelayed(MSG_LONG_CLICK, LONGCLICKED);
						break;
					}
				}
				if(selected == null){
					touchOnNothing(event);
					return false;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(selected != null){
				int cx = (int)event.getX();
				int cy = (int)event.getY();
				int dx = cx - ox;
				int dy = cy - oy;
				selected.onActionMove(event, dx, dy);
				if(Math.abs(dx) + Math.abs(dy) > 10){
					isWaitingForLongClicked = false;
				}
				ox = cx;
				oy = cy;
			}
			
			break;
		case MotionEvent.ACTION_UP:
			int cx = (int)event.getX();
			int cy = (int)event.getY();
			if(selected != null){
				selected.onActionUp(event);
				if(selected.isTouchMe(cx, cy) && Math.abs(cx-downX) + Math.abs(cy-downY) < 10){
					selected.onClicked();
				}
			}
			isWaitingForLongClicked = false;
			selected = null;
			startMoveAnimation();

			break;
		}
		postRefresh();
		return true;
	}
	
	/** Outer Method */
	public void setCallback(SurfaceHolder.Callback cb){
		holder.removeCallback(this);
		holder.addCallback(cb);
	}
	public void addObject(E e){
		synchronized (objs) {
			if(!objs.contains(e)){
				//e.setId(objs.size());
				objs.add(e);
				if(e.isNeedNextStep()){
					//startMoveAnimation();
				}
			}
		}
	}
	public void removeObject(E e){
		synchronized (objs) {
			if(objs.contains(e))objs.remove(e);
		}
	}
	public ArrayList<E> getImageObjects(){
		return this.objs;
	}
	
	long startTime = 0L;
	public void startMoveAnimation(){
		animationEnd = false;
		startTime = System.currentTimeMillis();
		onMoveAnimationStart();
		h.sendEmptyMessage(MSG_MOVE);
	}
	public void postRefresh(Rect r){
		draw(r);
	}
	public void postRefresh(){
		draw(holder.getSurfaceFrame());
	}
	public void touchOnNothing(MotionEvent e){
		
	}
	public void postLoadComplete(){
		h.sendEmptyMessage(MSG_LOAD_COMPE);
	}
	public void setAntis(boolean ant){
		this.anti = ant;
	}
	public void showLoading(boolean show){
		isLoading = show;
		h.sendEmptyMessageDelayed(MSG_MOVE, ANIMA_DELAYED);
	}
	
	/** this method make you view have a translucent background,
	 *  but, REMEMBER this:
	 *  this method MUST called before initView();
	 *  it can cause the view will clean all imageobject.
	 *  becaust the surfaceview will run if life like this:
	 *  create -> changed -> destroy -> create -> changed -> started your view...
	 * @param tran
	 */
	public void setTranslucent(boolean tran){
		this.TRANSLUCENT = tran;
		if(TRANSLUCENT){
			setZOrderOnTop(true);//set translucent, very important method, this will make the current view show in TOP level
			//setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			/**
			 * //set translucent
			 * but, this method will make the surface recreate again.
			 */
			holder.setFormat(PixelFormat.TRANSLUCENT);
		}
	}
	/** Abstract Method */
	/** This medhod should be override by children
	 * all the object will be init here
	 * int w: is the view width
	 * int h: is the view height
	 */
	public abstract void initView(int w, int h);
	
	/**
	 * This method called when the animation started;
	 */
	public abstract void onMoveAnimationStart();
	
	/**
	 * This method called when the animation end;
	 */
	public abstract void onMoveAnimationEnd();
	/** Inner Method **/	 
	protected void init(){
		ALog.alog(TAG, "init");
		h.sendEmptyMessage(MSG_LOAD_START);
		//h.sendEmptyMessageDelayed(MSG_REFRESH, 100);
		holder = this.getHolder();
		holder.addCallback(this);
	}
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		ALog.alog(TAG, "onDetachedFromWindow");
		super.onDetachedFromWindow();
		holder.removeCallback(this);
	}
	void draw(Rect r){
		Canvas c = holder.lockCanvas(r);
		/**Canvas c = holder.lockCanvas();
		ALog.alog(TAG, "draw()");
		Paint paint = new Paint();
		paint.setStrokeWidth(22);
		paint.setColor(Color.RED);
		c.drawLine(0, 0, 200, 1000, paint);
		**/
		if(c == null) return;
		if(anti)
			c.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		
		if(TRANSLUCENT)
			c.drawColor(Color.TRANSPARENT,Mode.CLEAR);
		Paint p = new Paint();
		synchronized (objs) {
			for(E e:objs){
				if(e == selected)continue;
				//will change the object scaled and then the size out of range from origin rect
				// will not draw. exectly, we want to draw all the scaled RECT in view;
				Rect er = e.getStat().getScaledRect();
				if(er.left > r.right || er.top > r.bottom || er.right < r.left || er.bottom < r.top)
					continue;
				c.save();
				c.clipRect(er);
				//c.drawColor(e.getBackColor());
				e.draw(c, p);
				c.restore();
			}
			if(selected != null){
				Rect er = selected.getStat().getScaledRect();
				c.save();
				c.clipRect(er);
				//c.drawColor(selected.getBackColor());
				selected.draw(c, p);
				c.restore();
			}
			if(isLoading){
				c.save();
				c.clipRect(mLoad.getStat().getRect());
				mLoad.draw(c, p);
				c.restore();
			}
		}
		
		holder.unlockCanvasAndPost(c);
	}
	
	void moveObject(){
		long currentTime = System.currentTimeMillis();
		int timePassed = (int)(currentTime - startTime);
		//Tool.ansonLog("passTime = " + timePassed);
		boolean needNextStep = false;
		synchronized (objs) {
			for(E e:objs){
				if(e.isNeedNextStep()){
					e.moveToTargetStepByStep(timePassed);
					needNextStep = true;
				}
			}
		}
		if(isLoading){
			mLoad.moveToTargetStepByStep(0);
		}
		if(needNextStep){
			h.sendEmptyMessageDelayed(MSG_MOVE, ANIMA_DELAYED);
		}else{
			startTime = 0L;
			animationEnd = true;
			ALog.alog(TAG, "moveObject > onMoveAnimationEnd");
			onMoveAnimationEnd();
			if(isLoading){
				h.sendEmptyMessageDelayed(MSG_MOVE, ANIMA_DELAYED);
			}
		}
		postRefresh();
	}
	
	/**
	 * if user touch the view, we may want to break the animation,
	 * and handle the user action FIRST.
	 * this method will force the Object to the target Status.
	 */
	void breakAnimation(){
		h.removeMessages(MSG_MOVE);//brake animation
		for(E e:objs){
			if(e.isNeedNextStep()){
				e.getStat().forceToStatus(e.getTarget());
				e.setTarget(null, null, false);
			}
		}
	}
	/** SurfaceHolder.Callback Method */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		ALog.alog(TAG, "surfaceChanged");
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		ALog.alog(TAG, "surfaceCreated");
		final int w = holder.getSurfaceFrame().width();
		final int h = holder.getSurfaceFrame().height();
		
		mLoad = mLoad.getLoadingView(w, h, getResources());
		new Thread(){
			public void run(){
				ALog.alog(TAG, "initView");
				
				initView(w, h);
				
				postLoadComplete();
			}
		}.start();
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		ALog.alog(TAG, "surfaceDestroyed");
		// TODO Auto-generated method stub
		for(E e:objs){
			e.recycle();
		}
		objs.clear();
		//objs = null;
		selected = null;
		//holder.removeCallback(this);
		h.removeMessages(MSG_MOVE);
	}
}
