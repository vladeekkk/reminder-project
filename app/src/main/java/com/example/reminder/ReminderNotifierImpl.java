package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ReminderNotifierImpl extends BroadcastReceiver implements ReminderNotifier {
    private static Deque<Reminder> queue;
    private static PushReminder pushReminder;
    private static ReminderDAO reminderDAO;
    private static boolean isInit = false;
    private Context context;

    @SuppressLint("SimpleDateFormat")
    private void createQueueOfReminders(List<Reminder> reminders) {
        queue = reminders.stream().
                sorted(new ReminderComparator()).
                collect(Collectors.toCollection(ArrayDeque::new));
    }

    @SuppressLint("SimpleDateFormat")
    private void setReminder(Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderNotifierImpl.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, queue.size(), intent, 0);

        Date date;
        try {
            date = new SimpleDateFormat("dd.MM.yyyy").parse(reminder.getDate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        long timeReminder = ReminderComparator.INTERVAL_SECOND * ReminderComparator.INTERVAL_MINUTE *
                (reminder.getHour() * ReminderComparator.INTERVAL_HOUR + reminder.getMinute());
        Date curDate = new Date();
        long delta = (date.getTime() - curDate.getTime()) + timeReminder;

        if (delta >= 0) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delta, pendingIntent);
        } else {
            reminderDAO.delete(reminder);
        }
//        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        intent = new Intent(context, ReminderNotifierImpl.class);
//        pendingIntent = PendingIntent.getBroadcast(context, 10000, intent, 0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
    }

    @Override
    public void init(Context cntx) {
        context = cntx;
        if (isInit) {
            return;
        }
        reminderDAO = new ReminderDAOImpl(context);
        pushReminder = new PushReminderImpl(context);

        List<Reminder> reminders = reminderDAO.findAll();
        createQueueOfReminders(reminders);

        for (Reminder reminder : reminders) {
            setReminder(reminder);
        }
        isInit = true;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void addReminder(Reminder reminder) {
        ReminderComparator reminderComparator = new ReminderComparator();
        Stack<Reminder> stack = new Stack<>();
        while (!queue.isEmpty() &&
                reminderComparator.compare(reminder, queue.getLast()) < 0) {
            stack.push(queue.pollLast());
        }
        queue.add(reminder);
        setReminder(reminder);

        while (!stack.isEmpty()) {
            queue.add(stack.pop());
        }
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (queue == null) {
            pushReminder = new PushReminderImpl(context);
            reminderDAO = new ReminderDAOImpl(context);
            createQueueOfReminders(reminderDAO.findAll());
        }

        Reminder reminder = queue.poll();
        if (reminder == null) {
            throw new RuntimeException("no reminders in data base");
        }
        pushReminder.push(reminder);
        reminderDAO.delete(reminder);
    }
}
