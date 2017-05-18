package com.anson.acode.view.common;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

/**
 * com.ansondroider.magiclauncher.views.common
 * Created by anson on 16-4-7.
 */
public class Xfermodes {
        public static final int CLEAR = 0;
        public static final int SRC = 1;
        public static final int DST = 2;
        public static final int SRC_OVER = 3;
        public static final int DST_OVER = 4;
        public static final int SRC_IN = 5;
        public static final int DST_IN = 6;
        public static final int SRC_OUT = 7;
        public static final int DST_OUT = 8;
        public static final int SRC_ATOP = 9;
        public static final int DST_ATOP = 10;
        public static final int XOR = 11;
        public static final int DARKEN = 12;
        public static final int LIGHTEN = 13;
        public static final int MULTIPLY = 14;
        public static final int SCREEN = 15;
    public static final Xfermode[] MODES = {
            new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            new PorterDuffXfermode(PorterDuff.Mode.SRC),
            new PorterDuffXfermode(PorterDuff.Mode.DST),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
            new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.XOR),
            new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
            new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
    };
}
