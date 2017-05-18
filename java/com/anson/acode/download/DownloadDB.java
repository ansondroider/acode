package com.anson.acode.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;

import com.anson.acode.TimeUtils;

/**
 * Created by anson on 16-11-10.
 * save download information to database.
 */

public class DownloadDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = Environment.getDownloadCacheDirectory() + "/dlinfo.db";
    public static final int DATABASE_VERSION = 1;
    public DownloadDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DownloadDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public static final String TABLE_DOWNLOAD = "download";

    private final String TABLE_DOWNLOAD_ID = "_id";
    private final String TABLE_DOWNLOAD_NAME = "name";
    private final String TABLE_DOWNLOAD_URLS = "urls";
    private final String TABLE_DOWNLOAD_LOCALS = "localpath";
    private final String TABLE_DOWNLOAD_TIME = "time";
    private final String TABLE_DOWNLOAD_COMPLETE = "completed";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_DOWNLOAD + " (" +
                TABLE_DOWNLOAD_ID + " TEXT PRIMARY KEY,"+
                TABLE_DOWNLOAD_NAME + " TEXT," +
                TABLE_DOWNLOAD_URLS + " TEXT," +
                TABLE_DOWNLOAD_LOCALS + " TEXT," +
                TABLE_DOWNLOAD_TIME + " TEXT," +
                TABLE_DOWNLOAD_COMPLETE + " INTEGER" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(ContentValues values, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(table, null, values);
        db.close();
    }
    public void update(ContentValues values, String id, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(table, values, TABLE_DOWNLOAD_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Cursor query(String id, String table) {
        if(TextUtils.isEmpty(id))return null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(table, null, TABLE_DOWNLOAD_ID + "=" + id, null, null, null, null);
        return c;
    }
    public Cursor query(String table){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(table, null, null, null, null, null, null);
        return c;
    }
    public void delete(String id, String table) {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null)
            db = getWritableDatabase();
        db.delete(table, TABLE_DOWNLOAD_ID + " =?", new String[] { id});
    }

    public void addTask(Task task){
        if(task != null){
            ContentValues values = new ContentValues();
            values.put(TABLE_DOWNLOAD_ID, task.getTaskId());
            values.put(TABLE_DOWNLOAD_NAME, task.getName());
            values.put(TABLE_DOWNLOAD_URLS, task.getUrlsStr());
            values.put(TABLE_DOWNLOAD_LOCALS, task.getLocalPathStr());
            values.put(TABLE_DOWNLOAD_TIME, TimeUtils.getTimeString());
            values.put(TABLE_DOWNLOAD_COMPLETE, 0);
        }
    }
}
