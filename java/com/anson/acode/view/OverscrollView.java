package com.anson.acode.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.anson.acode.R;

public class OverscrollView extends View {
	String TAG = "OverscrollView";
	
	public static final int over_none = 0x0000;
	public static final int over_left = 0x0001;
	public static final int over_top =  0x0010;
	public static final int over_right =0x0100;
	public static final int over_bottom=0x1000;
	
	private final int DURATION = 200;
	
	private int maxAlpha = 255;
	private int minAlpha = 0;
	
	private int alpha = 0;
	
	private int overscrollFlags = 0;
	
	Drawable overscroll_left, overscroll_top, overscroll_right, overscroll_bottom;
	
	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			moveStepByStep();
		}
	};
	
	public OverscrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public OverscrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public OverscrollView(Context context) {
		super(context);
		init();
	}
	
	void init(){
		overscroll_left = getResources().getDrawable(R.drawable.overscroll_left);
		overscroll_top = getResources().getDrawable(R.drawable.overscroll_top);
		overscroll_right = getResources().getDrawable(R.drawable.overscroll_right);
		overscroll_bottom = getResources().getDrawable(R.drawable.overscroll_bottom);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if(width > 0 && height > 0){
			overscroll_left.setBounds(0, 0, width/4, height);
			overscroll_top.setBounds(0, 0, width, height/4);
			overscroll_right.setBounds(width/4*3, 0, width, height);
			overscroll_bottom.setBounds(0, height/4*3, width, height);
		}
	}
	public void clear(){
		overscrollFlags = 0;
		alpha = 0;
	}
	public void setOverScroll(int direct, int distance){
		alpha = distance;
		alpha = alpha < minAlpha ? minAlpha : (alpha > maxAlpha ? maxAlpha :alpha);
		overscrollFlags |= direct;
		postInvalidate();
	}
	
	public void touchRelease(){
		startClear();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//ALog.alog(TAG, "onDraw overscrollFlags = " + overscrollFlags);
		if(canvas == null || overscrollFlags == 0)return;
		if((over_left & overscrollFlags) > 0){
			overscroll_left.setAlpha(alpha);
			overscroll_left.draw(canvas);
		}
		if((over_right & overscrollFlags) > 0){
			overscroll_right.setAlpha(alpha);
			overscroll_right.draw(canvas);
		}
		if((over_top & overscrollFlags) > 0){
			overscroll_top.setAlpha(alpha);
			overscroll_top.draw(canvas);
		}
		if((over_bottom & overscrollFlags) > 0){
			overscroll_bottom.setAlpha(alpha);
			overscroll_bottom.draw(canvas);
		}
	}
	
	int tranAlpha = 0;
	long startTime = 0;
	void startClear(){
		startTime = System.currentTimeMillis();
		tranAlpha = alpha;
		h.sendEmptyMessageDelayed(0, 20);
	}
	
	
	void moveStepByStep(){
		long curTime = System.currentTimeMillis();
		int passTime = (int)(curTime - startTime);
		if(passTime < DURATION){
			alpha = maxAlpha - tranAlpha * passTime / DURATION;
			h.sendEmptyMessageDelayed(0, 20);
			postInvalidate();
		}else{
			alpha = 0;
			postInvalidate();
			clear();
		}
	}
}
