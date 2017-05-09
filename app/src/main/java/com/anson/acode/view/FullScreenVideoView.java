package com.anson.acode.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by anson on 15-9-22.
 */
public class FullScreenVideoView extends VideoView {
    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = getDefaultSize(0, widthMeasureSpec);
        int h = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }
}
