package com.example.spendflow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "personalspend.db";
    private static final int DATABASE_VERSION = 1;
    public DataHelper (Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}
    public void onCreate (SQLiteDatabase db){
        String sql = "create table spend (number integer primary key, date text null, totalspend text null, reference text null);";
        Log.d("Data", "onCreate: " + sql);
        db.execSQL(sql);
    }
    public void onUpgrade (SQLiteDatabase arg0, int arg1, int arg2) {
    }
}
