package com.anson.acode.view;

import com.anson.acode.R;
import com.anson.acode.TimeUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class TimeSelectDialog extends Dialog implements
		android.view.View.OnClickListener {
	String firstTime = "09:00";

	public TimeSelectDialog(Context context, OnTimeSetListener listener,
			String curValue) {
		super(context);
		// TODO Auto-generated constructor stub
		lis = listener;
		firstTime = curValue;
	}

	public TimeSelectDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	TextView tv_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_timeselect);
		initView();
	}

	void initView() {
		tv_time = (TextView) findViewById(R.id.dt_tv_time);
		findViewById(R.id.dt_tv_1).setOnClickListener(this);
		findViewById(R.id.dt_tv_2).setOnClickListener(this);
		findViewById(R.id.dt_tv_3).setOnClickListener(this);

		findViewById(R.id.dt_tv_4).setOnClickListener(this);
		findViewById(R.id.dt_tv_5).setOnClickListener(this);
		findViewById(R.id.dt_tv_6).setOnClickListener(this);

		findViewById(R.id.dt_tv_7).setOnClickListener(this);
		findViewById(R.id.dt_tv_8).setOnClickListener(this);
		findViewById(R.id.dt_tv_9).setOnClickListener(this);

		findViewById(R.id.dt_tv_ok).setOnClickListener(this);
		findViewById(R.id.dt_tv_0).setOnClickListener(this);
		findViewById(R.id.dt_tv_del).setOnClickListener(this);
	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		tv_time.setText(TimeUtils.formatTimeString(firstTime));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		char c = '0';
		final int id = v.getId();
		if (id == R.id.dt_tv_0) {
			c = '0';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_1) {
			c = '1';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_2) {
			c = '2';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_3) {
			c = '3';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_4) {
			c = '4';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_5) {
			c = '5';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_6) {
			c = '6';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_7) {
			c = '7';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_8) {
			c = '8';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_9) {
			c = '9';
			insertTimeChar(c);
		} else if (id == R.id.dt_tv_ok) {
			onOkClick();
		} else if (id == R.id.dt_tv_del) {
			deleteChar();
		}

	}

	void onOkClick() {
		if (lis != null) {
			lis.onTimeSet(tv_time.getText().toString());
		}
		dismiss();
	}

	void deleteChar() {
		String cs = tv_time.getText().toString();
		if (cs.length() < 2)
			return;
		if (cs.length() > 3) {
			cs = cs.substring(0, cs.length() - 1);
		} else if (cs.length() > 0 && cs.length() <= 3) {
			cs = cs.substring(0, cs.length() - 2) + ":";
		}

		tv_time.setText(cs);
	}

	void insertTimeChar(char c) {
		String cs = tv_time.getText().toString();
		if (cs.equals(":")) {
			cs = c + cs;
			if ((Integer.valueOf(cs.substring(0, 1)) > 2)) {
				cs = "0" + cs;
			}
		} else if (cs.length() < 3) {
			cs = cs.substring(0, cs.length() - 1) + c + ":";
		} else if (cs.length() == 3) {
			cs = cs + c;
			if (Integer.valueOf(cs.substring(3, 4)) > 5) {
				cs = cs.substring(0, 3) + "0" + cs.substring(3, 4);
			}
		} else {
			cs = cs + c;
		}

		tv_time.setText(cs);
	}

	OnTimeSetListener lis;

	public static interface OnTimeSetListener {
		void onTimeSet(String time);
	}

}
