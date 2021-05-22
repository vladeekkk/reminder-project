package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class CreateReminderActivity extends AppCompatActivity {

    private final String TIME_STRING = "time";
    private final String DATE_STRING = "date";

    private EditText reminderInfo;
    private Button saveReminderBtn;
    private Button modeBtn;

    private TextView selectedDateText;

    private Calendar selectedCalendar;
    private Calendar calendarCurrent;
    private TimePicker timePicker;

    private int reminderId;
    private ReminderServiceImpl reminderService;

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(CreateReminderActivity.this);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingletonDataBaseService.getInstance().setValue(new ReminderServiceImpl(new ReminderDAOImpl(getApplicationContext())));
        reminderService = SingletonDataBaseService.getInstance().getDB();

        setContentView(R.layout.activity_create_reminder);
        setupUI(findViewById(R.id.layout));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ReminderNotifier reminderNotifier = new ReminderNotifierImpl();
        reminderNotifier.init(getApplicationContext());

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

        TextView textView = findViewById(R.id.text_view);

        final int[] mode = {Reminder.SIMPLE_MODE};
        final long[] delta = {0};

        modeBtn = findViewById(R.id.mode_btn);
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                PopupMenu menuMode = new PopupMenu(getApplicationContext(), v);
                menuMode.inflate(R.layout.mode_menu);

                menuMode
                        .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.menu_simple:
                                        Toast.makeText(getApplicationContext(),
                                                "You chose Simple reminder",
                                                Toast.LENGTH_SHORT).show();
                                        textView.setText("Mode: Simple reminder");
                                        mode[0] = Reminder.SIMPLE_MODE;
                                        return true;
                                    case R.id.menu_spaced_repetition:
                                        Toast.makeText(getApplicationContext(),
                                                "You chose Spaced repetition reminder",
                                                Toast.LENGTH_SHORT).show();
                                        textView.setText("Mode: Spaced repetition reminder");
                                        mode[0] = Reminder.EXP_MODE;
                                        return true;
                                    case R.id.submenu_day:
                                        Toast.makeText(getApplicationContext(),
                                                "You chose Repeated reminder(every day)",
                                                Toast.LENGTH_SHORT).show();
                                        textView.setText("Mode: Repeated reminder(every day)");
                                        mode[0] = Reminder.PERIOD_MODE;
                                        delta[0] = DateUtils.DAY_IN_MILLIS;
                                        return true;
                                    case R.id.submenu_week:
                                        Toast.makeText(getApplicationContext(),
                                                "You chose Repeated reminder(every week)",
                                                Toast.LENGTH_SHORT).show();
                                        textView.setText("Mode: Repeated reminder(every week)");
                                        mode[0] = Reminder.PERIOD_MODE;
                                        delta[0] = DateUtils.WEEK_IN_MILLIS;
                                        return true;
                                    case R.id.submenu_minute:
                                        Toast.makeText(getApplicationContext(),
                                                "You chose Repeated reminder(every minute)",
                                                Toast.LENGTH_SHORT).show();
                                        textView.setText("Mode: Repeated reminder(every minute)");
                                        mode[0] = Reminder.PERIOD_MODE;
                                        delta[0] = DateUtils.MINUTE_IN_MILLIS;
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                menuMode.setOnDismissListener(menu -> {
                    return;
                });
                menuMode.show();
            }
        });

        reminderInfo = findViewById(R.id.reminder_info_text);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        saveReminderBtn = findViewById(R.id.save_reminder_btn);
        saveReminderBtn.setOnClickListener(v -> {
            try {
                calendarCurrent = Calendar.getInstance(TimeZone.getDefault());
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
                        String.format("%02d", selectedCalendar.get(Calendar.MONTH) + 1) + '.' +
                        selectedCalendar.get(Calendar.YEAR);

                Reminder reminder = new Reminder(reminderId++,
                        date,
                        reminderInfo.getText().toString(),
                        timePicker.getHour(),
                        timePicker.getMinute(),
                        mode[0],
                        delta[0]);
                reminderService.save(reminder);
                reminderNotifier.addReminder(reminder);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendAlert(String errorType) {
        AlertDialog.Builder adb = new AlertDialog.Builder(CreateReminderActivity.this);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}