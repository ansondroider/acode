package com.anson.acode.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;

import com.anson.acode.ALog;
import com.anson.acode.HttpUtilsAndroid;
import com.anson.acode.MSG;
import com.anson.acode.R;
import com.anson.acode.StringUtils;
import com.anson.acode.aos.IntentUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.anson.acode.MSG.MSG_DOWN_PROG;

public class SimpleDownloadService extends Service {
	final String TAG = "SimpleDownloadService";
    final int MAX_TASKS = 3;
    private int currentTasks = 0;
    private NotificationManager notiMgr;
	static class H extends Handler{
        WeakReference<SimpleDownloadService> s;
        public H(SimpleDownloadService ds){
            this.s = new WeakReference<SimpleDownloadService>(ds);
        }
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG.MSG_DOWN_STAR:
			case MSG_DOWN_PROG:
			case MSG.MSG_DOWN_STOP:
				s.get().updateProgress(msg.what, msg.obj);
				break;
			}
		}
	}

    H h;
	@Override
	public void onCreate() {
		super.onCreate();
        h = new H(this);
        notiMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent intent) {
		return service;
	}
	
	/** IBinder service declear here *************/
	private static Hashtable<String, Task> tasks = new Hashtable<String, Task>();
	private static ICallback callback = null;

	IBinder service = new IDLService.Stub() {
		@Override
		public int registeCallback(ICallback cb) throws RemoteException {
			ALog.alog(TAG, "registeCallback()");
			callback = cb;
			return 0;
		}

		@Override
		public Task getTaskById(String id) throws RemoteException {
			return tasks.get(id);
		}

		@Override
		public List<Task> getAllTask() throws RemoteException {
			if(tasks != null){
				return new ArrayList<Task>(tasks.values());
			}
			return null;
		}

		@Override
		public int addTask(Task task) throws RemoteException {
			ALog.d(TAG, "addTask");
			String taskId = StringUtils.getUUID();
			task.setTaskId(taskId);
			tasks.put(taskId, task);
			updateTasksInfo(taskId, true);
            findAndStartNextTask();
			return 0;
		}

		@Override
		public void unRegisteCallback(int id) throws RemoteException {
			callback = null;
		}

        /**
         * remove task by id.
         * @param id String
         * @throws RemoteException
         */
		@Override
		public void cancelTask(String id) throws RemoteException {
            ALog.d(TAG, "cancelTask(" + id + ")");
            //find thread and stop it
            DownloadThread dt = DownloadThreadFactory.getFactory().findThread(id);
            if(dt != null){
                dt.cancel();
            }

            //remove task from tasks. then refresh task list.
			tasks.remove(id);
            //TODO remove from database.

			updateTasksInfo(id, false);

            findAndStartNextTask();
		}

        public void stopTask(String id) throws RemoteException {
            ALog.d(TAG, "stopTask(" + id + ")");
            Task t = getTaskById(id);
            if(t != null) {
                ALog.d(TAG, "stopTask set task state to PAUSE");
                t.setState(Task.STATE_PAUSE);
            }

            //find thread
            DownloadThread dt = DownloadThreadFactory.getFactory().findThread(id);
            if(dt != null){
                dt.cancel();
            }

            if(t != null){
                sendProgressWidthId(MSG_DOWN_PROG, t);
            }

            findAndStartNextTask();
        }
        public void startTask(String id) throws RemoteException {
            ALog.d(TAG, "startTask(" + id + ")");
            Task t = getTaskById(id);
            if(t != null && t.getState() == Task.STATE_PAUSE) {
                t.setState(Task.STATE_WAIT);
            }
            findAndStartNextTask();
        }
	};

    //DownloadThread downloadThread = null;
    /**
     * open a new thread to down a task.
     * @param task task
     */
	void startDownload(final Task task){
        currentTasks += 1;
        DownloadThread downloadThread = new DownloadThread(task){
			public void execute() {
                task.setState(Task.STATE_DOWNLOADING);
                sendProgressWidthId(MSG.MSG_DOWN_STAR, task);
				List<String> url = task.getUrls();
				List<String> path = task.getLocalPath();
				ALog.d(TAG, "startDownload " + task.getTaskId());
				for(int i=0; i<url.size(); i++){
                    String[] paths;
                    File f;
                    if(path.get(i).indexOf(";;") > 0) {
                        paths = path.get(i).split(";;");//folder;;fileName
                        f = new File(paths[0] + "/" + paths[1]);
                    }else{
                        //is full file path;
                        int lastIdx = path.get(i).lastIndexOf("/");
                        paths = new String[]{path.get(i).substring(0, lastIdx), path.get(i).substring(lastIdx + 1)};
                        f = new File(path.get(i));
                    }

					if(!canceled())sendProgressWidthId(MSG_DOWN_PROG, task);
					if(!f.exists()){
                        HttpUtilsAndroid.downloadFileFromUrlByDownloadThread(url.get(i), paths[0], paths[1], this);
                        if(canceled()){
                            if(f.exists()){
                                //remove file uncompleted.
                                ALog.d(TAG, "remove file " + (f.delete() ? "success" : "failed"));
                            }
                            break;
                        }
					}
                    task.formatProgress(i, url.size(), task.getName());
                    completeOne();
/*                    if(canceled()){
						updateTasksInfo(task.getTaskId(), false);
						break;
					}*/
				}

                if(!canceled()){//if canceled, it's ll be PAUSE
                    task.setState(Task.STATE_FINISH);
                    task.formatProgress(url.size()-1, url.size(), task.getName());
                }
                currentTasks --;
                sendProgressWidthId(MSG.MSG_DOWN_STOP, task);
				ALog.d("ComicDownLoadService", "startDownload " + (canceled() ? "canceled " : "finished ") +
                    task.getTaskId());
                //updateNotification(task);
			}
		};
		downloadThread.start();
	}

	/** 
	 * we do every download event here; and if complete remove task and started next task.
	 * @param what message
	 * @param progress progress
	 */
	void updateProgress(int what, Object progress){
		//ALog.alog(TAG, "updateProgress()");
		String[] info = (String[])progress;
		//update task state here to make task's state is newly
		Task t = tasks.get(info[0]);
		if(t != null){
			t.setProgress(info[1]);
		}
		if(callback != null){
			try {
				//ALog.alog(TAG, "updateProgress()2");
				callback.onProgressUpdate(what, info[0], info[1]);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		if(MSG.MSG_DOWN_STOP == what){
			if(t != null && t.getState() == Task.STATE_FINISH){
				tasks.remove(info[0]);
				updateTasksInfo(info[0], false);
			}

            findAndStartNextTask();
		}
	}

    private void findAndStartNextTask(){
        //only MAX_TASKS can be run at the same time
        if(currentTasks >= MAX_TASKS)return;
        for(Task tt : tasks.values()){
            if(tt.getState() == Task.STATE_WAIT){
                startDownload(tt);
                break;
            }
        }
    }
	
	/**
	 * tasks size has changed.
	 * @param id task id in String
	 */
	void updateTasksInfo(String id, boolean add){
		if(callback != null){
			try {
				callback.onTaskSizeChanged(id, add);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

        if(tasks.size() == 0){
            updateNotification(null);
        }
	}
	
	/**
	 * send message to handler to get the current state
	 * @param what message type
	 * @param task task info
	 */
	void sendProgressWidthId(int what, Task task){
		Message msg = h.obtainMessage(what);
		msg.obj = new String[]{task.getTaskId(), task.getProgress()};
        updateNotification(task);
		h.sendMessage(msg);
	}

    long lastNotifyTime = 0;
    void updateNotification(Task task){
        long cur = SystemClock.uptimeMillis();
        if(cur - lastNotifyTime < 100 && task != null){
            return;
        }else{
            lastNotifyTime = cur;
        }
        String pkg = getPackageName();
        Intent intent = IntentUtils.getIntentFromPackage(pkg, this);
        PendingIntent pIntent = intent == null ? null :
                PendingIntent.getActivity(SimpleDownloadService.this,
                this.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_download);
        builder.setAutoCancel(task == null);
        builder.setOngoing(task != null);
        builder.setContentTitle(task != null ? tasks.size() + " comic downloading":
                    "download finish");
        if(task != null) {
            builder.setContentText(task.getName());
            builder.setProgress(100, Integer.valueOf(task.getProgress().split(";;")[2]), false);
        }
        builder.setDefaults(0);
        if (pIntent != null) builder.setContentIntent(pIntent);

        notiMgr.notify(this.hashCode(), builder.build());
    }
}
