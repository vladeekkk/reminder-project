package com.example.reminder;

import android.annotation.SuppressLint;
import android.os.Build;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.app.AlarmManager.INTERVAL_DAY;

public class Reminder {
    private final int id;
    private final String date;
    private final String comment;
    private final int hour;
    private final int minute;
    private final int mode;
    private final long delta;
    private final String tag;

    public static final int SIMPLE_MODE = 0;
    public static final int PERIOD_MODE = 1;
    public static final int EXP_MODE = 2;

    public static final int DAYS_IN_WEEK = 7;
    public static final int DAYS_IN_MONTH = 30;
    public static final int DAYS_IN_HALF_OF_YEAR = 180;

    public static long nextDelta(long delta) {
        if (delta == 0) {
            return INTERVAL_DAY;
        }
        if (delta == INTERVAL_DAY) {
            return TimeUnit.DAYS.toMillis(DAYS_IN_WEEK);
        }
        if (delta == TimeUnit.DAYS.toMillis(DAYS_IN_WEEK)) {
            return TimeUnit.DAYS.toMillis(DAYS_IN_MONTH);
        }
        if (delta == TimeUnit.DAYS.toMillis(DAYS_IN_MONTH)) {
            return TimeUnit.DAYS.toMillis(DAYS_IN_HALF_OF_YEAR);
        }
        return -1;
    }

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

    public int getMode() {
        return mode;
    }

    public long getDelta() {
        return delta;
    }

    public String getTag() {
        return tag;
    }

    public Reminder(int id, String date, String comment, int hour, int minute, int mode, long delta, String tag) {
        this.id = id;
        this.date = date;
        this.comment = comment;
        this.hour = hour;
        this.minute = minute;
        this.mode = mode;
        this.delta = delta;
        this.tag = tag;
    }

    public Reminder(String allInformation) {
        String[] allInformationSplited = allInformation.split("\n");
        this.id = Integer.parseInt(allInformationSplited[0]);
        this.date = allInformationSplited[1];
        this.comment = allInformationSplited[2];
        this.hour = Integer.parseInt(allInformationSplited[3]);
        this.minute = Integer.parseInt(allInformationSplited[4]);
        this.mode = Integer.parseInt(allInformationSplited[5]);
        this.delta = Long.parseLong(allInformationSplited[6]);
        this.tag = allInformationSplited[7];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return id == reminder.id && date.equals(reminder.date) && comment.equals(reminder.comment) &&
                hour == reminder.hour && minute == reminder.minute && mode == reminder.mode &&
                delta == reminder.delta;
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(id, date, comment, hour, minute, mode, delta);
        }
        return Arrays.hashCode(new int[]{Arrays.hashCode(new String[]{date, comment}), id, hour, minute, mode, (int) delta});
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return comment + '\n' + date + ' ' + String.format("%02d", hour) + ':' + String.format("%02d", minute);
    }

    public String getAllInformation() {
        return String.valueOf(id) + '\n' + date + '\n' + comment + '\n' +
                hour + '\n' + minute + '\n' + mode + '\n' + delta + '\n' + tag + ' ';
    }
}