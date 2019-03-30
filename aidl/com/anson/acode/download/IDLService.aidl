package com.anson.acode.download;
import com.anson.acode.download.ICallback;
import com.anson.acode.download.Task;
interface IDLService{
	int addTask(in Task taskOk);
	void cancelTask(String id);
	List<Task> getAllTask();
	Task getTaskById(String id);
	int registeCallback(ICallback cb);
	void unRegisteCallback(int id);
	void startTask(String id);
	void stopTask(String id);
}