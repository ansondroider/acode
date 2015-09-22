package com.anson.acode;

import android.os.Bundle;
import android.os.Message;

public class MSG {
	public static final int CP_START = 11;
	public static final int CP_UPDATE = 12;
	public static final int CP_SUCCESS = 13;
	public static final int CP_FAILED = 14;
	public static final String CP_RESULT = "cp_result";
	
	public static Message formatUpdateMessage(int what, int progress){
		Message m = new Message();
		m.what = what;
		m.obj = progress;
		return m;
	}
	public static Message formatResultMessage(int result, String msg){
		Message m = new Message();
		m.what = result;
		m.obj = msg;
		return m;
	}
}
