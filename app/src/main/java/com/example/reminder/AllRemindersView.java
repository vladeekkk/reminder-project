package com.example.reminder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AllRemindersView extends AppCompatActivity {
    private EditText reminderInfo;
    private EditText reminderDate;
    private Button saveReminderBtn;
    private int reminderId;
    private ReminderServiceImpl reminderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_of_all_reminders);
        reminderService = new ReminderServiceImpl(new ReminderDAOImpl(getApplicationContext()));

        List<String> listOfReminders = reminderService.findAll().stream().map(Object::toString).collect(Collectors.toList());
        List<String> listOfRemindersAllInformation = reminderService.findAll().stream().map(Reminder::getAllInformation).collect(Collectors.toList());

        ListView itemListView
                = (ListView) findViewById(R.id.list);

        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listOfReminders);

        ArrayAdapter<String> adapterAllInformation
                = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                listOfRemindersAllInformation);


        itemListView.setAdapter(adapter);
        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position,
                                           long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(AllRemindersView.this);
                adb.setTitle("Delete");
                adb.setMessage("Are you sure ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        reminderService.delete(new Reminder(adapterAllInformation.getItem(position)));
                        adapter.remove(adapter.getItem(position));
                        adapterAllInformation.remove(adapterAllInformation.getItem(position));
                        adapter.notifyDataSetChanged();
                    }
                });
                adb.show();
                return true;
            }
        });

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                           long id) {
                setContentView(R.layout.activity_create_reminder);
                Reminder clickedReminder = new Reminder(adapterAllInformation.getItem(position));

                reminderInfo = findViewById(R.id.reminder_info_text);
                reminderDate = findViewById(R.id.reminder_date_text);
                saveReminderBtn = findViewById(R.id.save_remider_btn);

                reminderInfo.setText(clickedReminder.getComment(), TextView.BufferType.EDITABLE);
                reminderDate.setText(clickedReminder.getDate(), TextView.BufferType.EDITABLE);

                saveReminderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reminderService.update(new Reminder(clickedReminder.getId(),
                                reminderInfo.getText().toString(),
                                reminderDate.getText().toString(),1,1));
                        onBackPressed();
                    }
                });
            }
        });
    }
}