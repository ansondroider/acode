package com.anson.acode.view.common;

/**
 * Created by anson on 15-7-17.
 */

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class HaloHelper {


    private final Paint mHolographicPaint = new Paint();
    private final Paint mBlurPaint = new Paint();
    private final Paint mErasePaint = new Paint();

    public static final int MAX_OUTER_BLUR_RADIUS;
    public static final int MIN_OUTER_BLUR_RADIUS;

    private static final BlurMaskFilter sExtraThickOuterBlurMaskFilter;
    private static final BlurMaskFilter sThickOuterBlurMaskFilter;
    private static final BlurMaskFilter sMediumOuterBlurMaskFilter;
    private static final BlurMaskFilter sThinOuterBlurMaskFilter;
    private static final BlurMaskFilter sThickInnerBlurMaskFilter;
    private static final BlurMaskFilter sExtraThickInnerBlurMaskFilter;
    private static final BlurMaskFilter sMediumInnerBlurMaskFilter;

    private static final int THICK = 0;
    private static final int MEDIUM = 1;
    private static final int EXTRA_THICK = 2;

    static {
        final float scale = 1;

        MIN_OUTER_BLUR_RADIUS = (int) (scale * 1.0f);
        MAX_OUTER_BLUR_RADIUS = (int) (scale * 12.0f);

        sExtraThickOuterBlurMaskFilter = new BlurMaskFilter(scale * 12.0f, BlurMaskFilter.Blur.OUTER);
        sThickOuterBlurMaskFilter = new BlurMaskFilter(scale * 6.0f, BlurMaskFilter.Blur.OUTER);
        sMediumOuterBlurMaskFilter = new BlurMaskFilter(scale * 2.0f, BlurMaskFilter.Blur.OUTER);
        sThinOuterBlurMaskFilter = new BlurMaskFilter(scale * 1.0f, BlurMaskFilter.Blur.OUTER);
        sExtraThickInnerBlurMaskFilter = new BlurMaskFilter(scale * 6.0f, BlurMaskFilter.Blur.NORMAL);
        sThickInnerBlurMaskFilter = new BlurMaskFilter(scale * 4.0f, BlurMaskFilter.Blur.NORMAL);
        sMediumInnerBlurMaskFilter = new BlurMaskFilter(scale * 2.0f, BlurMaskFilter.Blur.NORMAL);
    }

    public HaloHelper() {
        mHolographicPaint.setFilterBitmap(true);
        mHolographicPaint.setAntiAlias(true);
        mBlurPaint.setFilterBitmap(true);
        mBlurPaint.setAntiAlias(true);
        mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mErasePaint.setFilterBitmap(true);
        mErasePaint.setAntiAlias(true);
    }

    /**
     * Returns the interpolated holographic highlight alpha for the effect we want when scrolling
     * pages.
     */
    public static float highlightAlphaInterpolator(float r) {
        float maxAlpha = 0.6f;
        return (float) Math.pow(maxAlpha * (1.0f - r), 1.5f);
    }

    /**
     * Returns the interpolated view alpha for the effect we want when scrolling pages.
     */
    public static float viewAlphaInterpolator(float r) {
        final float pivot = 0.95f;
        if (r < pivot) {
            return (float) Math.pow(r / pivot, 1.5f);
        } else {
            return 1.0f;
        }
    }

    /**
     * Applies a more expensive and accurate outline to whatever is currently drawn in a specified
     * bitmap.
     */
    void applyExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color,
                                       int outlineColor, int thickness) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, true,
                thickness);
    }

    void applyExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color,
                                       int outlineColor, boolean clipAlpha, int thickness) {

        // We started by removing most of the alpha channel so as to ignore shadows, and
        // other types of partial transparency when defining the shape of the object
        if (clipAlpha) {
            int[] srcBuffer = new int[srcDst.getWidth() * srcDst.getHeight()];
            srcDst.getPixels(srcBuffer,
                    0, srcDst.getWidth(), 0, 0, srcDst.getWidth(), srcDst.getHeight());
            for (int i = 0; i < srcBuffer.length; i++) {
                final int alpha = srcBuffer[i] >>> 24;
                final int red = srcBuffer[i] & 0x00FF0000;
                final int green = srcBuffer[i] & 0x0000FF00;
                final int blue = srcBuffer[i] & 0x000000FF;
                if (alpha < 188 /*|| (red > 188 && green > 188 && blue > 188)*/) {
                    srcBuffer[i] = 0;
                }
            }
            srcDst.setPixels(srcBuffer,
                    0, srcDst.getWidth(), 0, 0, srcDst.getWidth(), srcDst.getHeight());
        }
        Bitmap glowShape = srcDst.extractAlpha();

        // calculate the outer blur first
        BlurMaskFilter outerBlurMaskFilter;
        switch (thickness) {
            case EXTRA_THICK:
                outerBlurMaskFilter = sExtraThickOuterBlurMaskFilter;
                break;
            case THICK:
                outerBlurMaskFilter = sThickOuterBlurMaskFilter;
                break;
            case MEDIUM:
                outerBlurMaskFilter = sMediumOuterBlurMaskFilter;
                break;
            default:
                throw new RuntimeException("Invalid blur thickness");
        }
        mBlurPaint.setMaskFilter(outerBlurMaskFilter);
        int[] outerBlurOffset = new int[2];
        Bitmap thickOuterBlur = glowShape.extractAlpha(mBlurPaint, outerBlurOffset);
        if (thickness == EXTRA_THICK) {
            mBlurPaint.setMaskFilter(sMediumOuterBlurMaskFilter);
        } else {
            mBlurPaint.setMaskFilter(sThinOuterBlurMaskFilter);
        }

        int[] brightOutlineOffset = new int[2];
        Bitmap brightOutline = glowShape.extractAlpha(mBlurPaint, brightOutlineOffset);

        // calculate the inner blur
        srcDstCanvas.setBitmap(glowShape);
        srcDstCanvas.drawColor(0xFF000000, PorterDuff.Mode.SRC_OUT);
        BlurMaskFilter innerBlurMaskFilter;
        switch (thickness) {
            case EXTRA_THICK:
                innerBlurMaskFilter = sExtraThickInnerBlurMaskFilter;
                break;
            case THICK:
                innerBlurMaskFilter = sThickInnerBlurMaskFilter;
                break;
            case MEDIUM:
                innerBlurMaskFilter = sMediumInnerBlurMaskFilter;
                break;
            default:
                throw new RuntimeException("Invalid blur thickness");
        }
        mBlurPaint.setMaskFilter(innerBlurMaskFilter);
        int[] thickInnerBlurOffset = new int[2];
        Bitmap thickInnerBlur = glowShape.extractAlpha(mBlurPaint, thickInnerBlurOffset);

        // mask out the inner blur
        srcDstCanvas.setBitmap(thickInnerBlur);
        srcDstCanvas.drawBitmap(glowShape, -thickInnerBlurOffset[0],
                -thickInnerBlurOffset[1], mErasePaint);
        srcDstCanvas.drawRect(0, 0, -thickInnerBlurOffset[0], thickInnerBlur.getHeight(),
                mErasePaint);
        srcDstCanvas.drawRect(0, 0, thickInnerBlur.getWidth(), -thickInnerBlurOffset[1],
                mErasePaint);

        // draw the inner and outer blur
        srcDstCanvas.setBitmap(srcDst);
        srcDstCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mHolographicPaint.setColor(color);
        srcDstCanvas.drawBitmap(thickInnerBlur, thickInnerBlurOffset[0], thickInnerBlurOffset[1],
                mHolographicPaint);
        srcDstCanvas.drawBitmap(thickOuterBlur, outerBlurOffset[0], outerBlurOffset[1],
                mHolographicPaint);

        // draw the bright outline
        mHolographicPaint.setColor(outlineColor);
        srcDstCanvas.drawBitmap(brightOutline, brightOutlineOffset[0], brightOutlineOffset[1],
                mHolographicPaint);

        // cleanup
        srcDstCanvas.setBitmap(null);
        brightOutline.recycle();
        thickOuterBlur.recycle();
        thickInnerBlur.recycle();
        glowShape.recycle();
    }

    void applyExtraThickExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color,
                                                 int outlineColor) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, EXTRA_THICK);
    }

    void applyThickExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color,
                                            int outlineColor) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, THICK);
    }

    void applyMediumExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color,
                                             int outlineColor, boolean clipAlpha) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, clipAlpha,
                MEDIUM);
    }

    public void applyMediumExpensiveOutlineWithBlur(Bitmap srcDst, Canvas srcDstCanvas, int color,
                                                    int outlineColor) {
        applyExpensiveOutlineWithBlur(srcDst, srcDstCanvas, color, outlineColor, MEDIUM);
    }

}
