package com.example.reminder;


import android.os.Build;

import java.util.Arrays;
import java.util.Objects;

public class Reminder {
    int id;
    String date;
    String comment;
    long hour;
    long minute;

    public Reminder(int id, String date, String comment, long hour, long minute) {
        this.id = id;
        this.date = date;
        this.comment = comment;
        this.hour = hour;
        this.minute = minute;
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

    @Override
    public String toString() {
        return comment + '\n' + date;
//        return "Reminder{" +
//                "id=" + id +
//                ", date='" + date + '\'' +
//                ", comment='" + comment + '\'' +
//                '}';
    }
}