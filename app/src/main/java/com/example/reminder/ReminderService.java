package com.example.reminder;

import java.io.IOException;

public interface ReminderService {
    Reminder save(Reminder reminder) throws IOException;

    Reminder update(Reminder reminder);

    Reminder delete(Reminder reminder);
}
