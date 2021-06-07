package com.example.reminder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FeedReader.db";
    private static final int DATABASE_VERSION = 1;

    private static FeedReaderDbHelper mInstance = null;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry.ID + " INTEGER PRIMARY KEY, " +
                    FeedReaderContract.FeedEntry.DATE + " TEXT, " +
                    FeedReaderContract.FeedEntry.COMMENT + " TEXT, " +
                    FeedReaderContract.FeedEntry.HOUR + " INTEGER, " +
                    FeedReaderContract.FeedEntry.MINUTE + " INTEGER, " +
                    FeedReaderContract.FeedEntry.MODE + " INTEGER, " +
                    FeedReaderContract.FeedEntry.DELTA + " LONG, " +
                    FeedReaderContract.FeedEntry.TAG + " TEXT )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    private FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("FeedReaderDbHelper: ", SQL_CREATE_ENTRIES);
        Log.d("FeedReaderDbHelper: ", SQL_DELETE_ENTRIES);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public static FeedReaderDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FeedReaderDbHelper(context);
        }
        return mInstance;
    }
}

