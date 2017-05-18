package com.anson.acode.download;
import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    public static final int STATE_WAIT = 0;
    public static final int STATE_DOWNLOADING = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_FINISH = 3;
	private String taskId = "";// getUUID by service, like: fe5d8537-5667-48da-a2bd-504ce018ec9f
	private String name;//taskName/chapterName
	private List<String> urls;// http://www.cococomic.com/comicName/xxx.jpg
	private List<String> localPath;// folder;;fileName
    /** format as [a-z];;[a-z];;[0-9];;[0-9]
     *              0      1      2      3
     *  [0]: task title to show
     *  [1]: total progress like: 90/123
     *  [2]: current progress : 77
     *  [3]: current state: 3
     * **/
	private String progress = "--;;--;;0;;0";
    private int state = STATE_WAIT;

	public Task(String name, ArrayList<String> urlinfo, ArrayList<String> savePath){
		setName(name);
		setUrls(urlinfo);
		setLocalPath(savePath);
        progress = name + ";;0/0;;0;;0";
	}
	public Task(Parcel in){
		setTaskId(in.readString());
		setName(in.readString());
		setUrls(in.readArrayList(String.class.getClassLoader()));
		setLocalPath(in.readArrayList(String.class.getClassLoader()));
		setProgress(in.readString());
        setState(in.readInt());
	}
	public static final Creator<Task> CREATOR = new Creator<Task>() {
	     public Task[] newArray(int size) {
	         return new Task[size];
	     }
	
	     public Task createFromParcel(Parcel source) {
	    	 return new Task(source);
	     }
    };
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(taskId);
		dest.writeString(name);
		dest.writeList(urls);
		dest.writeList(localPath);
		dest.writeString(progress);
        dest.writeInt(state);
	}
	
	/** Geter and Setter */
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getUrls() {
		return urls;
	}
    public String getUrlsStr(){
        StringBuilder sb = new StringBuilder(urls.get(0));
        for(int i = 1; i < urls.size(); i ++){
            sb.append(";;").append(urls.get(i));
        }
        return sb.toString();
    }
	public void setUrls(ArrayList<String> urls) {
		this.urls = urls;
	}
    public String getLocalPathStr(){
        StringBuilder sb = new StringBuilder(localPath.get(0));
        for(int i = 1; i < localPath.size(); i ++){
            sb.append(";;").append(localPath.get(i));
        }
        return sb.toString();
    }
	public List<String> getLocalPath() {
		return localPath;
	}
	public void setLocalPath(ArrayList<String> localPath) {
		this.localPath = localPath;
	}

    public int getState(){
        return state;
    }
    public boolean isDownloading() {
        return state == STATE_DOWNLOADING;
    }
    public void setState(int state) {
        this.state = state;
        if(progress != null) {
            String[] ss = progress.split(";;");
            if(ss != null){
                progress = ss[0] + ";;" + ss[1] + ";;" + ss[2] + ";;" + state;
            }
        }
    }

    public String formatProgress(int page, int size, String taskName){
        StringBuilder sb = new StringBuilder();
        // taskName/partName;;15/100;;15;;0
        sb.append(taskName).append(";;").append(page + 1).append("/").append(size).append(";;");
        sb.append(page * 100 / (size-1));
        sb.append(";;").append(state);
        setProgress(sb.toString());
        return sb.toString();
    }
}
