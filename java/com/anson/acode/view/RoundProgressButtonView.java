package com.anson.acode.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.anson.acode.view.common.AView;

/**
 * Created by anson on 16-5-31.
 * RoundProgressView
 */
public class RoundProgressButtonView extends AView{
    public RoundProgressButtonView(Context context) {
        super(context);
    }

    public RoundProgressButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundProgressButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float radius_progress;
    float sw_progress;
    int col_progress = Color.GREEN;
    int col_progressBG = Color.GRAY & 0x44FFFFFF;
    int col_btn = Color.CYAN;
    RectF oval;
    float progressValue = 0; //0 - 1f;
    int maxValue = 100;
    float progress = 0;
    boolean going = false;
    Rect[] pauseRect = new Rect[2];
    Path play;
    @Override
    protected void init() {
        setNeedTouch(true);
        sw_progress = W / 10;
        radius_progress = realW/2 - sw_progress/2;
        oval = new RectF(centerX - radius_progress, centerY - radius_progress,
                centerX + radius_progress, centerY + radius_progress);

        int left = (int)(centerX - sw_progress * 2);
        int top = (int)(centerY - sw_progress * 2);
        int bottom = (int)(centerX + sw_progress * 2);
        pauseRect[0] = new Rect(left, top, (int)(left + sw_progress), bottom);


        int right = (int)(centerX + sw_progress * 2);

        play = new Path();
        play.moveTo(centerX - sw_progress * 1.5f, centerY - sw_progress * 2);
        play.lineTo(centerX + sw_progress * 2.5f, centerY);
        play.lineTo(centerX - sw_progress * 1.5f, centerY + sw_progress * 2);
        play.close();

        pauseRect[1] = new Rect((int)(right - sw_progress), top, right, bottom);
    }

    public void setMaxValue(int max){
        maxValue = max;
    }

    public void setProgress(float progress){
        this.progress = progress;
        progressValue = progress / maxValue;
        invalidate();
    }
    public void setProgress(int progress){
        setProgress((float)progress);
    }

    public void started(){
        going = true;
        invalidate();
    }

    public void stoped(){
        going = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw circle;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(sw_progress);
        mPaint.setColor(col_progressBG);
        float sweep = 360 * progressValue;
        canvas.drawArc(oval, sweep - 90, 360 - sweep, false, mPaint);
        mPaint.setColor(col_progress);
        canvas.drawArc(oval, -90, sweep, false, mPaint);

        //draw button.;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(col_btn);
        if(going){
            canvas.drawRect(pauseRect[0], mPaint);
            canvas.drawRect(pauseRect[1], mPaint);
        }else{
            canvas.drawPath(play, mPaint);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled){
            col_progress = Color.GREEN;
            col_progressBG = Color.GRAY & 0x44FFFFFF;
            col_btn = Color.CYAN;
        }else{
            col_progress = Color.GREEN  & 0x44FFFFFF;
            col_progressBG = Color.GRAY & 0x22FFFFFF;
            col_btn = Color.CYAN & 0x44FFFFFF;
        }
        invalidate();
    }

    @Override
    protected boolean touchDown(float downX, float downY) {
        clickable = true;
        isLongClick = false;
        postDelayed(longClickRunnable, LONG_CLICK);
        return true;
    }

    @Override
    protected boolean touchMove(float currentX, float currentY) {
        if(clickable){
            if(outOfClickPosition(currentX, currentY)){
                clickable = false;
            }
        }
        return true;
    }

    @Override
    protected void touchUp() {
        if(!isLongClick && clickable){
            removeCallbacks(longClickRunnable);
            //performClick();
        }
    }

    @Override
    public boolean performClick() {
        if(going){
            stoped();
        }else{
            started();
        }
        return super.performClick();
    }

    @Override
    protected void touchCancel() {
        clickable = false;
        removeCallbacks(longClickRunnable);
    }

}
