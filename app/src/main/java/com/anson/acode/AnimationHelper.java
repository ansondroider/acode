package com.anson.acode;

public class AnimationHelper {
	public static float getMoveRate(int passtime, int DURATION, boolean acce){
		return acce ? (float)Math.pow(passtime, 2)/(float)Math.pow(DURATION, 2):
					(float)(2*DURATION*passtime - Math.pow(passtime, 2)) / (float)Math.pow(DURATION, 2);
	}

    public static float getMoveRate2(int passed, int total, boolean acce){
        return acce ? (float)(total - Math.sqrt(Math.pow(total, 2) - Math.pow(passed, 2))) / (float)total
                :
                (float)Math.sqrt(Math.pow(total, 2) - Math.pow((total - passed), 2))/total;
    }
}
