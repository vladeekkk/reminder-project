package com.example.reminder;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class ReminderComparator implements Comparator<Reminder> {
    static final long INTERVAL_SECOND = 1000;
    static final long INTERVAL_MINUTE = 60;
    static final long INTERVAL_HOUR = 60;

    @SuppressLint("SimpleDateFormat")
    @Override
    public int compare(Reminder r1, Reminder r2) {
        Date date1;
        Date date2;
        try {
            date1 = new SimpleDateFormat("dd.MM.yyyy").parse(r1.getDate());
            date2 = new SimpleDateFormat("dd.MM.yyyy").parse(r2.getDate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        long delta1 = (r1.getHour() * INTERVAL_HOUR + r1.getMinute()) * INTERVAL_MINUTE * INTERVAL_SECOND;
        long delta2 = (r2.getHour() * INTERVAL_HOUR + r2.getMinute()) * INTERVAL_MINUTE * INTERVAL_SECOND;

        return (int) ((date1.getTime() + delta1) - (date2.getTime() + delta2));
    }
}
