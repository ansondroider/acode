package com.anson.acode.jni;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BitmapBlur {
	static{
		System.loadLibrary("BitmapBlur");
	}
	
	/**
	 * blur src bitmap
	 * @param src
	 * @param cxt
	 * @param radius 0 <= radius <= 25
	 * @return
	 */
	public static Bitmap getBlurBitmap(Bitmap src, Context cxt, int radius){
		return getBlurBitmap(src, cxt, radius, false);
	}
	
	/**
	 * blur src bitmap by JNI
	 * @param src
	 * @param radius
	 * @return
	 */
	public static Bitmap getBlurBitmapJNI(Bitmap src, int radius){
		return getBlurBitmap(src, null, radius, true);
	}
	
	/**
	 * blur src, if SDK > 16, use SDK, else use JNI;
	 * @param src
	 * @param cxt
	 * @param radius radius 0 <= radius <= 25
	 * @param forceJNI
	 * @return
	 */
	public static Bitmap getBlurBitmap(Bitmap src, Context cxt, int radius, boolean forceJNI){
		if (VERSION.SDK_INT > 16 && !forceJNI) {
            Bitmap bitmap = src.copy(src.getConfig(), true);

            final RenderScript rs = RenderScript.create(cxt);
            final Allocation input = Allocation.createFromBitmap(rs, src, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }else{
        	blur(src, radius);
        }
		return src;
	}
	public native static void blurArray(int[] bm, int w, int h, int radius);
	public native static void blur(Bitmap bm, int radius);
}
