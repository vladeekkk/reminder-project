package com.example.reminder;

import android.content.Context;

public interface ReminderNotifier {
    boolean init(ReminderDAO reminderDAO, Context context);
}
