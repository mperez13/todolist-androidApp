package com.maria.perez.com.todolist.data;
import android.provider.BaseColumns;
/**
 * Created by mark on 7/4/17.
 */
public class Contract {

    public static class TABLE_TODO implements BaseColumns{
        public static final String TABLE_NAME = "todoitems";

        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DUE_DATE = "duedate";

        // Added 2 columns for the category and completed
        public static final String COLUMN_NAME_COMPLETED = "completed";
        public static final String COLUMN_NAME_CATEGORY="category";
    }
}
