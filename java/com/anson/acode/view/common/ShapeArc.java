package com.anson.acode.view.common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

/**
 * com.ansondroider.magiclauncher.views
 * Created by anson on 16-3-29.
 */
public class ShapeArc {
    final String TAG = "ShapeArc";

    public static final int MODE_NORMAL = 0;
    public static final int MODE_ROTATE = 1;
    public static final int MODE_SWEEP = 2;

    private int mode = MODE_NORMAL;
    public final float default_stroke_width = 6;
    float radius = 0f;
    protected float startAngel;
    protected int color = Color.CYAN;
    protected float sweep = 0;
    Paint p;
    RectF area;
    float offsetAngel;
    float offset = 0;
    int alpha = 255;
    float strokeW = default_stroke_width;
    float centerX, centerY;
    float shortRadius, longRadius;

    float sx, sy, lx, ly;

    public ShapeArc(float radius, float startAngel, float sweep, RectF oval, Paint paint) {
        this.startAngel = startAngel;
        this.radius = radius;
        this.area = oval;
        centerX = area.centerX();
        centerY = area.centerY();
        this.sweep = sweep;
        this.p = paint;
        realStartAngel = (offsetAngel + startAngel + 90) % 360;
        realEndAngel = (realStartAngel + sweep) % 360;
    }

    public ShapeArc(float radius, float startAngel, float sweep, float centerX, float centerY, Paint paint) {
        this.startAngel = startAngel;
        this.radius = radius;
        this.area = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        this.centerX = centerX;
        this.centerY = centerY;
        this.sweep = sweep;
        this.p = paint;
        realStartAngel = (offsetAngel + startAngel + 90) % 360;
        realEndAngel = (realStartAngel + sweep) % 360;
    }

    public ShapeArc(float startAngel, float sweep, RectF oval, Paint paint) {
        this.startAngel = startAngel;
        this.radius = area.width()/2;
        this.area = oval;
        centerX = area.centerX();
        centerY = area.centerY();
        this.sweep = sweep;
        this.p = paint;
        realStartAngel = (offsetAngel + startAngel + 90) % 360;
        realEndAngel = (realStartAngel + sweep) % 360;
    }

    public void setWidth(float width) {
        this.strokeW = width;
        shortRadius = radius - width/2;
        longRadius = radius + width/2;
    }

    public void setColor(int c){
        this.color = c;
    }

    public void setAutoRotateOffset(float offset) {
        this.offset = offset;
    }

    public void setAlpha(float a) {
        alpha = (int)(255 * a);
    }

    public void setAlpha(int a) {
        alpha = a;
    }

    private void setOffsetAngel(float degree){
        offsetAngel = degree;
        realStartAngel = (offsetAngel + startAngel + 90) % 360;
        realEndAngel = (realStartAngel + sweep) % 360;
    }

    public void setSweep(float sweep){
        this.sweep = sweep;
        realEndAngel = (realStartAngel + sweep) % 360;
        refresh();
    }

    public void setMode(int mode){
        this.mode = mode;
    }

    float realStartAngel = 0;
    float realEndAngel = 0;
    public void refresh() {
        offsetAngel += offset;
        //offsetAngel = offsetAngel % 360;
        realStartAngel = (offsetAngel + startAngel + 90) % 360;
        realEndAngel = (realStartAngel + sweep) % 360;
    }

    float downDegree = 0;
    float downOffsetAngel = 0;
    float downSweep = 0;
    public boolean touchMe(float x, float y) {
        return touchMe(x, y, false);
    }

    public boolean touchMe(float x, float y, boolean degreeOnly) {

        float dx = x - centerX;//Math.abs(x - area.centerX());
        float dy = y - centerY;//Math.abs(y - area.centerY());
        float adx = dx < 0 ? -dx : dx;
        float ady = dy < 0 ? -dy : dy;

        float lenTouch = (float)Math.sqrt(adx * adx + ady * ady);

        float sin = dx / lenTouch;
        float cos = dy / lenTouch;

        sx = centerX + (shortRadius * sin);
        sy = centerY + (shortRadius * cos);

        lx = centerX + (longRadius * sin);
        ly = centerY + (longRadius * cos);

        float maxX = Math.max(sx, lx);
        float minX = Math.min(sx, lx);
        float maxY = Math.max(sy, ly);
        float minY = Math.min(sy, ly);

        float degree = (float)Math.toDegrees(Math.asin(sin));
        if(dy > 0){
            degree = 180 - degree;
        }else{
            if(dx < 0){
                degree = 360 + degree;
            }
        }
        //Log.d(TAG, String.format("realStart= %f, realEnd = %f, degree=%f", realStartAngel, realEndAngel,  degree));

        boolean inDegree = false;
        if(realEndAngel < realStartAngel){
            inDegree = (realStartAngel <= degree && degree <= 360)
                    || (0 <= degree && degree <= realEndAngel);
        }else{
            inDegree = realStartAngel <= degree && degree <= realEndAngel;
        }
        boolean touchme = (degreeOnly || (minX <= x && x <= maxX
                && minY <= y && y <= maxY))
                && inDegree;
        if(touchme){
            downDegree = degree;
            downOffsetAngel = offsetAngel;
            downSweep = sweep;
        }
        //Log.d(TAG, "touchMe " + touchme + ", degree = " + degree);
        return touchme;
    }

    public void move(float cx, float cy){
        float dx = cx - centerX;//Math.abs(x - area.centerX());
        float dy = cy - centerY;//Math.abs(y - area.centerY());
        float adx = dx < 0 ? -dx : dx;
        float ady = dy < 0 ? -dy : dy;

        float lenTouch = (float)Math.sqrt(adx * adx + ady * ady);
        float sin = dx / lenTouch;
        float degree = (float)Math.toDegrees(Math.asin(sin));
        if(dy > 0){
            degree = 180 - degree;
        }else{
            if(dx < 0){
                degree = 360 + degree;
            }
        }
        if(mode == MODE_ROTATE)setOffsetAngel(downOffsetAngel + degree - downDegree);
        else if(mode == MODE_SWEEP)setSweep(downSweep + degree - downDegree);
        //Log.d(TAG, "move degree to " + degree);
    }

    boolean D = false;
    public void draw(Canvas c) {
        p.setStrokeWidth(strokeW);
        p.setColor(color);
        p.setAlpha(alpha);
        if(D)p.setColor(Color.CYAN);
        c.drawArc(area, offsetAngel + startAngel, sweep, false, p);
        if(D) {
            p.setColor(Color.RED);

            p.setStrokeWidth(3);
            c.drawPoint(centerX, centerY, p);
            c.drawLine(sx, sy, lx, ly, p);
            //c.drawLine(centerX, centerY, centerX - shortRadius, centerY, p);
        }
    }
}
