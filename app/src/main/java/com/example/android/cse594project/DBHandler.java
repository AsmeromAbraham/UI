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

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_products_table = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_NOTE + " TEXT )";
        db.execSQL(create_products_table);
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
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteNote(int id) {
        String idString[] = {Integer.toString(id)};
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", idString);
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
}
