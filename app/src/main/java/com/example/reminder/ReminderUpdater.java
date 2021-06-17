package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;
import java.util.TimeZone;

public class ReminderUpdater extends AppCompatActivity {
    private final String TIME_STRING = "time";
    private final String DATE_STRING = "date";

    private EditText reminderInfo;
    private EditText reminderTag;
    private final Calendar calendarCurrent = Calendar.getInstance(TimeZone.getDefault());
    private TimePicker timePicker;
    private final Calendar selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_reminder);
        setTitle("Update reminder");
        ReminderServiceImpl reminderService = SingletonDataBaseService.getInstance().getDB();
        Reminder clickedReminder = new Reminder(getIntent().getStringExtra("clickedReminder"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reminderInfo = findViewById(R.id.reminder_info_text);
        reminderInfo.setText(clickedReminder.getComment(), TextView.BufferType.EDITABLE);

        reminderTag = findViewById(R.id.reminder_tag_text);
        reminderTag.setText(clickedReminder.getTag(), TextView.BufferType.EDITABLE);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setHour((int) clickedReminder.getHour());

        timePicker.setMinute((int) clickedReminder.getMinute());

        TextView selectedDateText = findViewById(R.id.show_selected_date);
        selectedDateText.setText("Selected Date :\n" + clickedReminder.getDate());

        Button pickDateBtn = findViewById(R.id.pick_date_button);
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        pickDateBtn.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(),
                "MATERIAL_DATE_PICKER"));
        selectedCalendar.set(Integer.parseInt(clickedReminder.getDate().split("\\.")[2]), Integer.parseInt(clickedReminder.getDate().split("\\.")[1]) - 1 /*look at 77*/, Integer.parseInt(clickedReminder.getDate().split("\\.")[0]));
        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    selectedDateText.setText("Selected Date :\n" + materialDatePicker.getHeaderText());
                    selectedCalendar.setTimeInMillis((Long) selection);
                });

        Button saveReminderBtn = findViewById(R.id.save_reminder_btn);
        saveReminderBtn.setOnClickListener(v -> {
            if (checkNotEnoughInfo()) {
                sendAlert("");
                return;
            }
            if (checkEqualDates() && checkWrongTimeChoice()) {
                sendAlert(TIME_STRING);
                return;
            }
            if (checkWrongChoice() && !checkEqualDates()) {
                sendAlert(DATE_STRING);
                return;
            }
            @SuppressLint("DefaultLocale")
            String date = String.format("%02d", selectedCalendar.get(Calendar.DAY_OF_MONTH)) + '.' +
                    String.format("%02d", selectedCalendar.get(Calendar.MONTH) + 1 /*WHY!?*/) + '.' +
                    selectedCalendar.get(Calendar.YEAR);

            reminderService.update(new Reminder(clickedReminder.getId(),
                    date,
                    reminderInfo.getText().toString(),
                    timePicker.getHour(),
                    timePicker.getMinute(),
                    clickedReminder.getMode(),
                    clickedReminder.getDelta(),
                    reminderTag.getText().toString()));

            Intent returnIntent = new Intent();
            returnIntent.putExtra("reminder",
                    String.valueOf(clickedReminder.getId()) + '\n' +
                            date + '\n' +
                            reminderInfo.getText().toString() + '\n' +
                            timePicker.getHour() + '\n' +
                            timePicker.getMinute() + '\n' +
                            clickedReminder.getMode() + '\n' +
                            clickedReminder.getDelta() + '\n' +
                            reminderTag.getText().toString());
            returnIntent.putExtra("clickedReminder", getIntent().getStringExtra("clickedReminder"));
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }

    private void sendAlert(String errorType) {
        AlertDialog.Builder adb = new AlertDialog.Builder(ReminderUpdater.this);
        if (errorType.equals(DATE_STRING) || errorType.equals(TIME_STRING)) {
            adb.setTitle("Invalid " + errorType);
            adb.setMessage("You are trying to set the " + errorType + " that has already passed");
        } else {
            adb.setTitle("Invalid reminder");
            adb.setMessage("Please set more info!");
        }
        adb.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        adb.show();
    }

    private boolean checkNotEnoughInfo() {
        return selectedCalendar == null || reminderInfo.getText().toString().isEmpty();
    }

    private boolean checkWrongChoice() {
        return calendarCurrent.getTime().compareTo(selectedCalendar.getTime()) >= 0;
    }

    private boolean checkEqualDates() {
        return calendarCurrent.get(Calendar.DAY_OF_MONTH) == selectedCalendar.get(Calendar.DAY_OF_MONTH) &&
                calendarCurrent.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
                calendarCurrent.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR);
    }

    private boolean checkWrongTimeChoice() {
        return calendarCurrent.get(Calendar.HOUR_OF_DAY) > timePicker.getHour() ||
                (calendarCurrent.get(Calendar.HOUR_OF_DAY) == timePicker.getHour() &&
                        calendarCurrent.get(Calendar.MINUTE) >= timePicker.getMinute());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}

