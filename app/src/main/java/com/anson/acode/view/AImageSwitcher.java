package com.anson.acode.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.anson.acode.ALog;
import com.anson.acode.AUtils;
import com.anson.acode.BitmapUtils;
import com.anson.acode.R;

import java.util.Hashtable;
import java.util.Random;

/**
 * Created by anson on 17-3-28.
 * switch with animation.
 */

public class AImageSwitcher extends RelativeLayout {
    Bitmap defBm;
    private int animDuration = 300;
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public AImageSwitcher(Context context) {
        super(context);
        w = getResources().getDisplayMetrics().widthPixels;
        h = getResources().getDisplayMetrics().heightPixels;
        defBm = android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.thumb);
        textPaint.setColor(Color.WHITE);
    }

    public AImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        w = getResources().getDisplayMetrics().widthPixels;
        h = getResources().getDisplayMetrics().heightPixels;
        defBm = android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.thumb);
        textPaint.setColor(Color.WHITE);
    }

    public AImageSwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        w = getResources().getDisplayMetrics().widthPixels;
        h = getResources().getDisplayMetrics().heightPixels;
        defBm = android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.thumb);
        textPaint.setColor(Color.WHITE);
    }

    public void setAnimDuration(int duration){
        animDuration = duration;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(child instanceof ImageView) {
            super.addView(child, index, params);
        }else{
            throw new RuntimeException("child View MUST instance of ImageView");
        }
    }

    ImageView ivPre, ivCur;
    private int curIdx = 0;
    Hashtable<String, Bitmap> bitmaps = new Hashtable<String, Bitmap>();
    private boolean isLoading = false;

    public int showImage(String file){
        int idx = adapter.getIndexOfFile(file);
        curIdx = showImage(idx);
        return curIdx;
    }
    public int showImage(int index){
        if(isLoading){
            android.util.Log.d("AImageSwitcher", "ALog showImage failed for loading");
            return curIdx;
        }
        isLoading = true;
        curIdx = index;
        LayoutParams par = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(ivCur != null){
            ivPre = ivCur;
            ivCur = null;
        }

        ivCur = new ImageView(getContext());
        ivCur.setTag(adapter.getImageFile(index));
        ivCur.setAlpha(0f);
        addView(ivCur, par);


        loadBitmapForView();
        return index;
    }

    int w, h;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        textPaint.setTextSize(h / 20);
    }


    AImageSwitcherAdapter adapter = null;
    public void setImageAdapter(AImageSwitcherAdapter adapter){
        this.adapter = adapter;
    }

    private void loadBitmapForView(){
            new AsyncTask<Integer, Integer, Integer>(){
                @Override
                protected Integer doInBackground(Integer... params) {
                    String curPath = adapter.getImageFile(curIdx);
                    if(bitmaps.get(curPath) == null){
                        Bitmap bm = BitmapUtils.decodeScaledBitmapKeepWHRateBySpecWH(curPath, w, h);
                        if(bm == null){
                            bm = defBm;
                        }
                        bitmaps.put(curPath, bm);
                    }
                    isWaittingAnimation = true;
                    //Bitmap is ready!
                    post(bitmapReady);
                    return 0;
                }
            }.execute(0);
    }
    private boolean isWaittingAnimation = false;
    @Override
    protected void onAttachedToWindow() {
        ALog.d("AImageSwitcher.onAttachedToWindow");
        super.onAttachedToWindow();
        if(isWaittingAnimation && !(animSet != null && animSet.isStarted()))post(bitmapReady);
    }

    private Runnable bitmapReady = new Runnable() {
        @Override
        public void run() {
            if(ivCur.getTag() != null){
                String key = (String)ivCur.getTag();
                ivCur.setImageBitmap(bitmaps.get(key));
            }
            showBitmapWithAnimation();
        }
    };

    private Runnable recycle = new Runnable() {
        @Override
        public void run() {
            removeView(ivPre);
            if(ivPre.getTag() != null) {
                String key = (String) ivPre.getTag();
                if(bitmaps.get(key) != defBm) {
                    if(bitmaps.get(key) != null) {
                        bitmaps.get(key).recycle();
                    }
                }
                bitmaps.remove(key);
            }
        }
    };

    AnimatorSet animSet;
    void showBitmapWithAnimation(){
        ALog.d("showBitmapWithAnimation");
        isWaittingAnimation = false;
        if(animSet != null && animSet.isStarted()){
            animSet.cancel();
        }

        animSet = new AnimatorSet();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if(ivPre != null){
                    postDelayed(recycle, 100);
                }
                //reset pivot xy after animation.
                ivCur.setPivotX(getWidth() >> 1);
                ivCur.setPivotY(getHeight() >> 1);
                isLoading = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        animSet.setDuration(animDuration);
        Animator[] inout = getInOutAnimator();
        if(inout[0] != null) {
            animSet.playTogether(inout[0], inout[1]);
        }else{
            animSet.playTogether(inout[1]);
        }
        animSet.start();
    }

    public ImageView getCurrentImageView(){
        return ivCur;
    }

    Random r = new Random(System.currentTimeMillis());
    private Animator[] getInOutAnimator(){
        int i = r.nextInt(5);
        ObjectAnimator hide = null;
        ObjectAnimator show;

        if (i == 1) {
            if (ivPre != null) {
                hide = ObjectAnimator.ofPropertyValuesHolder(ivPre,
                        PropertyValuesHolder.ofFloat("alpha", 1f, 0f),
                        PropertyValuesHolder.ofFloat("translationX", 0, -getWidth()));
            }
            show = ObjectAnimator.ofPropertyValuesHolder(ivCur,
                    PropertyValuesHolder.ofFloat("alpha", 0f, 1f),
                    PropertyValuesHolder.ofFloat("translationX", getWidth(), 0));
        }else if (i == 2) {
            if (ivPre != null) {
                hide = ObjectAnimator.ofPropertyValuesHolder(ivPre,
                        PropertyValuesHolder.ofFloat("alpha", 1f, 0f),
                        PropertyValuesHolder.ofFloat("translationY", 0, -getHeight()));
            }
            show = ObjectAnimator.ofPropertyValuesHolder(ivCur,
                    PropertyValuesHolder.ofFloat("alpha", 0f, 1f),
                    PropertyValuesHolder.ofFloat("translationY", getHeight(), 0));
        }else if (i == 3) {
            if (ivPre != null) {
                hide = ObjectAnimator.ofPropertyValuesHolder(ivPre,
                        PropertyValuesHolder.ofFloat("alpha", 1f, 0f),
                        PropertyValuesHolder.ofFloat("rotationY", 0, 90));
            }
            show = ObjectAnimator.ofPropertyValuesHolder(ivCur,
                    PropertyValuesHolder.ofFloat("alpha", 0f, 1f),
                    PropertyValuesHolder.ofFloat("rotationY", -90, 0));
        }else if (i == 4) {
            if (ivPre != null) {
                ivPre.setPivotX(0);
                hide = ObjectAnimator.ofPropertyValuesHolder(ivPre,
                        PropertyValuesHolder.ofFloat("alpha", 1f, 0f),
                        PropertyValuesHolder.ofFloat("scaleX", 1f, 0f));
            }
            ivCur.setPivotX(getWidth());
            show = ObjectAnimator.ofPropertyValuesHolder(ivCur,
                    PropertyValuesHolder.ofFloat("alpha", 0f, 1f),
                    PropertyValuesHolder.ofFloat("scaleX", 0f, 1f));
        }else{
            if (ivPre != null) {
                hide = ObjectAnimator.ofPropertyValuesHolder(ivPre,
                        PropertyValuesHolder.ofFloat("alpha", 1f, 0f));
            }
            show = ObjectAnimator.ofPropertyValuesHolder(ivCur,
                    PropertyValuesHolder.ofFloat("alpha", 0f, 1f));
        }

        return new Animator[]{hide, show};
    }

    int downX = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            downX = (int)event.getX();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        if(getAlpha() == 1) {
            int idx = curIdx + (downX > getWidth() / 2 ? 1 : -1);
            if(0 <= idx && idx < adapter.getCount()) {
                showImage(idx);
            }
        }
        return super.performClick();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawInfo(canvas);
    }

    void drawInfo(Canvas canvas){
        String info = (1 + curIdx) + "/" + adapter.getCount();
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(info, w/2, textPaint.getTextSize(), textPaint);
    }
}
