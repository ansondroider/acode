package com.anson.acode.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Hashtable;

/**
 * Created by anson on 17-6-29.
 * test Touch points.
 */

public class TouchView extends View {
    public TouchView(Context context) {
        super(context);
    }

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    int count = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        count = event.getPointerCount();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                updatePoints(event);
                break;
            case MotionEvent.ACTION_MOVE:
                updatePoints(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (count == 1) count = 0;
                updatePoints(event);
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Point p : points.values()) {
            if (p.active) p.draw(canvas, paint);
        }
    }

    Hashtable<Integer, Point> points = new Hashtable<Integer, Point>();

    void updatePoints(MotionEvent e) {
        for (Point p : points.values()) {
            p.active = false;
        }
        for (int i = 0; i < count; i++) {
            int pid = e.getPointerId(i);
            float x = e.getX(i);
            float y = e.getY(i);
            if (points.containsKey(pid)) {
                Point p = points.get(pid);
                p.set(x, y);
                p.active = true;
            } else {
                Point p = new Point(i);
                p.set(x, y);
                points.put(pid, p);
            }
        }
    }

    private class Point {
        int pointId;
        boolean active = false;
        float x, y;
        float[] lines = new float[4 * 2];

        Point(int idx) {
            pointId = idx;
            active = true;
        }

        void set(float px, float py) {
            x = px;
            y = py;
            lines[0] = 0;
            lines[1] = y;
            lines[2] = getWidth();
            lines[3] = y;

            lines[4] = x;
            lines[5] = 0;
            lines[6] = x;
            lines[7] = getHeight();

        }

        void draw(Canvas c, Paint p) {
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.GREEN);
            c.drawCircle(x, y, 16, p);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            p.setColor(Color.CYAN);
            c.drawCircle(x, y, 72, p);

            p.setColor(Color.YELLOW);
            c.drawLines(lines, p);
        }
    }
}

