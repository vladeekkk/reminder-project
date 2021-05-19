package com.example.reminder;

import android.content.Context;

public interface ReminderNotifier {
    void init(Context cntx);

    void addReminder(Reminder reminder);
}
