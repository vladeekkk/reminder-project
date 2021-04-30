package com.example.reminder;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.sql.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ReminderDAOImpl implements ReminderDAO {
    String dataBase = "jdbc:sqlite:identifier.sqlite";
    Connection connection;
    Context context = null;

    public ReminderDAOImpl() {
        this.connection = this.connect();
    }
    public ReminderDAOImpl(Context context) {
        new ReminderDAOImpl();
        this.context = context;
    }

    /*
     * Add new reminder
     * if reminder with input reminder id exists, then nothing is changes
     */
    @Override
    public Reminder save(Reminder reminder) {
        return updateData(reminder, "INSERT OR IGNORE INTO data_base(id, date, comment, hour, minute) VALUES(?, ?, ?, ?, ?)");
    }

    /*
     * Change some reminder
     * if reminder with input reminder id does not exist, then dataBase add it
     */
    @Override
    public Reminder update(Reminder reminder) {
        return updateData(reminder, "REPLACE INTO data_base(id, date, comment, hour, minute) VALUES (?, ?, ?, ?, ?)");
    }

    /*
     * Delete some reminder
     * if reminder with input reminder id does not exist, then nothing is changes
     */
    @Override
    public Reminder delete(Reminder reminder) {
        return updateData(reminder, "DELETE FROM data_base WHERE id = ?");
    }

    /*
     * Return list of all reminders
     * or empty list if the file is empty
     */
    @Override
    public List<Reminder> findAll() {
        List<Reminder> reminders = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, date, comment, hour, minute FROM data_base")) {
            while (rs.next()) {
                reminders.add(new Reminder(rs.getInt("id"),
                        rs.getString("date"),
                        rs.getString("comment"),
                        rs.getLong("hour"),
                        rs.getLong("minute")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reminders;
    }

    private Connection connect() {
        try {
            connection = DriverManager.getConnection(dataBase);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    private Reminder updateData(Reminder reminder, String sql) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int countParameter = pstmt.getParameterMetaData().getParameterCount();
            if (countParameter > 0) {
                pstmt.setInt(1, reminder.getId());
            }
            if (countParameter > 1) {
                pstmt.setString(2, reminder.getDate());
            }
            if (countParameter > 2) {
                pstmt.setString(3, reminder.getComment());
            }
            if (countParameter > 3) {
                pstmt.setLong(4, reminder.getHour());
            }
            if (countParameter > 4) {
                pstmt.setLong(5, reminder.getMinute());
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reminder;
    }
}