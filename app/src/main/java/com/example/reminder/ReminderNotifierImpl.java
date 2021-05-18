package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static android.app.AlarmManager.INTERVAL_DAY;

public class ReminderNotifierImpl extends BroadcastReceiver implements ReminderNotifier {
    private static Queue<Reminder> queue;
    private static PushReminder pushReminder;
    private static ReminderDAO reminderDAO;

    static final long INTERVAL_SECOND = 1000;
    static final long INTERVAL_MINUTE = 60;
    static final long INTERVAL_HOUR = 60;

    private long getRealTime() {
        LocalTime localTime = LocalTime.now();
        long hour = localTime.getHour();
        long minute = localTime.getMinute();
        long second = localTime.getSecond();

        return INTERVAL_SECOND * (hour * INTERVAL_HOUR * INTERVAL_MINUTE + minute * INTERVAL_MINUTE + second);
    }

    private long getReminderTime(Reminder reminder) {
        return INTERVAL_SECOND * (reminder.getHour() * INTERVAL_HOUR * INTERVAL_MINUTE + reminder.getMinute() * INTERVAL_MINUTE);
    }

    private void createQueueOfReminders(List<Reminder> reminders) {
        long realTime = getRealTime();
        queue = reminders.stream().
                sorted((o1, o2) -> {
                    long delta1 = (o1.getHour() * INTERVAL_HOUR + o1.getMinute()) * INTERVAL_MINUTE * INTERVAL_SECOND - realTime;
                    long delta2 = (o2.getHour() * INTERVAL_HOUR + o2.getMinute()) * INTERVAL_MINUTE * INTERVAL_SECOND - realTime;

                    delta1 = (delta1 + INTERVAL_DAY) % INTERVAL_DAY;
                    delta2 = (delta2 + INTERVAL_DAY) % INTERVAL_DAY;

                    return (int) (delta1 - delta2);
                }).collect(Collectors.toCollection(ArrayDeque::new));
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean init(ReminderDAO reminderDAO, Context context) {
        ReminderNotifierImpl.reminderDAO = reminderDAO;
        pushReminder = new PushReminderImpl(context);


        List<Reminder> reminders = reminderDAO.findAll();
        createQueueOfReminders(reminders);

        for (int i = 0; i < reminders.size(); i++) {
            Reminder reminder = reminders.get(i);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ReminderNotifierImpl.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, 0);

            long timeNotification = INTERVAL_SECOND * (reminder.getHour() * INTERVAL_HOUR * INTERVAL_MINUTE + reminder.getMinute() * INTERVAL_MINUTE);

            Date date;
            try {
                date = new SimpleDateFormat("dd.MM.yyyy").parse(reminder.getDate());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Date nowDate = new Date();

            long delta = Math.abs(date.getTime() - nowDate.getTime()) + timeNotification;
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delta, pendingIntent);
        }
        // after ten second app will send notification
        // but it doesn't work in background mode
        // and I don't know why

        // uncomment next four lines (read comments above)
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, ReminderNotifierImpl.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 10000, intent, 0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + 10000, pendingIntent);

        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (queue == null) {
            createQueueOfReminders(reminderDAO.findAll());
        }

        Reminder reminder = queue.poll();
        if (reminder == null) {
            throw new RuntimeException();
        }
        pushReminder.push(reminder);
    }
}
