package main.java.com.example.reminder;

import java.util.List;

public interface ReminderDAO {
    void save(Reminder reminder);

    void update(Reminder reminder);

    void delete(Reminder reminder);

    List<Reminder> findAll();
}
