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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn;
    private TextView textView;
    private EditText editText;

    ReminderNotifierImpl reminderNotifier;

    @Override
    protected void onStart() {
        super.onStart();
        reminderNotifier = new ReminderNotifierImpl();
        List<Reminder> list = new ArrayList<>();

        LocalTime localTime = LocalTime.now();
        long hour = localTime.getHour();
        long minute = localTime.getMinute();

        if (minute + 2 >= 60) {
            hour++;
        }
        minute = (minute + 2) % 60;

        list.add(new Reminder(1, "01.01.1970", "work!", hour, minute));
        list.add(new Reminder(2, "09.01.1975", "study!", hour, minute + 1));

        reminderNotifier.init(list, this);
    }
  
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_item);

        Button showReminders = findViewById(R.id.all_reminders);
        showReminders.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_reminders:
                Intent intent = new Intent(this, AllRemindersView.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}