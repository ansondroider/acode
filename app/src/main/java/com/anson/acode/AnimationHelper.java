package com.anson.acode;

public class AnimationHelper {
	public static float getMoveRate(int passtime, int DURATION, boolean acce){
		return acce ? (float)Math.pow(passtime, 2)/(float)Math.pow(DURATION, 2):
					(float)(2*DURATION*passtime - Math.pow(passtime, 2)) / (float)Math.pow(DURATION, 2);
	}
}
