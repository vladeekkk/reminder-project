package com.example.reminder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface ReminderDAO {
    Reminder save(Reminder reminder) throws IOException;

    Reminder update(Reminder reminder);

    Reminder delete(Reminder reminder);

    List<Reminder> findAll();
}
