package com.anson.acode.draw2D;

import com.anson.acode.ALog;
import com.anson.acode.AUtils;
import com.anson.acode.BitmapUtils;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * summary:
 * testcodes.base class of all the part in view.
 * it the better to have to draw a bitmap, 
 * if not, I suggest you to create an other class to perform what you want.
 * 
 * features:
 * 1. support rotate in 3D (x, y, z);
 * 2. support translate (x, y, z);
 * 3. support scale;
 * 4. support some simple animation(no interceptor);
 * 5. support simple interface or callback method in TouchEvent;
 * 
 * @author anson
 * @date 2012.6.9;
 */
public abstract class ImageObject2 {
	private Status stat, target;
	private Bitmap bm;
	private Rect oRect;
	private int screenW, screenH;
	private int id;
	private int backColor = Color.argb(0, 0, 0, 0);
	private int duration = 200;
	private int imgOffsetx, imgOffsety;
	private int scaleStyle = 0;// -1_noscale; 0_wrapscale; 1_fullfill;
	Status cStat = null;
	Interpolator interpolator;
	private int timeP = 0;
	private boolean isNeedNextStep = false;

	public ImageObject2(Status status, Bitmap bitmap, int screenWidth, int screenHeight){
		this.stat = status;
		this.bm = bitmap;
		oRect = new Rect(stat.getRect());
		screenW = screenWidth;
		screenH = screenHeight;
	}
	
	public void draw(Canvas canvas, Paint paint){
		canvas.drawColor(getBackColor());
		//canvas.drawColor(Color.argb(0, 0, 0, 0));
		/*paint.setColor(Color.WHITE);
		canvas.drawLine(0, 0, screenW, screenH, paint);
		canvas.drawLine(screenW, 0, 0, screenH, paint);*/
		Camera cam = new Camera();
		cam.save();
		float s = stat.getScale();
		float[] rot = stat.getRotation();
		Rect r = stat.getRect();
		int alpha = stat.getAlpha();
		paint.setAlpha(alpha);
		if(bm != null){
			int bW = bm.getWidth();
			int bH = bm.getHeight();
			int bmsW = (int)(bW * s);
			int bmsH = (int)(bH * s);
			Matrix mat = new Matrix();
			
			cam.rotateX(rot[0]);
			cam.rotateY(rot[1]);
			cam.rotateZ(rot[2]);
			cam.translate(-bmsW >> 1, bmsH >> 1, 0);
			cam.getMatrix(mat);
	
			int sW = bmsW - bW;
			int sH = bmsH - bH;
			mat.postScale(s, s, -bmsW >> 1, -bmsH>>1);
			int tranX = r.left + ((r.width())>>1) + imgOffsetx;
			int tranY = r.top + ((r.height())>>1) + imgOffsety;
			mat.postTranslate(tranX, tranY);
			canvas.drawBitmap(bm, mat, paint);
		}
		/** Test */
		/*paint.setColor(Color.CYAN);
		int cx = r.left + ((r.width()) >> 1);
		int cy = r.top + ((r.height()) >> 1);
		canvas.drawPoint(cx - (sW >> 1), cy - (sW >> 1), paint);
		//canvas.drawLine(r.left + ((r.width())>>1), 0, (r.width())>>1, screenH, paint);
		//canvas.drawLine(0, r.top + ((r.height())>>1), screenW, r.top + ((r.height())>>1), paint);
		
		canvas.drawText("scale=" + s, 100, 100, paint);
		/** End */
		//paint.setAlpha(0x000000ff);
		cam.restore();
	}
	
	public void setTarget(Status target){
		setTarget(target, new SimpleInterpolator(200, false), true);
	}
	
	/**
	 * set the status you want to go.
	 * if force == true, it will force to go to the last target status.
	 * if interpolator is not NULL, the move step will go step rate by the given interpolator.
	 */
	public void setTarget(Status target, Interpolator interpolator, boolean force){
		if(force && this.target != null)this.stat.forceToStatus(this.target);
		this.target = target;
		this.interpolator = interpolator;
		this.duration = interpolator != null ? interpolator.duration:200;
		isNeedNextStep = true;
		//timeP = 0;
		cStat = createStatusFromCurrent();
	}
	
	/**
	 * move the object to target step by step.
	 * @param timePassed: the time passed, we will calculate the rate by timePassed/DURATION.
	 */
	public void moveToTargetStepByStep(int timePassed){
		if(target == null)return;
		ALog.alog(this.getClass().getName(), "moveToTargetStepByStep > timePassed = " + timePassed);
		//timeP = timePassed;
		float progress;// = duration == 0 ? 1f : /*(interpolator != null ? interpolator.getProgress(timePassed) : */timePassed/(float)duration/*)*/;
		if(interpolator == null){
			progress = timePassed/(float)duration;
		}else{
			progress = interpolator.getProgress(timePassed);
		}
		stat.update(progress, cStat, target);
		if(timePassed >= duration){
			isNeedNextStep = false;
		}
		if(!isNeedNextStep()){
			stat.forceToStatus(target);
			target = null;
			interpolator = null;
			cStat = null;
		}
	}
	
	/**
	 * move the object to target step by step.
	 * @param rate: the time passed, we will calculate the rate by timePassed/DURATION.

	public void moveToTargetStepByStep(float rate){
		if(target == null)return;
		float progress = rate;
		stat.update(progress, cStat, target);
		if(timeP >= duration){
			isNeedNextStep = false;
		}
		if(!isNeedNextStep()){
			stat.forceToStatus(target);
			target = null;
			cStat = null;
		}
	}	 */
	
	public boolean isNeedNextStep(){
		return isNeedNextStep && target != null;
	}
	public boolean isTouchMe(int x, int y){
		return acceptTouch && stat.getScaledRect().contains(x, y);
	}
	public void move(int dx, int dy){
		stat.getRect().offset(dx, dy);
	}
	public Status getStat() {
		return stat;
	}
	public void setStat(Status stat) {
		this.stat = stat;
	}
	public Bitmap getBm() {
		return bm;
	}
	public void setBm(Bitmap bm) {
		this.bm = bm;
	}
	public int getDuration(){
		return this.duration;
	}
	public void setDuration(int duration){
		this.duration = duration;
	}
	public int[] getScreenWH(){
		int[] hw =  {screenW, screenH};
		return hw;
	}
	public Rect getOriginRect(){
		final Rect r = new Rect(oRect);
		return r;
	}
	/**
	 * if color is Color.WHITE, it will return a translucent Color ARGB(0, 0, 0, 0).
	 * @param color
	 */
	public void setBackColor(int color){
		this.backColor = color;
	}
	public int getBackColor(){
		return AUtils.getColorWithAlpha(backColor, stat.getAlpha());
	}
	public int[] getImgOffset(){
		int[] offset = {imgOffsetx, imgOffsety};
		return offset;
	}
	public void setImgOffset(int ox, int oy){
		this.imgOffsetx = ox;
		this.imgOffsety = oy;
	}
	public Status getTarget(){
		return this.target;
	}
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return this.id;
	}
	public void restoreToDefault(){
	}
	public Status createStatusFromCurrent(){
		Status status = new Status(new Rect(stat.getRect()));
		status.setAlpha(stat.getAlpha());
		status.setScale(stat.getScale());
		float[] rot = stat.getRotation();
		status.setRotate(rot[0], rot[1], rot[2]);
		return status;
	}
	
	public Status createStatusBySpecialValues(Rect r, int alpha, float scale, float[] rot){
		Status status = new Status(r);
		status.setAlpha(alpha);
		status.setScale(scale);
		if(rot != null && rot.length == 3)
			status.setRotate(rot[0], rot[1], rot[2]);
		return status;
	}
	
	public Status createStatusBySpecialValues(int newleft, int newtop){
		Rect r = new Rect(stat.getRect());
		r.offsetTo(newleft, newtop);
		Status status = new Status(r);
		status.setAlpha(stat.getAlpha());
		status.setScale(stat.getScale());
		float[] rot = stat.getRotation();
		status.setRotate(rot[0], rot[1], rot[2]);
		return status;
	}
	public abstract void onClicked();
	public abstract void onLongClicked();
	/**
	 * onActionDown: called when this object is acceptTouch == true;
	 * and user touch down
	 * @param e
	 */
	public abstract void onActionDown(MotionEvent e);
	/**
	 * onActionDown: called when this object is acceptTouch == true;
	 * and user touch move after touch down.
	 * if you want to make some change of the object's status, you can implements this method.
	 * @param e
	 */
	public abstract void onActionMove(MotionEvent e, int dx, int dy);
	/**
	 * onActionDown: called when this object is acceptTouch == true;
	 * and user touch up
	 * @param e
	 */
	public abstract void onActionUp(MotionEvent e);
	
	public void recycle(){
		if(bm != null) bm.recycle();
		stat.recycle();
	}
	
	boolean acceptTouch = true;
	/**
	 * if set to false, this object will not receive all the touch event.
	 * @param accept
	 */
	public void setAcceptTouch(boolean accept){
		this.acceptTouch = accept;
	}
	
	/**
	 * we may change our bitmap to change this object.
	 * @param bitmap
	 */
	protected void updateBitmap(Bitmap bitmap) {
		this.bm = bitmap;
	}
	public interface ActionEventListener{
		public void onClicked(int id);
		public void onLongClicked(int id);
		public void onActionDown(MotionEvent e);
		public void onActionMove(MotionEvent e, int dx, int dy);
		public void onActionUp(MotionEvent e);
	}
}
