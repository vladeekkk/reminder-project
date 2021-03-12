package main.java.com.example.reminder;


import android.os.Build;

import java.util.Arrays;
import java.util.Objects;

public class Reminder {
    int id;
    String date;
    String comment;

    public Reminder(int id, String date, String comment) {
        this.id = id;
        this.date = date;
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return id == reminder.id;
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(id, date, comment);
        }
        return Arrays.hashCode(new int[]{Arrays.hashCode(new String[]{date, comment}), id});
    }
}