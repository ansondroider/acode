package com.anson.acode.view.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anson on 18-1-14.
 * Shape View for custom draw shapes view.
 */

public abstract class ShapeView extends AView {
    protected List<Element> elements = new ArrayList<Element>();

    public ShapeView(Context context) {
        super(context);
    }

    public ShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(Element e : elements){
            if(!e.active)continue;
            e.draw(canvas, mPaint);
        }
    }

    /**
     * class Element.
     * every active element should be draw onto view.
     *  View : ELement ==> 1 : N
     */
    protected abstract class Element{
        protected int width, height;
        boolean active = true;
        public Element(int width, int height){
            this.width = width;
            this.height = height;
        }

        public void setActive(boolean active){
            this.active = active;
        }
        protected abstract void draw(Canvas canvas, Paint paint);
    }

    /**
     * class BitmapElement
     * some complex shape we draw it to a Bitmap once time.
     * then draw bitmap to View when call draw.
     */
    protected abstract class BitmapElement extends Element{

        int mBmLeft, mBmTop;
        Bitmap mBm;
        Canvas mBmCanvas;
        Paint mBmPaint;
        BitmapElement(int width, int height){
            super(width, height);
            mBm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mBmCanvas = new Canvas(mBm);
            mBmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            initBm();
        }

        public Bitmap getBitmap(){
            return mBm;
        }

        abstract void initBm();

        //draw bitmap in center.
        void drawDefault(Canvas c, Paint p){
            c.drawBitmap(mBm, mBmLeft, mBmTop, p);
        }
        void release(){
            if(mBm != null && !mBm.isRecycled()){
                mBm.recycle();
                mBm = null;
            }
        }
    }
}
