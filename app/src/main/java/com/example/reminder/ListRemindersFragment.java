package com.example.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ListRemindersFragment extends Fragment {
    private static final String ARG_PARAM1 = "listToView";
    private static final String ARG_PARAM2 = "listToInfo";

    private ArrayList<String> remindersToView;
    private ArrayList<String> remindersToInfo;
    private boolean shouldRefreshOnResume;
    private ListView listView;
    private ArrayAdapter<String> listViewAdapter;
    private ArrayAdapter<String> lvaAllReminders;
    private ReminderServiceImpl reminderService;


    public ListRemindersFragment() {
    }

    public static ListRemindersFragment newInstance(ArrayList<String> info, ArrayList<String> allInfo) {
        ListRemindersFragment fragment = new ListRemindersFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PARAM1, info);
        args.putStringArrayList(ARG_PARAM2, allInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            remindersToView = getArguments().getStringArrayList(ARG_PARAM1);
            remindersToInfo = getArguments().getStringArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        SingletonDataBaseService.getInstance().setValue(new ReminderServiceImpl(new ReminderDAOImpl(getContext())));
        reminderService = SingletonDataBaseService.getInstance().getDB();

        View view = inflater.inflate(R.layout.list_reminders, container, false);

        listView = view.findViewById(R.id.listView2);

        listViewAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1, remindersToView);
        lvaAllReminders = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1, remindersToInfo);
        listView.setOnItemLongClickListener((parent, itemClicked, position, id) -> {
            AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
            adb.setTitle("Delete");
            adb.setMessage("Are you sure ?");
            adb.setNegativeButton("Cancel", null);

            adb.setPositiveButton("Ok", (dialog, which) -> {
                Reminder reminder = new Reminder(lvaAllReminders.getItem(position));
                reminderService.delete(reminder);
                listViewAdapter.remove(listViewAdapter.getItem(position));
                lvaAllReminders.remove(lvaAllReminders.getItem(position));
                listViewAdapter.notifyDataSetChanged();

                ReminderNotifier reminderNotifier = new ReminderNotifierImpl();
                reminderNotifier.init(getContext());
                reminderNotifier.deleteReminder(reminder);
            });
            adb.show();
            return true;
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Intent intent = new Intent(getContext(), ReminderUpdater.class);
                intent.putExtra("clickedReminder", lvaAllReminders.getItem(position));
                someActivityResultLauncher.launch(intent);
            }

            private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Reminder updateReminder = new Reminder((String) result.getData().getExtras().getCharSequence("reminder"));
                            Reminder updateReminderBefore = new Reminder((String) result.getData().getExtras().getCharSequence("clickedReminder"));
                            listViewAdapter.remove(updateReminderBefore.toString());
                            if (updateReminder.getTag().equals(updateReminderBefore.getTag())) {
                                listViewAdapter.add(updateReminder.toString());
                            }
                            listViewAdapter.notifyDataSetChanged();

                            if (updateReminder.getTag().equals(updateReminderBefore.getTag())) {
                                lvaAllReminders.add(updateReminder.getAllInformation());
                            }
                            lvaAllReminders.remove(updateReminderBefore.getAllInformation());
                            lvaAllReminders.notifyDataSetChanged();

                            listView.setAdapter(listViewAdapter);
                        }
                    });
        });
        listView.setAdapter(listViewAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefreshOnResume) {
            if (remindersToInfo.size() > 0) {
                SingletonDataBaseService.getInstance().setValue(new ReminderServiceImpl(new ReminderDAOImpl(getContext())));
                reminderService = SingletonDataBaseService.getInstance().getDB();
                List<Reminder> reminders = reminderService.findAll();
                reminders.removeIf(r -> !r.getTag().contentEquals(new Reminder(remindersToInfo.get(0)).getTag()));
                remindersToView = new ArrayList<>(reminders.stream().map(Reminder::toString).collect(Collectors.toList()));
                remindersToInfo = new ArrayList<>(reminders.stream().map(Reminder::getAllInformation).collect(Collectors.toList()));

                listViewAdapter = new ArrayAdapter<>(
                        getActivity(), android.R.layout.simple_list_item_1, remindersToView);
                lvaAllReminders = new ArrayAdapter<>(
                        getActivity(), android.R.layout.simple_list_item_1, remindersToInfo);
                listView.setAdapter(listViewAdapter);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

}
