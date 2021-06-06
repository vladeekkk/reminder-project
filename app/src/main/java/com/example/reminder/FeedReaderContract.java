package com.example.reminder;

import android.provider.BaseColumns;


public class FeedReaderContract {
    private FeedReaderContract() {

    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminder_database";
        public static final String ID = "id";
        public static final String DATE = "date";
        public static final String COMMENT = "comment";
        public static final String HOUR = "hour";
        public static final String MINUTE = "minute";
        public static final String MODE = "mode";
        public static final String DELTA = "delta";
        public static final String TAG = "tag";
    }
}
