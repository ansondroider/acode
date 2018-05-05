package com.anson.acode.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.anson.acode.ALog;
import com.anson.acode.HttpUtilsAndroid;


public class ProgressView extends View {

	public ProgressView(Context context) {
		super(context);
		init();
	}
	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public ProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	int bgColor = Color.argb(255, 222, 222, 222);
	int forColor = Color.argb(255, 222, 255, 222);
	int radius = 40;
	int cx, cy;
	RectF ov = new RectF();
	Paint paint;
	float angle = 0;
	void init(){
		pcb = new HttpUtilsAndroid.HttpProgressListener() {

            @Override
            public void onRequestStart() {}
            @Override
            public boolean canceled() {
                return false;
            }
            @Override
            public void onFinish() {}
            @Override
            public void onResponse(int code) {}
            @Override
				public void onProgress(long progress, long full) {
					ALog.alog("ProgressView", "ALog 0603 > onProgressChange(" + progress + ", " + full + ")");
					setProgress((int)progress, (int)full);
					postInvalidate();
				}
			};
		paint = new Paint();
		paint.setAntiAlias(true);
	}
	public void setProgress(int progress, int full){
		angle = 360 * progress / full;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		paint.setColor(bgColor);
		canvas.drawCircle(cx, cy, radius, paint);
		paint.setColor(forColor);
		canvas.drawArc(ov, -90, angle, true, paint);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if(width > 0 && height > 0){
			cx = width >> 1;
			cy = height >> 1;
			ov.left = cx - radius;
			ov.top = cy - radius;
			ov.right = cx + radius;
			ov.bottom = cy + radius;
		}
	}
	
	public HttpUtilsAndroid.HttpProgressListener pcb = null;

}
