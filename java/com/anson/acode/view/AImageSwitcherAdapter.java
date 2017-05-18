package com.anson.acode.view;

/**
 * Created by anson on 17-4-3.
 * adapter for image switcher
 */

public abstract class AImageSwitcherAdapter{
    public abstract String getImageFile(int index);
    public abstract int getIndexOfFile(String file);
    public abstract int getCount();
}