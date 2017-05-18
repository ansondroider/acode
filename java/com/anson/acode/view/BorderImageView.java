package com.anson.acode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.anson.acode.R;

/**
 * Created by anson on 15-10-4.
 */
public class BorderImageView extends ImageView {
    Paint borderPaint;

    public BorderImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init(attrs);
    }

    public BorderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(attrs);
    }

    public BorderImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(null);
    }

    float round = 20.0f;
    float borderWidth = 0;
    int borderColor = 0;
    void init(AttributeSet attrs) {
        if(attrs != null){
            TypedArray attributesArray = getContext().obtainStyledAttributes(
                    attrs, R.styleable.BorderImageView, 0, 0);
            round = attributesArray.getDimension(R.styleable.BorderImageView_round, 20);
            borderWidth = attributesArray.getDimension(R.styleable.BorderImageView_borderWidth, 0);
            borderColor = attributesArray.getColor(R.styleable.BorderImageView_borderColor, Color.TRANSPARENT);
        }
        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setAntiAlias(true);
        //borderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        //int max = Math.max(w, h);
        //setMeasuredDimension(max, max);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        Path clipPath = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        clipPath.addRoundRect(new RectF(0, 0, w, h), round, round, Path.Direction.CW);
        canvas.clipPath(clipPath);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
        super.onDraw(canvas);
        float hb = borderWidth/2;
        canvas.drawRoundRect(new RectF(0+hb, 0+hb, w-hb, h-hb), round, round, borderPaint);

    }
}