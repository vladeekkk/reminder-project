package com.example.reminder;

import java.io.IOException;

public class ReminderServiceImpl implements ReminderService{
    private final ReminderDAO reminderDAO;

    public ReminderServiceImpl(ReminderDAO reminderDAO) {
        this.reminderDAO = reminderDAO;
    }

    @Override
    public Reminder save(Reminder reminder) throws IOException {
        return reminderDAO.save(reminder);
    }

    @Override
    public Reminder update(Reminder reminder) {
        return reminderDAO.update(reminder);
    }

    @Override
    public Reminder delete(Reminder reminder) {
        return reminderDAO.delete(reminder);
    }
}
