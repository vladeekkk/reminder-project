package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class CreateReminderActivity extends AppCompatActivity {

    private EditText remiderInfo;
    private EditText reminderDate;
    private Button saveReminderBtn;

    private int reminderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        remiderInfo = findViewById(R.id.reminder_info_text);
        reminderDate = findViewById(R.id.reminder_date_text);
        saveReminderBtn = findViewById(R.id.save_remider_btn);
        ReminderServiceImpl reminderService
                = new ReminderServiceImpl(new ReminderDAOImpl(getApplicationContext()));
        reminderId = reminderService.findAll().stream().mapToInt(Reminder::getId).max().orElse(0) + 1;
        ReminderNotifier reminderNotifier = new ReminderNotifierImpl();
        reminderNotifier.init(getApplicationContext());

        saveReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Reminder reminder = new Reminder(reminderId++,
                            reminderDate.getText().toString(),
                            remiderInfo.getText().toString(),14,56);
                    reminderService.save(reminder);
                    reminderNotifier.addReminder(reminder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}