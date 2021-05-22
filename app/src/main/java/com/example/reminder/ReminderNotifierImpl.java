package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ReminderNotifierImpl extends BroadcastReceiver implements ReminderNotifier {
    private static Deque<Reminder> queue;
    private static PushReminder pushReminder;
    private static ReminderService reminderService;
    private static boolean isInit = false;
    private static Context context;

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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder.getId(), intent, 0);

        Date date;
        try {
            date = new SimpleDateFormat("dd.MM.yyyy").parse(reminder.getDate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        long timeReminder = DateUtils.HOUR_IN_MILLIS * reminder.getHour() +
                DateUtils.MINUTE_IN_MILLIS * reminder.getMinute();

        long delta = (date.getTime() - System.currentTimeMillis()) + timeReminder;

        if (delta >= 0) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delta, pendingIntent);
        } else {
            reminderService.delete(reminder);
        }
//        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        intent = new Intent(context, ReminderNotifierImpl.class);
//        pendingIntent = PendingIntent.getBroadcast(context, 10000, intent, 0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
    }

    @Override
    public void init(Context cntx) {
        context = cntx;
        reminderService = new ReminderServiceImpl(new ReminderDAOImpl(context));
        pushReminder = new PushReminderImpl(context);
        if (isInit) {
            return;
        }

        List<Reminder> reminders = reminderService.findAll();
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

    @Override
    public void deleteReminder(Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderNotifierImpl.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);

        ReminderComparator reminderComparator = new ReminderComparator();
        Stack<Reminder> stack = new Stack<>();
        while (!queue.isEmpty() &&
                reminderComparator.compare(reminder, queue.getLast()) != 0) {
            stack.push(queue.pollLast());
        }
        queue.pollLast();

        while (!stack.isEmpty()) {
            queue.add(stack.pop());
        }
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context cntx, Intent intent) {
        context = cntx;
        if (queue == null) {
            pushReminder = new PushReminderImpl(context);
            reminderService = new ReminderServiceImpl(new ReminderDAOImpl(context));
            createQueueOfReminders(reminderService.findAll());
        }

        Reminder reminder = queue.poll();
        if (reminder == null) {
            throw new RuntimeException("no reminders in data base");
        }
        pushReminder.push(reminder);
        reminderService.delete(reminder);

        if (reminder.getMode() == Reminder.PERIOD_MODE ||
                reminder.getMode() == Reminder.EXP_MODE) {
            SingletonDataBaseService.getInstance().setValue(new ReminderServiceImpl(new ReminderDAOImpl(context)));
            ReminderService reminderService = SingletonDataBaseService.getInstance().getDB();

            Date date;
            try {
                date = new SimpleDateFormat("dd.MM.yyyy").parse(reminder.getDate());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            long time = date.getTime();
            time += DateUtils.HOUR_IN_MILLIS * reminder.getHour() +
                    DateUtils.MINUTE_IN_MILLIS * reminder.getMinute();

            long delta = reminder.getMode() == Reminder.PERIOD_MODE ?
                    reminder.getDelta() : Reminder.nextDelta(reminder.getDelta());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time + delta);

            String dateString = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + '.' +
                    String.format("%02d", calendar.get(Calendar.MONTH) + 1) + '.' +
                    calendar.get(Calendar.YEAR);

            Reminder nextReminder = new Reminder(reminder.getId(),
                    dateString,
                    reminder.getComment(),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    reminder.getMode(),
                    delta);
            addReminder(nextReminder);
            try {
                reminderService.save(nextReminder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
