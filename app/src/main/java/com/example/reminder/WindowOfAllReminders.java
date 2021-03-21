package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.stream.Collectors;

public class WindowOfAllReminders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_of_all_reminders);

        ReminderDAOImpl reminderDAO = new ReminderDAOImpl(this);
        List<String> listOfReminders = reminderDAO.findAll().stream().map(Object::toString).collect(Collectors.toList());

        ListView itemListView
                = (ListView) findViewById(R.id.list);

        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listOfReminders);

        itemListView.setAdapter(adapter);
    }
}