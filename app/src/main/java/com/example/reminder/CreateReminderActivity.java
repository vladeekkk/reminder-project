package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.io.IOException;

public class CreateReminderActivity extends AppCompatActivity {

    private EditText reminderInfo;
    private EditText reminderDate;
    private Button saveReminderBtn;
    private NumberPicker hourPicker;

    private int reminderId;
    private ReminderServiceImpl reminderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingletonDataBaseService.getInstance().setValue(new ReminderServiceImpl(new ReminderDAOImpl(getApplicationContext())));
        reminderService = SingletonDataBaseService.getInstance().getDB();

        setContentView(R.layout.activity_create_reminder);

        reminderInfo = findViewById(R.id.reminder_info_text);
        reminderDate = findViewById(R.id.reminder_date_text);
        saveReminderBtn = findViewById(R.id.save_reminder_btn);
        reminderId = reminderService.findAll().stream().mapToInt(Reminder::getId).max().orElse(0) + 1;

        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        ReminderNotifier reminderNotifier = new ReminderNotifierImpl();
        reminderNotifier.init(getApplicationContext());

        saveReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Reminder reminder = new Reminder(reminderId++,
                            reminderDate.getText().toString(),
                            reminderInfo.getText().toString(),
                            timePicker.getHour(),
                            timePicker.getMinute());
                    reminderService.save(reminder);
                    reminderNotifier.addReminder(reminder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}