package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class CreateReminderActivity extends AppCompatActivity {

    private EditText reminderInfo;
    private Button saveReminderBtn;

    private int reminderId;
    private ReminderServiceImpl reminderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingletonDataBaseService.getInstance().setValue(new ReminderServiceImpl(new ReminderDAOImpl(getApplicationContext())));
        reminderService = SingletonDataBaseService.getInstance().getDB();

        setContentView(R.layout.activity_create_reminder);

        reminderInfo = findViewById(R.id.reminder_info_text);
        reminderInfo.setSelection(0);

        reminderId = reminderService.findAll().stream().mapToInt(Reminder::getId).max().orElse(0) + 1;

        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        DatePicker datePicker = findViewById(R.id.datePicker);
        datePicker.setMinDate(System.currentTimeMillis());

        ReminderNotifier reminderNotifier = new ReminderNotifierImpl();
        reminderNotifier.init(getApplicationContext());

        saveReminderBtn = findViewById(R.id.save_reminder_btn);
        saveReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    if (dayOfMonth == datePicker.getDayOfMonth() &&
                            month == datePicker.getMonth() &&
                            year == datePicker.getYear()) {
                        if (hour > timePicker.getHour() ||
                                (hour == timePicker.getHour() && minute >= timePicker.getMinute())) {

                            AlertDialog.Builder adb = new AlertDialog.Builder(CreateReminderActivity.this);
                            adb.setTitle("Invalid time");
                            adb.setMessage("You are trying to set the time that has already passed");
                            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            adb.show();
                            return;
                        }
                    }

                    String date = String.format("%02d", datePicker.getDayOfMonth()) + '.' +
                            String.format("%02d", datePicker.getMonth() + 1) + '.' +
                            datePicker.getYear();
                    Reminder reminder = new Reminder(reminderId++,
                            date,
                            reminderInfo.getText().toString(),
                            timePicker.getHour(),
                            timePicker.getMinute());
                    reminderService.save(reminder);
                    reminderNotifier.addReminder(reminder);

                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}