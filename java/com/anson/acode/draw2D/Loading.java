package com.anson.acode.draw2D;

import com.anson.acode.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.MotionEvent;

public class Loading extends ImageObject2 {
	public Loading(Status status, Bitmap bitmap, int screenWidth,
			int screenHeight) {
		super(status, bitmap, screenWidth, screenHeight);
		// TODO Auto-generated constructor stub
	}
	
	public Loading(Status status, int screenWidth,
			int screenHeight, Resources res) {
		super(status, BitmapFactory.decodeResource(res, R.drawable.loading), screenWidth, screenHeight);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onClicked() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onActionDown(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onActionMove(MotionEvent e, int dx, int dy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onActionUp(MotionEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void moveToTargetStepByStep(int timePassed) {
		// TODO Auto-generated method stub
		//super.moveToTargetStepByStep(timePassed);
		float rotateZ = getStat().getRotation()[2] - 4f;
		float rotateX = 70;
		getStat().setRotate(rotateX, -15, rotateZ);
	}
	public static Loading getLoadingView(int sW, int sH, Resources res){
		Status status = new Status(new Rect(sW-64, sH-64, sW, sH));
		Loading load = new Loading(status, sW, sH, res);
		load.setDuration(500);
		Status target = load.createStatusFromCurrent();
		target.setRotate(0, 0, -1200);
		load.setTarget(target, null, false);
		return load;
	}

	@Override
	public void onLongClicked() {
		// TODO Auto-generated method stub
		
	}

}
