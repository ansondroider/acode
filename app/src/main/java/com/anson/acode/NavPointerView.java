package com.anson.acode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class NavPointerView extends View implements NavigateView.ScreenSwitchListener {
	public final float MAXALPHA = 240f;
	private int offsetX = 0;
	private float radium = 8;
	private int paddingRight = 30;
	private int TEXTSIZE = 12;
	private int Y = 15;
	private int parentOffset = 0;
	String TAG = "NavPointerView";
	Paint paint = null;
	private int[] textPadding = new int[2];
	
	public NavPointerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(TEXTSIZE);

		float scale = context.getResources().getDisplayMetrics().density;
		radium *= scale;
		paddingRight *= scale;
		TEXTSIZE *= scale;
		Y *= scale;
		int tw = StringUtils.getTextWidth("0", paint);
		textPadding[0] = tw;
		textPadding[1] = (int) ((radium * 2 - TEXTSIZE) /2);
	}
	public NavPointerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPointCount(3);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(TEXTSIZE);

		float scale = context.getResources().getDisplayMetrics().density;
		radium *= scale;
		paddingRight *= scale;
		TEXTSIZE *= scale;
		Y *= scale;
		int tw = StringUtils.getTextWidth("0", paint);
		textPadding[0] = tw;
		textPadding[1] = (int) ((radium * 2 - TEXTSIZE) /2);
	}
	public NavPointerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setAntiAlias(true);
		float scale = context.getResources().getDisplayMetrics().density;
		
		radium *= scale;
		paddingRight *= scale;
		TEXTSIZE *= scale;
		Y *= scale;
		int tw = StringUtils.getTextWidth("0", paint);
		textPadding[0] = tw;
		textPadding[1] = (int) ((radium * 2 - TEXTSIZE) /2);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		for(int i=0; i < pointers.length; i++){
			draw(pointers[i], canvas);
		}
	}
	
	int width = 0;
	int height = 0;
	boolean inited = false;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		offsetX = width - (int)(radium * 2) - (pointers.length - 1) * paddingRight;
		offsetX >>= 1;
		if(width > 0 && height > 0 && !inited){
			inited = true;
			onScroll(parentOffset);
		}
	}
	void draw(Pointer p, Canvas c){
		paint.setStyle(Style.FILL);
		paint.setColor(Color.GREEN);
		paint.setAlpha(p.alpha);
		c.drawCircle(p.x + offsetX, p.y, p.r-2, paint);
		paint.setStyle(Style.STROKE);
		paint.setAlpha((int)MAXALPHA);
		c.drawCircle(p.x + offsetX, p.y, p.r, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(TEXTSIZE);
		c.drawText(p.num, (float)(p.x + offsetX) - textPadding[0], (float)p.y + textPadding[1], paint);
	}
	
	/**
	 * this method should called by the Activity
	 * to set How many points will show
	 */
	Pointer pointers[];
	public void setPointCount(int c){
		pointers = new Pointer[c];
		for(int i=0; i<c;i++){
			Pointer p = new Pointer();
			p.set(i * paddingRight, Y);
			p.num = String.valueOf(i+1);
			p.idx = i;
			pointers[i] = p;
		}
		pointers[0].alpha = (int)MAXALPHA;
	}
	
	class Pointer extends Point{
		private String num;
		private int idx;
		private float r = radium;
		private int forColor = 0;
		private int backColor = 0;
		private int textSize = TEXTSIZE;
		private int alpha = 0;
		private boolean selected = false;
		public void updateAlpha(int offset){
			if(isInArea(offset)){
				postInvalidate();
			}else{
				alpha = 0;
			}
		}
		boolean isInArea(int offset){
			int os = offset + (idx * width);
			if(0 <= os && os < width){
				alpha = (int)MAXALPHA - (int)(os * MAXALPHA / width);
				return true;
			}else if(-width < os && os < 0){
				alpha = (int)(os * MAXALPHA / width);
				alpha = alpha == 0 ? -1:alpha;
				return true;
			}else{
				return false;
			}
			
		}
	}
	
	/**
	 * method implements from NavigateView.ScreenSwitchListener 
	 * this method will called after you NavigateView.addSiwtchListener;
	 */
	public void onScroll(int offset){
		for(int i=0; i<pointers.length; i++){
			pointers[i].updateAlpha(offset);
			parentOffset = offset;
		}
	}
	@Override
	public void switchToScreen(int screen) {
		// TODO Auto-generated method stub
		
	}
}
