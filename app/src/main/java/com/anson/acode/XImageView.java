package com.anson.acode;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class XImageView extends ImageView{

	private Bitmap mBm = null;
	private Bitmap preBm = null;
	boolean isClick = true;
	boolean showProgress = false;
	ClickListener clistener;
	final String TAG = "XImageView";
	float minScale = 1;
	float autoScale = 1;
	float[] target = null;
	float[] params = null;
	float[] current = new float[9];
	int DURATION = 500;
	int index = -1;
	IndexChangedListener icListener;
	long startTime;
	long endTime;
	private int idx = 0;
	private String tag = "XImageView";
	private Matrix matrix;
	private int ViewMode = VMODE_AUTOSCALE;
	private int CurMode = ViewMode;
	private int mode = NONE;
	private float oldDist;
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	public final static int VMODE_NORMAL = 0;
	public final static int VMODE_AUTOSCALE = 1;
	public final static int VMODE_FITSCREEN = 2;
	public final static int MSG_DOUBLE_CLICKCANCEL = 81;
	public final static int MSG_DOUBLE_CLICKED = 82;
	public final static int MSG_LONG_CLICKCANEL = 83;
	public final static int MSG_LONG_CLICKED = 84;
	public final static int MSG_SINGLE_CLICKED = 85;
	public final static int MSG_MOVE_SBS = 91;
	public final static int MSG_LOADNXTBM_COMP = 71;
	float sysScale = 1;
	
	int bgColor = Color.argb(200, 244, 244, 244);
	int forColor = Color.argb(200, 200, 255, 200);
	int radius = 40;
	int cx, cy;
	RectF ov = new RectF();
	Paint paint;
	float angle = 0;
	public HttpUtilsAndroid.ProgressCallback pcb = null;

	
	/** Handler ********************************************/
	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//ALog.alog(tag, "msg.what = " + msg.what);
			switch(msg.what){
			case MSG_DOUBLE_CLICKCANCEL:
				cancelDoubleClick();
				break;
			case MSG_DOUBLE_CLICKED:
				performDoubleClicked();
				break;
			case MSG_LONG_CLICKCANEL:
				cancelLongClick();
				break;
			case MSG_LONG_CLICKED:
				performLongClicked();
				break;
			case MSG_SINGLE_CLICKED:
				float[] p = (float[])msg.obj;
				preformSingleClicked(p[0], p[1]);
				break;
			case MSG_MOVE_SBS:
				moveStepByStep();
				break;
			case MSG_LOADNXTBM_COMP:
				setImageBitmap(bmNxt);
				bmNxt = null;
				startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
				break;
			}
		};
	};
	
	/** Construction ****************************************/
	public XImageView(Context context) {
		super(context);
		matrix = getImageMatrix();
		setScaleType(ScaleType.MATRIX);

		// TODO Auto-generated constructor stub
		init();
	}
	public XImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		matrix = getImageMatrix();
		setScaleType(ScaleType.MATRIX);
		// TODO Auto-generated constructor stub
		init();
	}
	public XImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		matrix = getImageMatrix();
		setScaleType(ScaleType.MATRIX);
		// TODO Auto-generated constructor stub
		init();
	}
	
	/**********************************************************/
	void init(){
		sysScale = getResources().getDisplayMetrics().density;
		radius *= sysScale;
		pcb = new HttpUtilsAndroid.ProgressCallback() {
				
				@Override
				public void onProgressChange(int progress, int full) {
					// TODO Auto-generated method stub
					//ALog.alog("ProgressView", "ALog 0603 > onProgressChange(" + progress + ", " + full + ")");
					setProgress(progress, full);
					postInvalidate();
				}
			};
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(14 * sysScale);
	}
	public void setProgress(int progress, int full){
		angle = 360 * progress / full;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
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
	public void setMinScale(float scale){
		minScale = scale;
	}
	public float getMinScale(){
		return minScale;
	}
	
	public void setAutoScale(float scale){
		autoScale = scale;
	}
	public float getAutoScale(){
		return autoScale;
	}
	
	
	public boolean isCanDrag(){
		boolean drag = false;
		if(mBm != null){
			int w = getWidth();
			int h = getHeight();
			Matrix mat = getImageMatrix();
			float[] values = new float[9];
			mat.getValues(values);
			float bw = mBm.getWidth() * values[0];
			float bh = mBm.getHeight() * values[0];
			Rect vRect = new Rect(0, 0, w, h);
			Rect bRect = new Rect((int)values[2], (int)values[5], (int)(values[2]+bw), (int)(values[5]+bh));
			
			if(bw > w){
				if(vRect.right < bRect.right || vRect.left > bRect.left){
					drag = true;
				}
			}
			if(bh > h){
				if(vRect.top > bRect.top || vRect.bottom < bRect.bottom){
					drag = true;
				}
			}			
		}
		return drag;
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		//ALog.alog(TAG, "setImageBitmap(" + (bm == null ? "NULL":"NOTNULL") +") idx = " + index);
		int vW = getWidth();
		int vH = getHeight();
		drawCache = false;
		// TODO Auto-generated method stub
		if(bm == null){
			//ALog.alog(TAG, "setImageBitmap(null) go recycle");
			if(mBm != null)mBm.recycle();
			mBm = null;
			if( null != bmNxt)bmNxt.recycle();
			//isLoading = false;
			return;
		}
		
		preBm = mBm;
		int bmW = bm.getWidth();
		int bmH = bm.getHeight();
		float scale[] = BitmapUtils.getScaleByMode(bmW, bmH, vW, vH);
		//ALog.alog(TAG, "bw:bh=" + bmW + ":" + bmH + ", vW:vH=" + vW + ":" + vH);
		//ALog.alog(TAG, "scale = " + scale[0] + ", " + scale[1] + ", " + scale[2]);
		ViewMode = CurMode;
		int cW = (int)(bmW * scale[ViewMode]);
		int cH = (int)(bmH * scale[ViewMode]);
		//ALog.alog(TAG, "finalScale = " + ViewMode + ":" + scale[ViewMode] + "\n" +
		//			"cW = " + cW + ", cH = " + cH);
		matrix.setScale(scale[ViewMode], scale[ViewMode]);
		if(cW > vW || cH > vH){
			matrix.postTranslate(vW - cW, 0);
		} else {
			matrix.postTranslate(vW - cW >> 1, vH - cH >> 1);
		}
		
		setAutoScale(scale[VMODE_AUTOSCALE]);
		setMinScale(scale[VMODE_FITSCREEN]);
		mBm = bm;
		setImageMatrix(matrix);//Sould add in Nexus 5
		postInvalidate();
		if(preBm != null){
			preBm.recycle();
		}
		//isLoading = false;
	}
	@Override
	public void setImageMatrix(Matrix matrix) {
		// TODO Auto-generated method stub
		super.setImageMatrix(matrix);
		this.matrix = matrix;
	}
	
	boolean drawCache = false;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(drawCache){
			
		}else{
			try{
				super.onDraw(canvas);
			}catch(Exception e){
				ALog.alog(TAG, "error onDraw()");
			}
		}
		if(showProgress){
			paint.setColor(bgColor);
			canvas.drawCircle(cx, cy, radius, paint);
			paint.setColor(forColor);
			canvas.drawArc(ov, -90, angle, true, paint);
			paint.setColor(Color.BLACK);
			canvas.drawText(formatProgress(), cx-16, cy, paint);
		}
	}
	String formatProgress(){
		String progress = String.valueOf(angle * 100/360);
		if(progress.length() > 5){
			progress = progress.substring(0, 5);
		}
		return progress + "%";
	}
	
	public Bitmap getImage(){
		return mBm;
	}
	
	public void setIdxChangeListener(IndexChangedListener lis){
		this.icListener = lis;
	}
	public void onIndexChanged(int idx){
		drawCache = true;
		this.idx = idx;
		if(icListener != null){
			icListener.indexChanged(idx);
		}
	}
	public int getIdx(){
		return this.idx + 1;
	}
	public interface IndexChangedListener{
		void indexChanged(int idx);
		void onLongClicked();
	}
	
	
	/** Handle Double Click Event *************************************/
	boolean waitForDoubleClick = false;
	void cancelDoubleClick(){
		waitForDoubleClick = false;
		//ALog.alog("cancelDoubleClick");
	}
	
	void performDoubleClicked(){
		//ALog.alog("performDoubleClicked");
		if(mBm == null)return;
		h.removeMessages(MSG_DOUBLE_CLICKCANCEL);
		h.removeMessages(MSG_LONG_CLICKED);
		h.removeMessages(MSG_SINGLE_CLICKED);
		waitForDoubleClick = false;
		if(CurMode == VMODE_FITSCREEN){
			CurMode = VMODE_NORMAL;
		}else{
			CurMode ++;
		}
		startMoveAnimation(getTargetFromMode(CurMode));
	}
	
	public void setClickListener(ClickListener cl){
		clistener = cl;
	}
	void preformSingleClicked(float x, float y){
		waitForDoubleClick = false;
		h.removeMessages(MSG_DOUBLE_CLICKED);
		if(clistener != null)clistener.onClick(x, y);
	}
	
	
	boolean readyToLongClick = false;
	void cancelLongClick(){
		h.removeMessages(MSG_LONG_CLICKED);
		readyToLongClick = false;
	}
	
	OnLongClickListener longList = null;
	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		// TODO Auto-generated method stub
		super.setOnLongClickListener(l);
		longList = l;
	}
	void performLongClicked(){
		//ALog.alog(tag, " performLongClicked");
		if(icListener != null && 0 == getLeft()){
			icListener.onLongClicked();
		}
		
		if(longList != null && getLeft() % getWidth() == 0)longList.onLongClick(this);
		h.removeMessages(MSG_SINGLE_CLICKED);
		readyToLongClick = false;
		waitForDoubleClick = false;
	}
	
	/** TouchEvent for imageView *******************************************/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//ALog.alog(TAG, "onTouchEvent()");
		XImageView xiv = this;
		Matrix matrix = xiv.getImageMatrix();
		boolean spend = true;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				isClick = true;
				readyToLongClick = true;
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				if(xiv.isCanDrag()){
					mode = DRAG;
					xiv.getParent().requestDisallowInterceptTouchEvent(true);
				}
				h.sendEmptyMessageDelayed(MSG_LONG_CLICKED, 300);
				break;
			case MotionEvent.ACTION_UP:
				h.sendEmptyMessage(MSG_LONG_CLICKCANEL);
				float ux = start.x - event.getX();
				float uy = start.y - event.getY();
				if(Math.sqrt(ux * ux + uy * uy) > 16.0f){
					isClick = false;
				}
				if(isClick){
					if(waitForDoubleClick){
						h.sendEmptyMessage(MSG_DOUBLE_CLICKED);
					}else{
						waitForDoubleClick = true;
						h.sendEmptyMessageDelayed(MSG_DOUBLE_CLICKCANCEL, 500);
						Message msg = h.obtainMessage();
						msg.what = MSG_SINGLE_CLICKED;
						msg.obj = new float[]{event.getX(), event.getY()};
						if(readyToLongClick)h.sendMessageDelayed(msg, 200);
						h.sendEmptyMessage(MSG_LONG_CLICKCANEL);
					}
					
				}
				
				onTouchRelease();
				break;
			case MotionEvent.ACTION_POINTER_UP:
				h.sendEmptyMessage(MSG_LONG_CLICKCANEL);
				mode = NONE;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				h.sendEmptyMessage(MSG_LONG_CLICKCANEL);
				//isClick = false;
				oldDist = spacing(event);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}
				if(event.getPointerCount() > 1){
					xiv.getParent().requestDisallowInterceptTouchEvent(true);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				//isClick = false;
				float vx = start.x - event.getX();
				float vy = start.y - event.getY();
				if(Math.sqrt(vx * vx + vy * vy) > 16.0f){
					h.sendEmptyMessage(MSG_LONG_CLICKCANEL);
				}
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					int[] dis = calcAvaSpace(xiv, matrix, event.getX() - start.x, event.getY()- start.y);
					if(Math.abs(dis[0]) < 1 && Math.abs(dis[1]) < 1 && Math.abs(event.getX() - start.x) > 5){
						xiv.getParent().requestDisallowInterceptTouchEvent(false);
					}else{
						matrix.postTranslate(dis[0], dis[1]);
						osView.clear();
						if(Math.abs(vx) > Math.abs(vy)){
							if((int)Math.abs(dis[0]) != (int)Math.abs(vx)){
								if(vx < 0)osView.setOverScroll(OverscrollView.over_left, (int)Math.abs(vx));
								if(vx > 0)osView.setOverScroll(OverscrollView.over_right, (int)Math.abs(vx));
							}
						}else{
							if((int)Math.abs(dis[1]) != (int)Math.abs(vy)){
								if(vy < 0)osView.setOverScroll(OverscrollView.over_top, (int)Math.abs(vy));
								if(vy > 0)osView.setOverScroll(OverscrollView.over_bottom, (int)Math.abs(vy));
							}
						}
					}
				} else if (mode == ZOOM) {
					float newDist = spacing(event);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
			}

			xiv.setImageMatrix(matrix);
			xiv.postInvalidate();
			return spend;
	}
	
	/** Animation *****************************************************/
	private void onTouchRelease(){
		osView.touchRelease();
		if(mBm == null)return;
		matrix.getValues(current);
		if(current[0] < getMinScale()){
			startMoveAnimation(getTargetFromMode(ViewMode));
		}
	}
	
	void startMoveAnimation(float[] tar){
		target = tar;
		matrix.getValues(current);
		params = new float[3];
		
		ALog.alog(TAG, "startMoveAnimation tar=" + tar[0] + "," + tar[1] + "," + tar[2] + "\n" +
						"current=" + current[0] + ","+current[1]+","+current[2]);
		//target[0] = getMinScale();
		float bw = mBm.getWidth() * target[0];
		float bh = mBm.getHeight() * target[0];
		//target[1] =((int)(getWidth() - bw)) >> 1;
		//target[2] =((int)(getHeight() - bh)) >> 1;
		
		params[0] = target[0] - current[0];
		params[1] = target[1] - current[2];
		params[2] = target[2] - current[5];
		startTime = System.currentTimeMillis();
		endTime = startTime + DURATION;
		h.sendEmptyMessage(MSG_MOVE_SBS);
	}
	
	void moveStepByStep(){
		long time = System.currentTimeMillis();
		float progress =AnimationHelper.getMoveRate((int)(time-startTime), DURATION, true); //(time - startTime)/(float)DURATION;
		if(progress >= 1){
			matrix.setScale(target[0], target[0]);
			matrix.postTranslate(target[1],target[2]);
		}else{
			matrix.setScale(current[0] + params[0] * progress, current[0] + params[0] * progress);
			matrix.postTranslate(current[2] + params[1] * progress, current[5] + params[2] * progress);
			h.sendEmptyMessage(MSG_MOVE_SBS);
		}
		postInvalidate();
	}
	
	
	
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
	/**
	 * calculate max space we can move.
	 * @param v
	 * @param mat
	 * @param dx
	 * @param dy
	 * @return
	 */
	private int[] calcAvaSpace(XImageView v, Matrix mat, float dx, float dy){
		int[] dis = {0, 0};
		Bitmap bm = v.getImage();
		float[] values = new float[9];
		mat.getValues(values);
		if(bm != null){
			Rect vr = new Rect(0, 0, v.getWidth(), v.getHeight());
			int bw = (int)(values[0] * bm.getWidth());
			int bh = (int)(values[0] * bm.getHeight());
			Rect br = new Rect((int)values[2], (int)values[5], (int)(values[2] + bw), (int)(values[5] + bh));
			br.offset((int)dx, (int)dy);
			dis[0] = (int)dx;
			dis[1] = (int)dy;
			if(bw >= v.getWidth()){
				if(dx >= 0 && vr.left < br.left){
					dis[0] = (int)(dx - br.left);
				}else if(dx < 0 && vr.right > br.right){
					dis[0] = (int)(dx + vr.right - br.right);
				}
			}else{
				if(dx >= 0 && vr.right < br.right){
					dis[0] = (int)(dx - br.right + vr.right);
				}else if(dx < 0 && vr.left > br.left){
					dis[0] = (int)(dx - br.left);
				}
			}
			if(bh >= v.getHeight()){
				if(dy >= 0 && vr.top < br.top){
					dis[1] = (int)(dy - br.top);
				}else if(dy < 0 && vr.bottom > br.bottom){
					dis[1] = (int)(dy + vr.bottom - br.bottom);
				}
			}else{
				if(dy >= 0 && vr.bottom < br.bottom){
					dis[1] = (int)(dy - br.bottom + vr.bottom);
				}else if(dy < 0 && vr.top > br.top){
					dis[1] = (int)(dy - br.top);
				}
			}
		}
		return dis;
	}
	
	float[] getTargetFromMode(int mode){
		float[] tar = new float[3];
		tar[0] = mode == VMODE_NORMAL ? 1 : 
					(mode == VMODE_AUTOSCALE ? getAutoScale() : getMinScale());
		float bw = mBm.getWidth() * tar[0];
		float bh = mBm.getHeight() * tar[0];
		tar[1] =((int)(getWidth() - bw)) >> 1;
		tar[2] =((int)(getHeight() - bh)) >> 1;
		return tar;
	}
	
	public void setViewMode(int mode){
		CurMode = mode;
	}
	
	Bitmap bmNxt = null;
	volatile boolean isLoading = false;
	/** Load and Update Bitmap to ImageView **************************************************/
	public void loadNextBitmap(final int idx, final ArrayList<String[]> pictures, final String title, final String[] chapter, 
			final String savePath, final String header, final String headerValue, final boolean force){
		if(isLoading || mBm != null && !force)return;
		setShowProgress(true);
		new Thread(){
			@Override
			public void run() {
				//ALog.alog("XImageView", "ALog 0605 > loadNextBitmap()_idx = " + idx + " isLoading = " + isLoading);
				if(isLoading)
					return;
				isLoading = true;
				String[] pic = pictures.get(idx);
				Bitmap bm = null;
				File f = new File(savePath + title + "/" + chapter[1] + "/" + 
						StringUtils.changeStringTile(pic[0], ".", ".co"));//comicName/chapterName/1.co
				//ALog.alog(f.getAbsolutePath());
				if(!force)
					bm = BitmapUtils.decodeBitmapWithExceptionCatch(f.getAbsolutePath());
				else{
					if(f.exists())f.delete();
				}
				if(bm == null){
					if(pic[1].startsWith("http://")){
						bm = BitmapUtils.getBitmapFromUrlWidthHeader(pic[1], savePath + title + "/" + chapter[1],
							StringUtils.changeStringTile(pic[0], ".", ".co"), true, header, headerValue, 
							pcb);
					}
				}
				
				bmNxt = bm;		
				h.sendEmptyMessage(MSG_LOADNXTBM_COMP);
				setShowProgress(false);
				isLoading = false;
			}
		}.start();
	}
	
	public void setIndex(int idx){
		this.index = idx;
	}
	
	public void setShowProgress(boolean show){
		showProgress = show;
		postInvalidate();
	}
	
	public interface ClickListener{
		void onClick(float x, float y);
	}
	
	OverscrollView osView;
	public void setOverScrollView(OverscrollView ov){
		this.osView = ov;
	}
	
}
