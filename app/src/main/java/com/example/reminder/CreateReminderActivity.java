package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.Random;

public class CreateReminderActivity extends AppCompatActivity {

    private EditText remiderInfo;
    private EditText reminderDate;
    private Button saveReminderBtn;

    private int reminderId = 40; // TODO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Random rand = new Random();
        rand.setSeed(239);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        remiderInfo = findViewById(R.id.reminder_info_text);
        reminderDate = findViewById(R.id.reminder_date_text);
        saveReminderBtn = findViewById(R.id.save_remider_btn);

        saveReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderId = rand.nextInt();
                ReminderServiceImpl reminderService
                        = new ReminderServiceImpl(new ReminderDAOImpl(getApplicationContext()));
                try {
                    reminderService.save(new Reminder(reminderId,
                            remiderInfo.getText().toString(),
                            reminderDate.getText().toString(),1,1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}