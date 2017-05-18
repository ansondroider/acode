package com.anson.acode.draw2D;

import android.graphics.Rect;

public class Status{
	private Rect rect;
	private int alpha = 0x000000ff;
	private float scale = 1f;
	private float rx, ry, rz;
	
	/** Construction */
	public Status(Rect area){
		this.rect = area;
	}
	
	public void update(float progress, Status cst, Status target){
		progress = progress > 1 ? 1:progress;
		final Rect tar = target.rect;
		final Rect cstr = cst.rect;
		rect.left = cstr.left + (int)(progress * (tar.left - cstr.left));
		rect.top = cstr.top + (int)(progress * (tar.top - cstr.top));
		rect.right = cstr.right + (int)(progress * (tar.right - cstr.right));
		rect.bottom = cstr.bottom + (int)(progress * (tar.bottom - cstr.bottom));
		
		setAlpha(cst.alpha + (int)(progress * (target.alpha - cst.alpha)));
		setScale(cst.scale + (progress * (target.scale - cst.scale)));
		setRotate(cst.rx + (progress * (target.rx-cst.rx)),
				  cst.ry + (progress * (target.ry - cst.ry)),
				  cst.rz + (progress * (target.rz - cst.rz)));
	}
	
	public void forceToStatus(Status target){
		setRect(target.rect);
		setAlpha(target.alpha);
		setScale(target.scale);
		setRotate(target.rx, target.ry, target.rz);
		target = null;
	}
	
	/** Getter and Setter */
	public Rect getRect() {
		return rect;
	}
	public void setRect(Rect rect) {
		this.rect = rect;
	}
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		alpha = alpha < 0 ? 0:alpha;
		alpha = alpha > 255 ? 255:alpha;
		this.alpha = alpha;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public float[] getRotation() {
		float[] rotate = {rx, ry, rz};
		return rotate;
	}
	public void setRotate(float rotateX, float rotateY, float rotateZ) {
		this.rx = rotateX;
		this.ry = rotateY;
		this.rz = rotateZ;
	}
	public void setRotateX(float rx){
		this.rx = rx;
	}
	public void setRotateY(float ry){
		this.ry = ry;
	}
	public void setRotateZ(float rz){
		this.rz = rz;
	}
	public void rotateX(float rx){
		this.rx += rx;
	}
	public void rotateY(float ry){
		this.ry += ry;
	}
	public void rotateZ(float rz){
		this.rz += rz;
	}
	public void recycle(){
		this.rect = null;
	}
	
	public Rect getScaledRect(){
		Rect sr = new Rect(getRect());
		int sx =((int)(sr.width() * (scale-1)))>>1;
		int sy =((int)(sr.height() * (scale-1)))>>1;
		sr.left -= sx;
		sr.right += sx;
		sr.top -= sy;
		sr.bottom += sy;
		return sr;
	}
}
