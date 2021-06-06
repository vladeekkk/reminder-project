package com.example.reminder;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.sql.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ReminderDAOImpl implements ReminderDAO {
    SQLiteDatabase read;
    SQLiteDatabase write;

    public ReminderDAOImpl(Context context) {
        read = FeedReaderDbHelper.getInstance(context).getReadableDatabase();
        write = FeedReaderDbHelper.getInstance(context).getWritableDatabase();
    }

    /*
     * Add new reminder
     * if reminder with input reminder id exists, then nothing is changes
     */
    @Override
    public Reminder save(Reminder reminder) {
        String sql = "INSERT OR IGNORE INTO reminder_database(id, date, comment, hour, minute, mode, delta, tag) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = write.compileStatement(sql);
        statement.bindLong(1, reminder.getId());
        statement.bindString(2, reminder.getDate());
        statement.bindString(3, reminder.getComment());
        statement.bindLong(4, reminder.getHour());
        statement.bindLong(5, reminder.getMinute());
        statement.bindLong(6, reminder.getMode());
        statement.bindLong(7, reminder.getDelta());
        statement.bindString(8, reminder.getTag());
        statement.executeInsert();
        return reminder;
    }

    /*
     * Change some reminder
     * if reminder with input reminder id does not exist, then dataBase addReminder it
     */
    @Override
    public Reminder update(Reminder reminder) {
        String sql = "REPLACE INTO reminder_database(id, date, comment, hour, minute, mode, delta, tag) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = write.compileStatement(sql);
        statement.bindLong(1, reminder.getId());
        statement.bindString(2, reminder.getDate());
        statement.bindString(3, reminder.getComment());
        statement.bindLong(4, reminder.getHour());
        statement.bindLong(5, reminder.getMinute());
        statement.bindLong(6, reminder.getMode());
        statement.bindLong(7, reminder.getDelta());
        statement.bindString(8, reminder.getTag());
        statement.executeUpdateDelete();
        return reminder;
    }

    /*
     * Delete some reminder
     * if reminder with input reminder id does not exist, then nothing is changes
     */
    @Override
    public Reminder delete(Reminder reminder) {
        String sql = "DELETE FROM reminder_database WHERE id = ?";
        SQLiteStatement statement = write.compileStatement(sql);
        statement.bindLong(1, reminder.getId());
        statement.executeUpdateDelete();
        return reminder;
    }

    /*
     * Return list of all reminders
     * or empty list if the file is empty
     */
    @Override
    public List<Reminder> findAll() {
        String[] projection = {
                FeedReaderContract.FeedEntry.ID,
                FeedReaderContract.FeedEntry.DATE,
                FeedReaderContract.FeedEntry.COMMENT,
                FeedReaderContract.FeedEntry.HOUR,
                FeedReaderContract.FeedEntry.MINUTE,
                FeedReaderContract.FeedEntry.MODE,
                FeedReaderContract.FeedEntry.DELTA,
                FeedReaderContract.FeedEntry.TAG
        };
        Cursor cursor = read.query(FeedReaderContract.FeedEntry.TABLE_NAME, projection, null, null, null, null, null);
        List<Reminder> reminders = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.ID));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.DATE));
            String comment = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COMMENT));
            int hour = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.HOUR));
            int minute = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.MINUTE));
            int mode = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.MODE));
            long delta = cursor.getLong(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.DELTA));
            String tag = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.TAG));
            reminders.add(new Reminder(id, date, comment, hour, minute, mode, delta, tag));
        }
        cursor.close();
        return reminders;
    }
}