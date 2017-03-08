package com.example.android.cse594project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "noteDB.db";
    public static final String TABLE_NAME = "notes_table";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTE = "Note";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_ALARMID = "AlarmID";
    public static final String COLUMN_ALARMTYPE = "AlarmType";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String table = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_NOTE + " TEXT, " +
                COLUMN_DATE + " TEXT, " + " TEXT, " + COLUMN_ALARMTYPE + " INTEGER, " + COLUMN_ALARMID + " INTEGER)";
        db.execSQL(table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_NOTE}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor;
        } else {
            return null;
        }
    }

    public void addNote(String note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_DATE, "null");
        values.put(COLUMN_ALARMTYPE, -1);
        values.put(COLUMN_ALARMID, -1);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteNote(int id) {
        String idString[] = {Integer.toString(id)};
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", idString);
    }

    public void deleteNoteCB(int id, Context context) {
        String idString[] = {Integer.toString(id)};
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", idString);
        MainActivity main = (MainActivity) context;
        main.showNotes();
    }



    public void updateNote(int id, String n) {
        ContentValues note = new ContentValues();
        note.put(COLUMN_NOTE, n);
        SQLiteDatabase db = this.getReadableDatabase();
        db.update(TABLE_NAME, note, "_id = ? ", new String[]{Integer.toString(id)});
    }


    public String getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String note = "null";
        Cursor c = db.query(TABLE_NAME, new String[] {COLUMN_NOTE}, COLUMN_ID + "=" + id, null, null, null, null);
        if(c.getCount() == 1){
            c.moveToFirst();
            note = c.getString(c.getColumnIndex(COLUMN_NOTE));
        }
        return note;
    }

    public String getDate(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String date = "null";
        Cursor c = db.query(TABLE_NAME, new String[] {COLUMN_DATE}, COLUMN_ID + "=" + id, null, null, null, null);
        if(c.getCount() == 1){
            c.moveToFirst();
            date = c.getString(c.getColumnIndex(COLUMN_DATE));
        }
        return date;
    }

    public int getAlarm(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        int alarmID = -1;
        Cursor c = db.query(TABLE_NAME, new String[] {COLUMN_ALARMID}, COLUMN_ID + "=" + id, null, null, null, null);
        if(c.getCount() == 1){
            c.moveToFirst();
            alarmID = c.getInt(c.getColumnIndex(COLUMN_ALARMID));
        }
        return alarmID;
    }

    public int getAlarmType(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        int alarmType = -1;
        Cursor c = db.query(TABLE_NAME, new String[] {COLUMN_ALARMTYPE}, COLUMN_ID + "=" + id, null, null, null, null);
        if(c.getCount() == 1){
            c.moveToFirst();
            alarmType = c.getInt(c.getColumnIndex(COLUMN_ALARMTYPE));
        }
        return alarmType;
    }


    public void updateDate(int id, String n) {
        ContentValues note = new ContentValues();
        note.put(COLUMN_DATE, n);
        SQLiteDatabase db = this.getReadableDatabase();
        db.update(TABLE_NAME, note, "_id = ? ", new String[]{Integer.toString(id)});
    }



    public void updateAlarm(int id, int n) {
        ContentValues note = new ContentValues();
        note.put(COLUMN_ALARMID, n);
        SQLiteDatabase db = this.getReadableDatabase();
        db.update(TABLE_NAME, note, "_id = ? ", new String[]{Integer.toString(id)});
    }

    public void updateAlarmType(int id, int n) {
        ContentValues note = new ContentValues();
        note.put(COLUMN_ALARMTYPE, n);
        SQLiteDatabase db = this.getReadableDatabase();
        db.update(TABLE_NAME, note, "_id = ? ", new String[]{Integer.toString(id)});
    }


}
