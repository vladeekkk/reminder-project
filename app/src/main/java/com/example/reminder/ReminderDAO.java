package main.java.com.example.reminder;

import java.io.IOException;
import java.util.List;

public interface ReminderDAO {
    boolean save(Reminder reminder) throws IOException;

    boolean update(Reminder reminder);

    boolean delete(Reminder reminder);

    List<Reminder> findAll();
}
