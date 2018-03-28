package com.anson.acode;

/**
 * Created by anson on 17-10-16.
 * convert String to Md5
 */

public class MD5Tool {
    static {
        System.loadLibrary("Md5Tool");
    }
    public native static String getMd5(String src);
}
