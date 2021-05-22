package com.example.reminder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;
import java.util.stream.Collectors;

public class AllRemindersView extends AppCompatActivity {
    public final static int REQUEST_CODE = 1;

    private ReminderServiceImpl reminderService;
    private ArrayAdapter<String> adapter;
    private ListView itemListView;
    private ArrayAdapter<String> adapterAllInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_of_all_reminders);
        SingletonDataBaseService.getInstance().setValue(new ReminderServiceImpl(new ReminderDAOImpl(getApplicationContext())));
        reminderService = SingletonDataBaseService.getInstance().getDB();

        List<String> listOfReminders = reminderService.findAll()
                .stream().map(Object::toString).collect(Collectors.toList());
        List<String> listOfRemindersAllInformation = reminderService.findAll()
                .stream().map(Reminder::getAllInformation).collect(Collectors.toList());

        itemListView = findViewById(R.id.list);

        adapter = new ArrayAdapter<>(
                this,
                R.layout.simple_list_item,
                listOfReminders);

        adapterAllInformation = new ArrayAdapter<>(
                this,
                R.layout.simple_list_item,
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
                        Reminder reminder = new Reminder(adapterAllInformation.getItem(position));
                        reminderService.delete(reminder);
                        adapter.remove(adapter.getItem(position));
                        adapterAllInformation.remove(adapterAllInformation.getItem(position));
                        adapter.notifyDataSetChanged();

                        ReminderNotifier reminderNotifier = new ReminderNotifierImpl();
                        reminderNotifier.init(getApplicationContext());
                        reminderNotifier.deleteReminder(reminder);
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
                Intent intent = new Intent(AllRemindersView.this, ReminderUpdater.class);
                intent.putExtra("clickedReminder", adapterAllInformation.getItem(position));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.clear();
        adapter.addAll(reminderService.findAll().stream().map(Object::toString).collect(Collectors.toList()));
        adapter.notifyDataSetChanged();

        adapterAllInformation.clear();
        adapterAllInformation.addAll(reminderService.findAll().stream().map(Reminder::getAllInformation).collect(Collectors.toList()));
        adapterAllInformation.notifyDataSetChanged();
    }
}