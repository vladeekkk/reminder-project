package com.example.reminder;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class ReminderDAOImpl implements ReminderDAO {
    String dataBase = "DataBase.json";
    Context context;

    ReminderDAOImpl(Context context) {
        this.context = context;
    }

    /*
     * Add new reminder
     * if reminder with input reminder id exists, then nothing is changes
     */
    @Override
    public Reminder save(Reminder reminder) {
        List<Reminder> currentReminders = findAll();
        if (currentReminders == null) {
            return null; //some error
        }

        if (currentReminders.contains(reminder)) {
            return null; //already exist reminder with input reminder id
        }
        currentReminders.add(reminder);
        if (saveListReminders(currentReminders)) {
            return reminder;
        }
        return null;
    }

    /*
     * Change some reminder
     * if reminder with input reminder id does not exist, then nothing is changes
     */
    @Override
    public Reminder update(Reminder reminder) {
        List<Reminder> currentReminders = findAll();
        if (currentReminders == null) {
            return null; //some error
        }

        if (!currentReminders.contains(reminder)) {
            return null; //does not exist reminder with input reminder id
        }
        currentReminders.remove(reminder);
        currentReminders.add(reminder);
        if (saveListReminders(currentReminders)) {
            return reminder;
        }
        return null;
    }

    /*
     * Delete some reminder
     * if reminder with input reminder id does not exist, then nothing is changes
     */
    @Override
    public Reminder delete(Reminder reminder) {
        List<Reminder> currentReminders = findAll();
        if (currentReminders == null) {
            return null; //some error
        }

        for (Reminder item : currentReminders) {
            if (item.id == reminder.id) {
                currentReminders.remove(item);
            }
        }

        if (saveListReminders(currentReminders)) {
            return reminder;
        }
        return null;
    }

    /*
     * Return list of all reminders
     * or empty list if the file is empty
     */
    @Override
    public List<Reminder> findAll() {
        try (Reader reader = new InputStreamReader(context.getAssets().open(dataBase))) {
            if (!reader.ready()) {
                return new LinkedList<>(); //empty file
            } else {
                return new Gson().fromJson(reader, new TypeToken<List<Reminder>>() {
                }.getType());
            }
        } catch (IOException e) {
            return null;
        }
    }

    private boolean saveListReminders(List<Reminder> currentReminders) {
        try (Writer writer = new FileWriter(dataBase)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(currentReminders, writer);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}