package com.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ToDoDB extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "todo_db";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "todo_table";
    public final static String FIELD_id = "_id";
    public final static String FIELD_TEXT = "todo_text";
    private final static String TAG = "ToDoDB";

    public ToDoDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + " (" + FIELD_id
                + " INTEGER primary key autoincrement,  " + FIELD_TEXT
                + " text)";
        Log.v(TAG, "onCreate");
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        Log.v(TAG, "onUpgrade");
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        Log.v(TAG, "onDowngrade");
        onCreate(db);
    }

    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    public Cursor select(String text) {
        SQLiteDatabase db = this.getReadableDatabase();
        //query方法的第一个参数是表名
        //第二个参数是使用String数组存放列名，一个列明占用一个元素
        //第三个参数是SQL语句中的Where条件子语句，其中的？对应第四个参数中String数组。数组中有几个字符就有几个问号。
        //第三个参数是Where的条件
        //第四个参数分组
        //第五个参数是SQL中的having
        //第六个参数是排序

        String where = FIELD_TEXT + " = ?";
        String[] whereValue = {text};
        Cursor cursor = db
                .query(TABLE_NAME, null, where, whereValue, null, null, null);
        return cursor;
    }

    public long insert(String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FIELD_TEXT, text);
        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_id + " = ?";
        String[] whereValue = {Integer.toString(id)};
        db.delete(TABLE_NAME, where, whereValue);
    }

    public void update(int id, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_id + " = ?";
        String[] whereValue = {Integer.toString(id)};
        ContentValues cv = new ContentValues();
        cv.put(FIELD_TEXT, text);
        db.update(TABLE_NAME, cv, where, whereValue);
    }
}
