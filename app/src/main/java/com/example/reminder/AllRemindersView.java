package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AllRemindersView extends AppCompatActivity {

    private ReminderServiceImpl reminderService;
    TabLayout tabLayout;

    private class ViewStateAdapter extends FragmentStateAdapter {
        ArrayList<ArrayList<String>> remindersByTag = new ArrayList<>();
        ArrayList<ArrayList<String>> remindersByTagAllInfo = new ArrayList<>();

        public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
            update();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return ListRemindersFragment.newInstance(remindersByTag.get(position), remindersByTagAllInfo.get(position));
        }

        @Override
        public int getItemCount() {
            return remindersByTag.size();
        }

        public void update() {
            List<String> tabLayoutNames = reminderService.findAll().stream().map(Reminder::getTag).distinct().collect(Collectors.toList());
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                if (!tabLayoutNames.contains(tabLayout.getTabAt(i).getText().toString())) {
                    tabLayout.removeTab(tabLayout.getTabAt(i));
                    i--;
                }
            }
            for (String reminder : reminderService.findAll().stream().map(Reminder::getTag).distinct().collect(Collectors.toList())) {
                boolean match = false;
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    if (tabLayout.getTabAt(i).getText().toString().equals(reminder)) {
                        match = true;
                    }
                }
                if (!match) {
                    tabLayout.addTab(tabLayout.newTab().setText(reminder));
                }
            }
            remindersByTag.clear();
            remindersByTagAllInfo.clear();
            remindersByTag = new ArrayList<>(tabLayout.getTabCount());
            remindersByTagAllInfo = new ArrayList<>(tabLayout.getTabCount());
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                List<Reminder> reminders = reminderService.findAll();
                int finalI = i;
                reminders.removeIf(r -> !r.getTag().contentEquals(tabLayout.getTabAt(finalI).getText()));
                remindersByTag.add(new ArrayList<>(reminders.stream().map(Reminder::toString).collect(Collectors.toList())));
                remindersByTagAllInfo.add(new ArrayList<>(reminders.stream().map(Reminder::getAllInformation).collect(Collectors.toList())));
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingletonDataBaseService.getInstance().setValue(new ReminderServiceImpl(new ReminderDAOImpl(getApplicationContext())));
        reminderService = SingletonDataBaseService.getInstance().getDB();
        setContentView(R.layout.activity_window_of_all_reminders);
        setTitle("All reminders");
        tabLayout = findViewById(R.id.tabLayout);

        final Dialog dialog = new Dialog(AllRemindersView.this);
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TimePicker timePicker = findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);


        ViewStateAdapter stateAdapter = new ViewStateAdapter(getSupportFragmentManager(), getLifecycle());
        final ViewPager2 viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(stateAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                stateAdapter.update();
                viewPager.setCurrentItem(tab.getPosition());
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                stateAdapter.update();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
}