package com.example.android_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CategorySchedule.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_SCHEDULES = "schedules";

    // Column names for categories table
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";

    // Column names for schedules table
    public static final String COLUMN_SCHEDULE_ID = "schedule_id";
    public static final String COLUMN_SCHEDULE_NAME = "schedule_name";
    public static final String COLUMN_SCHEDULE_CATEGORY_ID = "category_id";
    public static final String COLUMN_SCHEDULE_DATE = "schedule_date";
    public static final String COLUMN_SCHEDULE_ITEM = "schedule_item";
    public static final String COLUMN_SCHEDULE_CHECKED = "schedule_checked";

    // SQL statement to create categories table
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CATEGORY_NAME + " TEXT)";

    // SQL statement to create schedules table
    private static final String CREATE_TABLE_SCHEDULES = "CREATE TABLE " + TABLE_SCHEDULES + "("
            + COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SCHEDULE_NAME + " TEXT,"
            + COLUMN_SCHEDULE_DATE + " TEXT,"
            + COLUMN_SCHEDULE_ITEM + " TEXT,"
            + COLUMN_SCHEDULE_CATEGORY_ID + " INTEGER,"
            + COLUMN_SCHEDULE_CHECKED + " INTEGER DEFAULT 0," // Add a comma here
            + " FOREIGN KEY (" + COLUMN_SCHEDULE_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + "))";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_SCHEDULES);

        // Insert default categories if the categories table is empty
        if (!categoriesExist(db)) {
            insertDefaultCategories(db);
        }
    }

    // Check if there are any categories in the database
    private boolean categoriesExist(SQLiteDatabase db) {
        String query = "SELECT COUNT(*) FROM " + TABLE_CATEGORIES;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);

        // Create tables again
        onCreate(db);
    }

    // Insert a new category into the database
    public long insertCategory(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_CATEGORIES, null, values);
    }

    // Delete a category from the database
    public int deleteCategory(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete(TABLE_CATEGORIES, selection, selectionArgs);
    }

    // Insert default categories into the database
    public void insertDefaultCategories(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        // Add default categories here
        String[] defaultCategories = {"Trip", "Work", "Study"};

        for (String category : defaultCategories) {
            values.put(COLUMN_CATEGORY_NAME, category);
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    // Check if the category already exists in the database
    private boolean isCategoryExists(SQLiteDatabase db, String categoryName) {
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{COLUMN_CATEGORY_NAME},
                COLUMN_CATEGORY_NAME + "=?", new String[]{categoryName}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public long insertSchedule(ContentValues values, ArrayList<String> itemsList, ArrayList<Boolean> itemCheckedList) {
        SQLiteDatabase db = getWritableDatabase();
        StringBuilder itemsStringBuilder = new StringBuilder();
        for (String item : itemsList) {
            itemsStringBuilder.append(item).append(", ");
        }
        String itemsString = itemsStringBuilder.toString().trim();
        values.put(COLUMN_SCHEDULE_ITEM, itemsString); // 아이템 문자열을 컬럼에 추가
        return db.insert(TABLE_SCHEDULES, null, values);
    }

    public void updateCheckboxStatus(int scheduleId, String itemName, boolean checked) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULE_CHECKED, checked ? 1 : 0);
        String selection = COLUMN_SCHEDULE_ID + "=? AND " + COLUMN_SCHEDULE_ITEM + "=?";
        String[] selectionArgs = {String.valueOf(scheduleId), itemName};
        db.update(TABLE_SCHEDULES, values, selection, selectionArgs);
    }
}