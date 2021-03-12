package main.java.com.example.reminder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class ReminderDAOImpl implements ReminderDAO {
    String dataBase = "DataBase.json";

    /*
     * Add new reminder
     * if reminder with input reminder id exists, then nothing is changes
     */
    @Override
    public boolean save(Reminder reminder) {
        List<Reminder> currentReminders = findAll();
        if (currentReminders == null) {
            return false; //some error
        }

        if (currentReminders.contains(reminder)) {
            return false; //already exist reminder with input reminder id
        }
        currentReminders.add(reminder);
        return saveListReminders(currentReminders);
    }

    /*
     * Change some reminder
     * if reminder with input reminder id does not exist, then nothing is changes
     */
    @Override
    public boolean update(Reminder reminder) {
        List<Reminder> currentReminders = findAll();
        if (currentReminders == null) {
            return false; //some error
        }

        if (!currentReminders.contains(reminder)) {
            return false; //does not exist reminder with input reminder id
        }
        currentReminders.remove(reminder);
        currentReminders.add(reminder);
        return saveListReminders(currentReminders);
    }

    /*
     * Delete some reminder
     * if reminder with input reminder id does not exist, then nothing is changes
     */
    @Override
    public boolean delete(Reminder reminder) {
        List<Reminder> currentReminders = findAll();
        if (currentReminders == null) {
            return false; //some error
        }

        if (!currentReminders.contains(reminder)) {
            return false; //does not exist reminder with input reminder id
        }
        currentReminders.remove(reminder);
        return saveListReminders(currentReminders);
    }

    /*
     * Return list of all reminders
     * ot empty list if the file is empty
     */
    @Override
    public List<Reminder> findAll() {
        try (Reader reader = new FileReader(dataBase)) {
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
