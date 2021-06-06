package com.example.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class ListRemindersFragment extends Fragment {
    private static final String ARG_PARAM1 = "listToView";
    private static final String ARG_PARAM2 = "listToInfo";

    private ArrayList<String> remindersToView;
    private ArrayList<String> remindersToInfo;


    public ListRemindersFragment() {

    }

    // TODO: Rename and change types and number of parameters
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
        ReminderServiceImpl reminderService = SingletonDataBaseService.getInstance().getDB();
        View view = inflater.inflate(R.layout.list_reminders, container, false);

        ListView lv = view.findViewById(R.id.listView2);

        System.out.println(remindersToView);
        ArrayAdapter<String> lva = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, remindersToView);
        ArrayAdapter<String> lvaAllReminders = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, remindersToInfo);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position,
                                           long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                adb.setTitle("Delete");
                adb.setMessage("Are you sure ?");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Reminder reminder = new Reminder(lvaAllReminders.getItem(position));
                        reminderService.delete(reminder);
                        lva.remove(lva.getItem(position));
                        lvaAllReminders.remove(lvaAllReminders.getItem(position));
                        lva.notifyDataSetChanged();

                        ReminderNotifier reminderNotifier = new ReminderNotifierImpl();
                        reminderNotifier.init(getContext());
                        reminderNotifier.deleteReminder(reminder);
                    }
                });
                adb.show();
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                            lva.remove(updateReminderBefore.toString());
                            lva.add(updateReminder.toString());
                            lva.notifyDataSetChanged();

                            lvaAllReminders.add(updateReminder.getAllInformation());
                            lvaAllReminders.remove(updateReminderBefore.getAllInformation());
                            lvaAllReminders.notifyDataSetChanged();
                        }
                    });
        });
        lv.setAdapter(lva);
        lv.refreshDrawableState();
        return view;
    }

}
