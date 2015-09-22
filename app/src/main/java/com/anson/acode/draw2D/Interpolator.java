package com.anson.acode.draw2D;
/**
 * XAnimation: support the customize animation,
 * just like the accelerate or decelerate or constant
 * you can control the rate you want to change in setProgress();
 * @author Anson
 *
 */
public abstract class Interpolator {
	public int duration;
	public long startTime;
	boolean animEnd = true;
	public Interpolator(int duration){
		this.duration = duration;
	}
	public boolean isAnimationEnd(){
		return animEnd;
	}
	protected void setAnimEnd(boolean e) {
		animEnd = e;
	}
	
	public void start(long startTime){
		this.startTime = startTime;
	}
	public abstract float getProgress(long currTime);
	
	/**
	 * get accelerate animation
	 * @param duration animation time
	 * @param current current time
	 * @return Float current progress
	 */
	public static float getAccelerate(int duration, long current){
		float total =(float) Math.pow(duration, 2);
		float cur = (float)Math.pow(current, 2);
		return cur/total;
	}
	
	/**
	 * get decelerate animation
	 * @param duration
	 * @param current
	 * @return
	 */
	public static float getDecelerate(int duration, long current){
		float total = (float) Math.pow(duration, 2);
		float cur = (float)Math.pow(current, 2);
		return (2 * duration * current + cur)/total;
	}
	
	/**
	 * get constance animation.
	 * @param duration
	 * @param current
	 * @return
	 */
	public static float getConstance(int duration, long current){
		return current/(float)duration;
	}
}
