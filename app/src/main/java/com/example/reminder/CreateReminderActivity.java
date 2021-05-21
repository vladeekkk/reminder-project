package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class CreateReminderActivity extends AppCompatActivity {

    private final String TIME_STRING = "time";
    private final String DATE_STRING = "date";

    private EditText reminderInfo;
    private TextView selectedDateText;

    private Calendar selectedCalendar;
    private Calendar calendar;
    private TimePicker timePicker;

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


        selectedDateText = findViewById(R.id.show_selected_date);
        Button pickDateBtn = findViewById(R.id.pick_date_button);
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        pickDateBtn.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(),
                "MATERIAL_DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    selectedDateText.setText("Selected Date is : " + materialDatePicker.getHeaderText());
                    selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    selectedCalendar.setTimeInMillis((Long) selection);
                });


        reminderId = reminderService.findAll().stream().mapToInt(Reminder::getId).max().orElse(0) + 1;

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        ReminderNotifier reminderNotifier = new ReminderNotifierImpl();
        reminderNotifier.init(getApplicationContext());

        Button saveReminderBtn = findViewById(R.id.save_reminder_btn);
        saveReminderBtn.setOnClickListener(v -> {
            try {
                calendar = Calendar.getInstance(TimeZone.getDefault());

                if (checkEqualDates() && checkWrongTimeChoice()) {
                    sendAlert(TIME_STRING);
                    return;
                }
                if (checkWrongChoice()) {
                    sendAlert(DATE_STRING);
                    return;
                }
                @SuppressLint("DefaultLocale")
                String date = String.format("%02d", selectedCalendar.get(Calendar.DAY_OF_MONTH)) + '.' +
                        String.format("%02d", selectedCalendar.get(Calendar.MONTH) + 1) + '.' +
                        selectedCalendar.get(Calendar.YEAR);

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
        });
    }

    void sendAlert(String errorType) {
        AlertDialog.Builder adb = new AlertDialog.Builder(CreateReminderActivity.this);
        adb.setTitle("Invalid " + errorType);
        adb.setMessage("You are trying to set the " + errorType + " that has already passed");
        adb.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        adb.show();

    }

    boolean checkWrongChoice() {
        return calendar.getTimeInMillis() > selectedCalendar.getTimeInMillis();
    }

    boolean checkEqualDates() {
        return calendar.get(Calendar.DAY_OF_MONTH) == selectedCalendar.get(Calendar.DAY_OF_MONTH) &&
                calendar.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
                calendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR);
    }

    boolean checkWrongTimeChoice() {
        return calendar.get(Calendar.HOUR_OF_DAY) > timePicker.getHour() ||
                (calendar.get(Calendar.HOUR_OF_DAY) == timePicker.getHour() && calendar.get(Calendar.MINUTE) >= timePicker.getMinute());
    }
}