package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private ReminderService reminderService;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Button logoutBtn;
    private Button updatePass;

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.reminder_item);

        logoutBtn = findViewById(R.id.logout_btn);
        updatePass = findViewById(R.id.update_pass_btn);

        reminderService = new ReminderServiceImpl(new ReminderDAOImpl(this));
        logoutBtn.setOnClickListener(v -> {
            database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference(auth.getCurrentUser().getUid());

            List<String> reminders = new ArrayList<>();
            for (Reminder reminder : reminderService.findAll()) {
                reminders.add(reminder.getAllInformation());
            }
            ref.setValue(reminders);

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            for (String reminder : reminders) {
                reminderService.delete(new Reminder(reminder));
            }

            finish();
        });

        updatePass.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdatePassword.class);
            startActivity(intent);
        });

        reminderNotifier = new ReminderNotifierImpl();
        reminderNotifier.init(getApplicationContext());


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