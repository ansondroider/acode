package com.anson.acode;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ParcelableParent implements Parcelable {
	
	String[] strs;
	int[] ints;
	float[] floats;
	long[] longs;
	
    public ParcelableParent(Parcel source) {
    	source.readStringArray(strs);
    	source.readIntArray(ints);
    	source.readFloatArray(floats);
    	source.readLongArray(longs);
    }
    
    public abstract void setStrings(String[] strs);
    public abstract void setInts(int[] ints);
    public abstract void setFloats(float[] floats);
    public abstract void setLongs(long[] longs);

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		// TODO Auto-generated method stub
		out.writeStringArray(strs);
		out.writeIntArray(ints);
		out.writeFloatArray(floats);
		out.writeLongArray(longs);
	}
	
	public static final Parcelable.Creator<ParcelableParent> CREATOR = new Parcelable.Creator<ParcelableParent>() {
	     public ParcelableParent[] newArray(int size) {
	         return new ParcelableParent[size];
	     }
		@Override
		public ParcelableParent createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new ParcelableParent(source){
				@Override
				public void setStrings(String[] strs) {
					this.strs = strs;}
				@Override
				public void setInts(int[] ints) {
					this.ints = ints;}
				@Override
				public void setFloats(float[] floats) {
					this.floats = floats;}
				@Override
				public void setLongs(long[] longs) {
					this.longs = longs;}};
		}
   };

}
