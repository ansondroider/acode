package com.anson.acode;

import java.io.File;

import com.anson.acode.FileUtils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LogForm {
	Button btn_close, btn_save;
	Context mContext;
	TextView tv_title, tv_content;
	View parent;
	public LogForm(Context cxt, View parent, final int close, final int save, final int title, final int content, OnClickListener listener){
		mContext = cxt;
		this.parent = parent;
		btn_close = (Button)parent.findViewById(close);
		btn_close.setOnClickListener(listener);
		btn_save = (Button)parent.findViewById(save);
		tv_title = (TextView)parent.findViewById(title);
		tv_content = (TextView)parent.findViewById(content);
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String s = tv_content.getText().toString();
				String fn = tv_title.getText().toString();
				if(fn.lastIndexOf("/") > 0)fn = fn.substring(fn.lastIndexOf("/") +1);
				if(fn.lastIndexOf(".") > 0)fn = fn.substring(0, fn.lastIndexOf("."));
				
				FileUtils.writeStringToFile(new File("/mnt/sdcard/"+fn+".xml"), s);
				Toast.makeText(mContext, "save file /mnt/sdcard/"+fn+".xml", 1000).show();
			}
		});
	}
	public LogForm(Context cxt, View parent, final int close, final int save, final int title, final int content,
			String str_title, String str_content, OnClickListener listener){
		mContext = cxt;
		this.parent = parent;
		btn_close = (Button)parent.findViewById(close);
		btn_close.setOnClickListener(listener);
		tv_title = (TextView)parent.findViewById(title);
		tv_title.setText(str_title);
		tv_content = (TextView)parent.findViewById(content);
		tv_content.setText(str_content);
		btn_save = (Button)parent.findViewById(save);
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String s = tv_content.getText().toString();
				FileUtils.writeStringToFile(new File("/mnt/sdcard/LogForm.xml"), s);
				Toast.makeText(mContext, "save file /mnt/sdcard/LogForm.xml", 1000).show();
			}
		});
	}
	
	public void show(boolean show){
		isShow = show;
		parent.setVisibility(show ? View.VISIBLE:View.INVISIBLE);
	}
	
	public boolean isShowing(){
		return isShow;
	}
	
	public LogForm appendTitle(String title){
		if(!isShow)tv_title.setText(title);
		else{
			tv_title.append(title);
		}
		return this;
	}
	
	public LogForm setTitle(String title){
		if(!isShow)tv_title.setText(title);
		else{
			tv_title.setText(title);
		}
		return this;
	}
	public LogForm appendContent(String content){
		if(!isShow)tv_content.setText(content);
		else tv_content.append(content);
		return this;
	}
	
	public LogForm setContent(String content){
		if(!isShow)tv_content.setText(content);
		else tv_content.setText(content);
		return this;
	}
	boolean isShow = false;
}
