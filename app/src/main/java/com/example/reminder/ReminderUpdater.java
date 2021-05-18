package com.example.reminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ReminderUpdater extends Activity {
    private EditText reminderInfo;
    private EditText reminderDate;
    private Button saveReminderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_reminder);
        Reminder clickedReminder = new Reminder(getIntent().getStringExtra("clickedReminder"));
        reminderInfo = findViewById(R.id.reminder_info_text);
        reminderDate = findViewById(R.id.reminder_date_text);
        saveReminderBtn = findViewById(R.id.save_remider_btn);
        ReminderServiceImpl reminderService = SingletonDataBaseService.getInstance().getDB();

        reminderInfo.setText(clickedReminder.getComment(), TextView.BufferType.EDITABLE);
        reminderDate.setText(clickedReminder.getDate(), TextView.BufferType.EDITABLE);

        saveReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderService.update(new Reminder(clickedReminder.getId(),
                        reminderInfo.getText().toString(),
                        reminderDate.getText().toString(), 1, 1));
                Intent returnIntent = new Intent();
                setResult(1, returnIntent);
                finish();
            }
        });
    }
}

