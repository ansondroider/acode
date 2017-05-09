package com.anson.acode.view.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * com.ansondroider.magiclauncher.views.common
 * Created by anson on 16-4-11.
 */
public class AnimationShapeArc extends ShapeArc {
    LineWithValue line;
    int duration = 200;
    View parent = null;
    public AnimationShapeArc(float radius, float startAngel, float sweep, float centerX, float centerY, Paint paint) {
        super(radius, startAngel, sweep, centerX, centerY, paint);
    }

    public AnimationShapeArc(float startAngel, float sweep, RectF oval, Paint paint) {
        super(startAngel, sweep, oval, paint);
    }

    public AnimationShapeArc(float radius, float startAngel, float sweep, RectF oval, Paint paint) {
        super(radius, startAngel, sweep, oval, paint);
    }

    public void setSweepTarget(float sweep){
        startAnimation(sweep);
    }

    public void setParentView(View v){
        parent = v;
    }

    public void setLineWithValue(LineWithValue l){
        line = l;
    }

    @Override
    public void draw(Canvas c) {
        p.setStyle(Paint.Style.STROKE);
        super.draw(c);
        if(line != null)line.draw(c);
    }

    @Override
    public void setSweep(float sweep) {
        super.setSweep(sweep);
        parent.invalidate();
    }

    float getLineSourceX(){
        if(sweep < 180) {
            return centerX - (float) (radius * Math.cos(Math.PI * sweep / 180));
        }else{
            return centerX + (float) (radius * Math.cos(Math.PI * sweep / 180));
        }
    }

    public float getSweep(){
        return sweep;
    }

    public boolean isAnimating(){
        return animating;
    }

    float getLineSourceY(){
        return centerY - (float)(radius * Math.sin(Math.PI * sweep /180));
    }

    boolean animating = false;
    void startAnimation(float target){
        PropertyValuesHolder proSweep = PropertyValuesHolder.ofFloat("Sweep", this.sweep, target);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(this, proSweep);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        anim.setDuration(duration);
        anim.start();
    }
}