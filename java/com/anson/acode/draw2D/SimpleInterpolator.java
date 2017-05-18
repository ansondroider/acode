package com.anson.acode.draw2D;

import com.anson.acode.ALog;

public class SimpleInterpolator extends Interpolator {
	String TAG = "SimpleInterpolator";
	public SimpleInterpolator(int duration) {
		super(duration);
		// TODO Auto-generated constructor stub
	}

	boolean isAcce = false;
	public SimpleInterpolator(int duration, boolean acce) {
		super(duration);
		isAcce = acce;
		// TODO Auto-generated constructor stub
	}
	@Override
	public float getProgress(long currTime) {
		// TODO Auto-generated method stub
		ALog.alog(TAG, this.getClass().getName() + " currTime = " + currTime);
		float progress = isAcce ? Interpolator.getAccelerate(duration, currTime - startTime):Interpolator.getDecelerate(duration, currTime - startTime);
		return progress;
	}

}
