package com.anson.acode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.anson.acode.ALog;
import com.anson.acode.StringUtils;
import com.anson.acode.R;

import java.lang.ref.WeakReference;

/**
 * Created by anson on 15-10-3.
 * Wave View for loading..
 */
public class ProgressWaveView extends View {
    String TAG = "ProgressWaveView";
    public ProgressWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public ProgressWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ProgressWaveView(Context context) {
        super(context);
        init(null);
    }

    Paint paint;
    Paint paintLine;
    Paint paintText;
    Path wavePath, wavePath2;
    int waveColor = 0;
    void init(AttributeSet attrs){

        if(attrs != null){
            TypedArray attributesArray = getContext().obtainStyledAttributes(
                    attrs, R.styleable.ProgressWaveView, 0, 0);
            waveColor = attributesArray.getColor(R.styleable.ProgressWaveView_color,
                    getResources().getColor(R.color.water));
            showText = attributesArray.getBoolean(R.styleable.ProgressWaveView_showText,
                    true);
            attributesArray.recycle();
        }else{
            waveColor = getResources().getColor(R.color.water);
        }

        paint = new Paint();
        paint.setColor(waveColor);
        paint.setAntiAlias(true);
        //paint.setStyle(Style.STROKE);
        wavePath = new Path();

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(1);
        paintLine.setColor(Color.RED);

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setStrokeWidth(1);
        paintText.setColor(Color.CYAN);

        wavePath2 = new Path();
        wavePath2.moveTo(0, 0);

        rh = new RefreshHandler(Looper.getMainLooper(), this);
        refreshView();
    }


    public static final int REFRESH = 0;
    RefreshHandler rh;
    static class RefreshHandler extends Handler{
        WeakReference<ProgressWaveView> v;
        RefreshHandler(Looper loop, ProgressWaveView pwv){
            super(loop);
            v = new WeakReference<ProgressWaveView>(pwv);
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case REFRESH:
                        if(v.get() != null) {
                            v.get().offsetX += v.get().offsetXUnit;
                            v.get().offsetX = v.get().offsetX > v.get().width ? 0 : v.get().offsetX;
                            ////int p = progress + 1;
                            ////v.get().setProgress(p);
                            v.get().refreshView();
                        }
                    break;
            }
        }
    }

    int width, height;
    int HW, HH;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        HW = width >> 1;
        HH = height >> 1;
        paintText.setTextSize(HW /3);
        offsetXUnit = width / 32;
        waveHeight = height / 10;
        setProgress(progress);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility != View.VISIBLE){
            rh.removeMessages(REFRESH);
        }else{
            setProgress(progress);
            rh.sendEmptyMessage(REFRESH);
        }
    }

    boolean drawReference = false;
    @Override
    protected void onDraw(Canvas canvas) {
        //ALog.d(TAG, "onDraw");
        canvas.drawPath(wavePath, paint);

        if(drawReference) {
            canvas.drawLine(0, HH, width, HH, paintLine);
            canvas.drawLine(HW, 0, HW, height, paintLine);
        }

        if(showText){
            String text = progress + "%";
            int tw = StringUtils.getTextWidth(text, paintText);
            canvas.drawText(text, HW - tw/2, HH, paintText);
        }

    }

    private int progress = 0;
    public void setProgress(int progress){

        this.progress = Math.min(100, progress);
        offsetY = progress * height/100;
    }

    boolean showText = true;
    public void showText(boolean showText){
        this.showText = showText;
    }

    float offsetX = 0;
    float offsetY = 0;
    float waveHeight = 64;
    float offsetXUnit = 6;
    void refreshView(){

        float baseHeight = height - offsetY + waveHeight/2;
        float startX = -width + offsetX;
        wavePath.reset();
        wavePath.moveTo(startX, height - offsetY);
        wavePath.lineTo(startX, baseHeight);

        //wave
        wavePath.quadTo(startX + HW /2, baseHeight + waveHeight, startX + HW, baseHeight);
        startX += HW;
        wavePath.quadTo(startX + HW /2, baseHeight - waveHeight, startX + HW, baseHeight);
        startX += HW;
        wavePath.quadTo(startX + HW/2, baseHeight + waveHeight, startX + HW, baseHeight);
        startX += HW;
        wavePath.quadTo(startX + HW /2, baseHeight - waveHeight, startX + HW, baseHeight);
        //end area
        wavePath.lineTo(width, height);
        wavePath.lineTo(-width, height);
        rh.removeMessages(REFRESH);
        rh.sendEmptyMessageDelayed(REFRESH, 30);
        invalidate();
    }
}
