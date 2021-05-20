package com.example.reminder;

import android.annotation.SuppressLint;
import android.os.Build;

import java.util.Arrays;
import java.util.Objects;

public class Reminder {
    private final int id;
    private final String date;
    private final String comment;
    private final long hour;
    private final long minute;

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public long getMinute() {
        return minute;
    }
  
    public long getHour() {
        return hour;
    }

    public Reminder(int id, String date, String comment, long hour, long minute) {
        this.id = id;
        this.date = date;
        this.comment = comment;
        this.hour = hour;
        this.minute = minute;
    }

    public Reminder(String allInformation){
        String[] allInformationSplited = allInformation.split("\n");
        this.id = Integer.parseInt(allInformationSplited[0]);
        this.date = allInformationSplited[1];
        this.comment = allInformationSplited[2];
        this.hour = Integer.parseInt(allInformationSplited[3]);
        this.minute = Integer.parseInt(allInformationSplited[4]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return id == reminder.id && date.equals(reminder.date) && comment.equals(reminder.comment);
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(id, date, comment);
        }
        return Arrays.hashCode(new int[]{Arrays.hashCode(new String[]{date, comment}), id});
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return comment + '\n' + date + ' ' + String.format("%02d", hour) + ':' + String.format("%02d", minute);
    }

    public String getAllInformation() {
        return String.valueOf(id) + '\n' + date + '\n' + comment + '\n' + String.valueOf(hour) + '\n' + String.valueOf(minute);
    }
}