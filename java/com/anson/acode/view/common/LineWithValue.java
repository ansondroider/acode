package com.anson.acode.view.common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.anson.acode.AUtils;
import com.anson.acode.StringUtils;

/**
 * com.ansondroider.magiclauncher.views.common
 * Created by anson on 16-4-15.
 */
public class LineWithValue {
    /**
     * line:
     *      (0, 1)
     *      (4, 5)       (6, 7)
     *        /Y-----------Z  |_value_|
     *       /
     *      /
     *     X (2, 3)
     *
     * -------------------------------
     */
    float[] line = new float[8];
    RectF textArea = null;
    Paint paint;
    float strokeWidth = 1;
    float fontSize = 14;
    boolean toLeft = false;

    float textPosX, textPosY;
    String value;

    int colorTextBg = Color.argb(64, 100, 155, 122);
    Path pathTextBg;

    public LineWithValue(Paint p, float strokeWidth, boolean lineToLeft,
                         float sourceX, float sourceY,
                         float textLeft, float textTop,
                         float textSize, String text){
        this.strokeWidth = strokeWidth;
        paint = p;
        toLeft = lineToLeft;
        textPosX = textLeft;
        textPosY = textTop;

        fontSize = textSize;
        value = text;
        textArea = new RectF(textLeft, textTop, textLeft + fontSize * 12, textTop + fontSize * 1.4f);
        setSourcePosition(sourceX, sourceY);
        line[1] = line[5] = line[7] = textArea.centerY();
        line[6] = textLeft;

        initTextBg();
    }

    void initTextBg(){
        /**
         *          /[2,3]-------------------[4,5]
         *         /                           |
         *   [0, 1]                            |
         *         \                           |
         *          \[8, 9]------------------[6,7]
         */
        pathTextBg = new Path();
        pathTextBg.moveTo(textArea.left, textArea.centerY());
        pathTextBg.lineTo(textArea.left + fontSize * 0.75f, textArea.top);
        pathTextBg.lineTo(textArea.left + fontSize*8, textArea.top);
        pathTextBg.lineTo(textArea.left + fontSize*8, textArea.bottom);
        pathTextBg.lineTo(textArea.left + fontSize * 0.75f, textArea.bottom);
        pathTextBg.close();

    }

    public void setSourcePosition(float srcX, float srcY){
        formatValues(srcX, srcY);
    }

    public void setText(String text){
        value = text;
        float w = StringUtils.getTextWidth(text, paint);
        textArea.right = textArea.left + w + fontSize;
    }

    /**
     *            Y---------
     *           /|
     *          / |
     *         /30|
     *        /   | H
     *       /    |
     *      /     |
     *     /      |
     * ---X-----------------
     *       W
     * tan(PI/6) = 0.57735027 = W/H
     * @param X int
     * @param Y int
     */
    float rate = 0.57735027f;
    void formatValues(float X, float Y){
        line[2] = X;
        line[3] = Y;
        float h = Math.abs(textPosY - Y);
        float w = h * rate;
        line[0] = line[4] = X + w;
    }

    int color;
    public void setColor(int c){
        color = c;
    }
    public void draw(Canvas canvas){
        /** draw line **/
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawLines(line, paint);
        /** draw point **/
        canvas.drawCircle(line[2], line[3], fontSize/3, paint);
        /** draw bg **/
        paint.setColor(colorTextBg);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(pathTextBg, paint);

        /** text **/
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(fontSize);
        canvas.drawText(value, textArea.left + fontSize, textArea.top + fontSize, paint);
    }
}
