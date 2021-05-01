package com.example.reminder;

import android.content.Context;

import java.util.List;

public interface ReminderNotifier {
    boolean init(List<Reminder> reminders, Context context);
}
