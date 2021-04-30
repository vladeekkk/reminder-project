package com.example.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import static android.app.AlarmManager.INTERVAL_DAY;

public class ReminderNotifierImpl extends BroadcastReceiver implements ReminderNotifier {
    private static Queue<Reminder> queue;
    private static PushReminderImpl pushReminder;

    @Override
    public boolean init(List<Reminder> reminders, Context context) {
        final long INTERVAL_SECOND = 1000;
        final long INTERVAL_MINUTE = 60;
        final long INTERVAL_HOUR = 60;

        pushReminder = new PushReminderImpl(context);

        long currentTime = System.currentTimeMillis();

        LocalTime localTime = LocalTime.now();
        long hour = localTime.getHour();
        long minute = localTime.getMinute();
        long second = localTime.getSecond();

        long realTime = INTERVAL_SECOND * (hour * INTERVAL_HOUR * INTERVAL_MINUTE + minute * INTERVAL_MINUTE + second);

        queue = reminders.stream().
                sorted((o1, o2) -> {
                    long delta1 = (o1.hour * INTERVAL_HOUR + o1.minute) * INTERVAL_MINUTE * INTERVAL_SECOND - realTime;
                    long delta2 = (o2.hour * INTERVAL_HOUR + o2.minute) * INTERVAL_MINUTE * INTERVAL_SECOND - realTime;

                    delta1 = (delta1 + INTERVAL_DAY) % INTERVAL_DAY;
                    delta2 = (delta2 + INTERVAL_DAY) % INTERVAL_DAY;

                    return (int) (delta1 - delta2);
                }).collect(Collectors.toCollection(ArrayDeque::new));

        for (int i = 0; i < reminders.size(); i++) {
            Reminder reminder = reminders.get(i);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ReminderNotifierImpl.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, 0);

            long timeNotification = reminder.hour * INTERVAL_HOUR + reminder.minute;
            long delta = (INTERVAL_DAY + timeNotification * INTERVAL_MINUTE * INTERVAL_SECOND - realTime) % INTERVAL_DAY;

            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + delta, pendingIntent);
        }
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (queue == null) {
            throw new RuntimeException();
        }

        Reminder reminder = queue.poll();
        if (reminder == null) {
            throw new RuntimeException();
        }
        pushReminder.push(reminder);
    }
}
