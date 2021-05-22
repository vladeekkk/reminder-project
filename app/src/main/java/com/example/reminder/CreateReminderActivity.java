package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class CreateReminderActivity extends AppCompatActivity {

    private EditText reminderInfo;
    private Button saveReminderBtn;
    private Button modeBtn;

    private int reminderId;
    private ReminderServiceImpl reminderService;

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingletonDataBaseService.getInstance().setValue(new ReminderServiceImpl(new ReminderDAOImpl(getApplicationContext())));
        reminderService = SingletonDataBaseService.getInstance().getDB();

        setContentView(R.layout.activity_create_reminder);
        setupUI(findViewById(R.id.layout));

        ReminderNotifier reminderNotifier = new ReminderNotifierImpl();
        reminderNotifier.init(getApplicationContext());

        reminderId = reminderService.findAll().stream().mapToInt(Reminder::getId).max().orElse(0) + 1;

        DatePicker datePicker = findViewById(R.id.datePicker);
        datePicker.setMinDate(System.currentTimeMillis());

        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

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
                                                "You chose Simple data",
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
                menuMode.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        return;
                    }
                });
                menuMode.show();
            }
        });

        reminderInfo = findViewById(R.id.reminder_info_text);

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
                            timePicker.getMinute(),
                            mode[0],
                            delta[0]);
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