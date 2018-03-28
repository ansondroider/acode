package com.anson.acode.view.common;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.anson.acode.ALog;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * com.ansondroider.magiclauncher.views
 * Created by anson on 16-3-30.
 */
public abstract class AView extends View {
    protected String TAG = "ShapeView";
    protected boolean needTouch = false;
    protected boolean debugLife = false;
    protected float density = 1;
    protected boolean animSelf = false;
    public AView(Context context) {
        super(context);
        constructed();
    }

    public AView(Context context, AttributeSet attrs) {
        super(context, attrs);
        constructed();
    }

    public AView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructed();
    }
    protected void constructed(){
        TAG = getClass().getSimpleName();
        if(debugLife)Log.i(TAG, "LIFE: constructed");
    }

    protected int W = 0, H = 0, realW = 0, realH = 0, oldW = -1, oldH = -1;
    protected int paddingLeft, paddingTop, paddingRight, paddingBottom;
    protected boolean inited = false;
    protected boolean debugMeasure = false;
    protected Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(debugLife)Log.i(TAG, "LIFE: onMeasure");
                //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int mw = MeasureSpec.getMode(widthMeasureSpec);
        int mh = MeasureSpec.getMode(heightMeasureSpec);

        String modeW = mw == MeasureSpec.EXACTLY ? "MeasureSpec.EXACTLY" :
                (mw == MeasureSpec.AT_MOST ? "MeasureSpec.AT_MOST" : " MeasureSpec.UNSPECIFIED");
        String modeH = mh == MeasureSpec.EXACTLY ? "MeasureSpec.EXACTLY" :
                (mh == MeasureSpec.AT_MOST ? "MeasureSpec.AT_MOST" : " MeasureSpec.UNSPECIFIED");
        if(debugMeasure) ALog.d(TAG, "LIFE: onMeasure(" + modeW + "," + modeH + ", " + w + ", " + h + ")");

        if(mw == MeasureSpec.EXACTLY || mh == MeasureSpec.EXACTLY){
            W = mw == MeasureSpec.EXACTLY ? w : Math.min(w, h);
            H = mh == MeasureSpec.EXACTLY ? h : Math.min(w, h);
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(W, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(H, MeasureSpec.EXACTLY));
        }else if(mw == MeasureSpec.UNSPECIFIED || mh == MeasureSpec.UNSPECIFIED){
            H = W = Math.min(w, h);
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(W, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(H, MeasureSpec.EXACTLY));
        }else{
            W = w;
            H = h;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * should do some recyle() and reset value here...
     */
    protected void reset(){
        inited = false;
    }

    public void setNeedTouch(boolean need){
        needTouch = need;
    }

    OnClickListener mOnClickListener = null;
    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
        super.setOnClickListener(l);
    }

    protected float centerX, centerY;
    protected abstract void init();

    protected float dx, dy;
    protected float rdx, rdy;
    protected float rcx, rcy;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //AUtils.d(TAG, "onTouchEvent " + getEventStr(event.getAction()));

        boolean spend = needTouch;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = event.getX();
                dy = event.getY();
                rdx = event.getRawX();
                rdy = event.getRawY();
                spend = touchDown(dx, dy);
                break;
            case MotionEvent.ACTION_MOVE:
                float cx = event.getX();
                float cy = event.getY();
                rcx = event.getRawX();
                rcy = event.getRawY();
                spend = touchMove(cx, cy);
                break;
            case MotionEvent.ACTION_UP:
                rcx = event.getRawX();
                rcy = event.getRawY();
                touchUp();
                break;
            case MotionEvent.ACTION_CANCEL:
                rcx = event.getRawX();
                rcy = event.getRawY();
                touchCancel();
                break;
        }
        if(mOnClickListener != null){
            return super.onTouchEvent(event);
        }

        invalidate();
        return spend;
    }

    protected abstract boolean touchDown(float downX, float downY);
    protected abstract boolean touchMove(float currentX, float currentY);
    protected abstract void touchUp();
    protected abstract void touchCancel();

    protected boolean isLongClick = false;
    protected boolean clickable = false;
    protected int LONG_CLICK = 250;
    protected Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            isLongClick = true;
            performLongClick();
        }
    };


    protected String getEventStr(int action){
        if(action == MotionEvent.ACTION_DOWN){
            return "ACTION_DOWN";
        }else if(action == MotionEvent.ACTION_MOVE){
            return "ACTION_MOVE";
        }else if(action == MotionEvent.ACTION_UP){
            return "ACTION_UP";
        }else if(action == MotionEvent.ACTION_CANCEL){
        return "ACTION_CANCEL";
        }else{
            return "UNKNOW " + action;
        }
    }

    protected OnTouchCallback tcb = null;
    public void setOnTouchCallback(OnTouchCallback cb){
        tcb = cb;
    }


    Camera mCamera = new Camera();
    Matrix matrix = new Matrix();
    public void advanceRotate(Canvas canvas, float x, float y, float z){
        advanceRotate(canvas, centerX, centerY, x, y, z);
    }
    public void advanceRotate(Canvas canvas, float cx , float cy, float x, float y, float z){
        mCamera.save();
        if(x != 0)mCamera.rotateX(x);
        if(y != 0)mCamera.rotateY(y);
        if(z != 0)mCamera.rotateZ(z);
        mCamera.getMatrix(matrix);
        mCamera.restore();
        matrix.preTranslate(-cx, -cy);
        matrix.postTranslate(cx, cy);
        canvas.concat(matrix);
    }

    public void advanceScale(Canvas canvas, float sx, float sy){
        advanceScale(canvas, centerX, centerY, sx, sy);
    }
    public void advanceScale(Canvas canvas, float cx , float cy, float sx, float sy){
        mCamera.save();
        mCamera.getMatrix(matrix);
        matrix.postScale(sx, sy, cx, cy);
        mCamera.restore();
        //matrix.preTranslate(-cx, -cy);
        //matrix.postTranslate(cx, cy);

        canvas.concat(matrix);
    }

    public void setAnimSelf(boolean animSelf) {
        this.animSelf = animSelf;
        invalidate();
    }

    protected float getTextLength(String text, Paint p){
        if(text != null){
            float[] ls = new float[text.length()];
            p.getTextWidths(text, ls);
            float l = 0;
            for(float f : ls){
                l += f;
            }
            return l;
        }
        return 0;
    }
    public boolean getAnimSelf(){
        return this.animSelf;
    }

    protected boolean outOfClickPosition(float cx, float cy){
        return Math.abs(cx - dx) + Math.abs(cy - dy) > density * 5;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(debugLife)Log.i(TAG, "LIFE: onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        if(oldW != w || oldH != h){
            reset();
            density = getResources().getDisplayMetrics().density;
            centerX = W >> 1;
            centerY = H >> 1;
            realW = W - paddingLeft - paddingRight;
            realH = H - paddingTop - paddingBottom;
            init();
            oldW = W;
            oldH = H;
            inited = true;
            onInited();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(debugLife)Log.i(TAG, "LIFE: onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(debugLife)Log.i(TAG, "LIFE: onDetachedFromWindow");
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(debugLife)Log.i(TAG, "LIFE: onVisibilityChanged " + visibility);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(debugLife)Log.i(TAG, "LIFE: onLayout");
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(debugLife)Log.i(TAG, "LIFE: onWindowFocusChanged " + hasWindowFocus);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(debugLife)Log.i(TAG, "LIFE: onFinishInflate");
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        super.onWindowSystemUiVisibilityChanged(visible);
        if(debugLife)Log.i(TAG, "LIFE: onWindowSystemUiVisibilityChanged");
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(debugLife)Log.i(TAG, "LIFE: onConfigurationChanged");
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        if(debugLife)Log.i(TAG, "LIFE: onSaveInstanceState");
        return super.onSaveInstanceState();
    }

    ConcurrentLinkedQueue<Runnable> eventQueue = new ConcurrentLinkedQueue<Runnable>();


    protected void queue(Runnable event){
        if(!inited) {
            eventQueue.add(event);
        }else{
            post(event);
        }
    }

    void onInited(){
        if(debugLife)Log.i(TAG, "LIFE: onInited");
        if(eventQueue.size() > 0){
            Runnable event = eventQueue.poll();
            post(event);
            onInited();
        }
    }

    void flushEvents(){
        eventQueue.clear();
    }
}
