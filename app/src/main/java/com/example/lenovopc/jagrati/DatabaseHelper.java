package com.example.lenovopc.jagrati;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jagrati";
    private static final String TABLE_NAME = "users";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY, is_admin INTEGER, token TEXT);";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public Cursor getRow() {
        SQLiteDatabase db = this.getWritableDatabase();
        String getQuery = "SELECT * FROM " + TABLE_NAME + " LIMIT 1;";
        Cursor cursor = db.rawQuery(getQuery,null);
        return cursor;
    }

    public boolean insertRow(String userId, String isAdmin, String token) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", userId);
        contentValues.put("is_admin", isAdmin.equals("false") ? 0 : 1);
        contentValues.put("token", token);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean updateData(String id, String isAdmin, String token) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (isAdmin != null) {
            contentValues.put("is_admin", isAdmin);
        }

        if (token != null) {
            contentValues.put("token", token);
        }

        long result = db.update(TABLE_NAME, contentValues, "id = ?", new String[] { id });
        db.close();
        return result != -1;
    }

    public void deleteAllRows() {
        SQLiteDatabase db = getWritableDatabase();
        String deleteAllQuery = "DELETE FROM " + TABLE_NAME;
        db.execSQL(deleteAllQuery);
        db.close();
    }
}
