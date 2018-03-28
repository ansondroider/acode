package com.anson.acode.view.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.anson.acode.AnimationHelper;

/**
 * Created by anson on 17-6-16.
 * AImageView was extends ImageView.
 * 1. show Image with Animator.
 * 2. adjust Image Size to Height and Width
 * 3. support scale.
 */

public class AImageView extends ImageView {
    public AImageView(Context context) {
        super(context);
        init();
    }

    public AImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    Animator animShow, animHide;
    void init(){
        Animator alphaS = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat(AnimationHelper.alpha, 0f, 1f));
        Animator alphaH = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat(AnimationHelper.alpha, 1f, 0f));

        Animator scaleS = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat(AnimationHelper.scaleX, 0f, 1f),
                PropertyValuesHolder.ofFloat(AnimationHelper.scaleY, 0f, 1f));
        Animator scaleH = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat(AnimationHelper.scaleX, 1f, 0f),
                PropertyValuesHolder.ofFloat(AnimationHelper.scaleY, 1f, 0f));
        animHide = alphaH;
        animShow = alphaS;
        animHide.setDuration(300);
        animShow.setDuration(300);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Bitmap curBm = ((BitmapDrawable)getDrawable()).getBitmap();
        super.setImageBitmap(bm);
        if(curBm != bm){
            animShow.start();
        }
    }

}
