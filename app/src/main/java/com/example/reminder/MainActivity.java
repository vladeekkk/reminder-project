package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Intent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button addNewReminderBtn;
    private Button showReminders;

    private ReminderNotifier reminderNotifier;
    private ReminderDAO reminderDAO;

    @Override
    protected void onStart() {
        super.onStart();

        reminderNotifier = new ReminderNotifierImpl();
        reminderDAO = new ReminderDAOImpl(this);

//        reminderNotifier.init(reminderDAO, this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reminder_item);

        addNewReminderBtn = findViewById(R.id.new_reminder_btn);
        showReminders = findViewById(R.id.all_reminders);

        showReminders.setOnClickListener(this);
        addNewReminderBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.all_reminders:
                intent = new Intent(this, AllRemindersView.class);
                startActivity(intent);
                break;
            case R.id.new_reminder_btn:
                intent = new Intent(this, CreateReminderActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // examle of pushing a notification
//        PushReminderImpl reminderPusher = new PushReminderImpl(this);
//        reminderPusher.push(new Reminder(0, "03.11.2001", "homework",1,1));
    }
}