package com.example.wheatinfo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(long year, long production, long yield) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.YEAR, year);
        contentValue.put(DatabaseHelper.PRODUCTION, production);
        contentValue.put(DatabaseHelper.YIELD, yield);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.YEAR, DatabaseHelper.PRODUCTION, DatabaseHelper.YIELD};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, long year, long production, long yield) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.YEAR, year);
        contentValues.put(DatabaseHelper.PRODUCTION, production);
        contentValues.put(DatabaseHelper.YIELD, yield);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public void delete(long id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
    }

    public Cursor findAllMoreThan22mln() {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.YEAR, DatabaseHelper.PRODUCTION, DatabaseHelper.YIELD};
        String selection = "production > ?";
        String[] selectionArgs = new String[] { "22000000" };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor findAverageProduction(){
        String query = "SELECT AVG("+DatabaseHelper.PRODUCTION +") FROM "+DatabaseHelper.TABLE_NAME;
        Cursor cursor = database.rawQuery(query,null);
        return cursor;
    }
}
