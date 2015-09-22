package com.anson.acode;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

public class DialogUtils {
	private ProgressDialog dialog = null;
	Context cxt = null;
	View loading;
	public DialogUtils(Context c){
		this.cxt = c;
		dialog = null;
	}
	
	public DialogUtils(View v){
		loading = v;
	}
	public void setVisibility(int vi){
		if(loading != null){
			loading.setVisibility(vi);
		}
	}
	public void show(boolean b){
		if(cxt == null)return;
		if(b){
			if(dialog == null){
				dialog = new ProgressDialog(cxt);
				dialog.setMessage("wait please...");
				dialog.setCancelable(true);
			}
			dialog.show();
		}else{
			if(dialog != null){
				dialog.dismiss();
				dialog = null;
			}
		}
	}
	
	public void generateDialogWithTitleNMsg(String title, String msg){
		if(cxt == null)return;
		if(dialog == null){
			dialog = new ProgressDialog(cxt);
		}
		dialog.setTitle(title);
		dialog.setMessage(msg);
	}
	
	public static AlertDialog getAlertDialog(Context context, int icon, String title, String message, 
			String navStr,android.content.DialogInterface.OnClickListener listener,
			String posStr, android.content.DialogInterface.OnClickListener listener2){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(icon);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton(navStr, listener);
		builder.setPositiveButton(posStr, listener2);
		
		return builder.create();
	}
}
