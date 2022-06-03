package com.example.anomalbrowser.dbSQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {
    Context context;


    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "anomal.db";
    public static final String TABLE_NAME = "history";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_URL = "URL";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME = "name";

    public DB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " text, " + COLUMN_URL + " text, " + COLUMN_DATE + " text, " + COLUMN_TIME + " text);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addInHistory(String name, String url, String data, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_URL, url);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DATE, data);
        cv.put(COLUMN_TIME, time);
        long res = db.insert(TABLE_NAME, null, cv);
        if (res == -1) {
//            Toast.makeText(context, "FAILURE", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();
        }
    }

    public void delete(History history, int id)
    {
        String name = history.getName();
        String URL = history.getURL();
        String date = history.getData();
        String time = history.getTime();
        SQLiteDatabase dbR = this.getReadableDatabase();
        Cursor cursor = dbR.rawQuery("select * from " + TABLE_NAME, null);
        cursor.moveToPosition(id);

        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NAME, COLUMN_NAME + " = ?" + " and " + COLUMN_URL + " = ?" + " and " +
//                COLUMN_DATE + " = ?" + " and " + COLUMN_TIME + " = ?" + " LIMIT 1", new String[] {name, URL, date, time});
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {String.valueOf(cursor.getInt(0))});
    }

    public ArrayList<History> getAllData()
    {
        ArrayList<History> arrayListHistory = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        if (sqLiteDatabase != null)
        {
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
            if (cursor.getCount() != 0)
            {
                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String URL = cursor.getString(2);
                    String data = cursor.getString(3);
                    String time = cursor.getString(4);
                    arrayListHistory.add(new History(id, name, URL, data, time));
                }

                return arrayListHistory;
            }
            else
            {
                Toast.makeText(context, "No data", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
