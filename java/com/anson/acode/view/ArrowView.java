package com.anson.acode.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.anson.acode.R;

/**
 * Created by anson on 17-7-29.
 * arrow view
 */

public class ArrowView extends View {

    float originTranslationX = 0;
    public ArrowView(Context context) {
        super(context);
        arrow = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.arrow));
        originTranslationX = getTranslationX();
    }

    public ArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        arrow = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.arrow));
        originTranslationX = getTranslationX();
        if(attrs != null) {
            TypedArray attributesArray = getContext().obtainStyledAttributes(
                    attrs, R.styleable.ArrowView, 0, 0);
            isRight = attributesArray.getBoolean(R.styleable.ArrowView_isRight, false);
            attributesArray.recycle();
        }
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        arrow = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.arrow));
        originTranslationX = getTranslationX();
        if(attrs != null) {
            TypedArray attributesArray = getContext().obtainStyledAttributes(
                    attrs, R.styleable.ArrowView, 0, 0);
            isRight = attributesArray.getBoolean(R.styleable.ArrowView_isRight, false);
            attributesArray.recycle();
        }
    }

    boolean isRight;
    BitmapDrawable arrow;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    int colorBg = 0x44000000;
    Path pathBg = new Path();
    void init(){
        paint.setColor(colorBg);
        paint.setStyle(Paint.Style.FILL);
        pathBg.reset();
        if(isRight){
            RectF oval = new RectF(0, 0, H, H);
            pathBg.moveTo(W, 0);
            pathBg.lineTo(W, H);
            pathBg.lineTo(H/2, H);
            pathBg.lineTo(H/2, 0);
            pathBg.lineTo(W, 0);
            pathBg.addArc(oval, 90, 180);
            pathBg.close();
            arrow.setBounds(0, 0, H, H);
        }else{
            RectF oval = new RectF(W - H, 0, W, H);
            pathBg.moveTo(0, 0);
            pathBg.lineTo(0, H);
            pathBg.lineTo(W - H/2, H);
            pathBg.lineTo(W - H/2, 0);
            pathBg.lineTo(0, 0);
            pathBg.addArc(oval, 90, -180);
            pathBg.close();
            arrow.setBounds(W - H, 0, W, H);
        }
    }

    int W, H = 0;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w != W || h != H) {
            W = w;
            H = h;
            init();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw bg;
        canvas.drawPath(pathBg, paint);
        if(isRight){
            int layer = canvas.saveLayer(0, 0, H, H, null, 0);
            canvas.rotate(180, H/2, H/2);
            arrow.draw(canvas);
            canvas.restoreToCount(layer);
        }else {
            arrow.draw(canvas);
        }
    }

    float dx, dy, cx, cy;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        cx = event.getRawX();
        cy = event.getRawY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                dx = cx;
                dy = cy;
                break;
            case MotionEvent.ACTION_MOVE:
                onMove();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onRelease();
                break;
        }
        postInvalidate();
        return true;
    }

    void onMove(){
        float disX = cx - dx;
        float tx = originTranslationX + disX;

        setTranslationX(isRight ?
                (tx > 0 ? tx : 0) :
                (tx > 0 ? 0 : tx));
    }

    void onRelease(){
        float tx = getTranslationX();
        if((isRight && tx < originTranslationX / 3) || (!isRight && tx > originTranslationX / 3)){
            if(trigger != null)trigger.onTrigger(isRight);
        }
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("translationX", getTranslationX(), originTranslationX));
        anim.setDuration(500);
        anim.start();
    }

    OnTriggerListener trigger;
    public void setOnTriggerListener(OnTriggerListener lis){
        trigger = lis;
    }
    public interface OnTriggerListener{
        void onTrigger(boolean right);
    }
}
