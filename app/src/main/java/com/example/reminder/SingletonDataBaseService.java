package com.example.reminder;

public class SingletonDataBaseService {
    private static SingletonDataBaseService instance;

    public static SingletonDataBaseService getInstance() {
        if (instance == null)
            instance = new SingletonDataBaseService();
        return instance;
    }

    private SingletonDataBaseService() {
    }

    private ReminderServiceImpl service;

    public ReminderServiceImpl getDB() {
        return service;
    }

    public void setValue(ReminderServiceImpl value) {
        this.service = value;
    }
}
