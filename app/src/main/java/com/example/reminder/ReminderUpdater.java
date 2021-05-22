package com.example.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.TimeZone;

public class ReminderUpdater extends Activity {
    private EditText reminderInfo;
    private Button saveReminderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_reminder);
        Reminder clickedReminder = new Reminder(getIntent().getStringExtra("clickedReminder"));
        reminderInfo = findViewById(R.id.reminder_info_text);

        DatePicker datePicker = findViewById(R.id.datePicker);
        datePicker.setMinDate(System.currentTimeMillis());

        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        ReminderServiceImpl reminderService = SingletonDataBaseService.getInstance().getDB();

        reminderInfo.setText(clickedReminder.getComment(), TextView.BufferType.EDITABLE);

        saveReminderBtn = findViewById(R.id.save_reminder_btn);
        saveReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        AlertDialog.Builder adb = new AlertDialog.Builder(ReminderUpdater.this);
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

                reminderService.update(new Reminder(clickedReminder.getId(),
                        date,
                        reminderInfo.getText().toString(),
                        timePicker.getHour(),
                        timePicker.getMinute(),
                        0,
                        0));
                Intent returnIntent = new Intent();
                setResult(1, returnIntent);
                finish();
            }
        });
    }
}

