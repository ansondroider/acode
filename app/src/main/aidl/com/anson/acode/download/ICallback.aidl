package com.anson.acode.download;

interface ICallback {
	void onTaskSizeChanged(String taskId, boolean add);
	void onProgressUpdate(int what, String taskId, String proStr);
}