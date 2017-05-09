package com.anson.acode.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.anson.acode.R;

public class CheckImageView extends ImageView {
	int state = 0;// normal 0, selected 1, checked 2;
	public static final int STATE_NORMAL = 0;
	public static final int STATE_SELECTED = 1;
	public static final int STATE_CHECKED = 2;
	int idx = 0;
	public CheckImageView(Context context) {
		super(context);
	}
	public CheckImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public CheckImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    @Override
    public boolean performClick() {
        switchStateTo(state == STATE_NORMAL ? STATE_CHECKED : STATE_NORMAL);
        return super.performClick();
    }

    public void switchStateTo(int stat){
		this.state = stat;
		updateImage(stat);
	}
	public int getState(){
		return this.state;
	}
	void updateImage(int stat){
		setImageResource(STATE_NORMAL == stat ? R.drawable.rate_off: (STATE_SELECTED == state ? R.drawable.rate_wait:R.drawable.rate_on));
	}
	
	public void setIndex(int i){
		this.idx = i;
	}
	public int getIndex(){
		return this.idx;
	}

}
