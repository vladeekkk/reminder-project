package com.example.reminder;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReminderComparator implements Comparator<Reminder> {
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

        long delta1 = DateUtils.HOUR_IN_MILLIS * r1.getHour() +
                DateUtils.MINUTE_IN_MILLIS * r1.getMinute();
        long delta2 = DateUtils.HOUR_IN_MILLIS * r2.getHour() +
                DateUtils.MINUTE_IN_MILLIS * r2.getMinute();

        return (int) ((date1.getTime() + delta1) - (date2.getTime() + delta2));
    }
}
