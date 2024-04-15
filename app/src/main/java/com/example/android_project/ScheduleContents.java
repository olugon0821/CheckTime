package com.example.android_project;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ScheduleContents extends AppCompatActivity {
    private DBHelper dbHelper;
    private int scheduleId;
    private TextView titleTextView;
    private TextView dateTextView;
    private LinearLayout checkboxContainer;
    private SharedPreferences preferences; // SharedPreferences 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_contents);

        dbHelper = new DBHelper(this);
        scheduleId = getIntent().getIntExtra("schedule_id", -1);
        titleTextView = findViewById(R.id.textViewscheduletitle);
        dateTextView = findViewById(R.id.textViewscheduledate);
        checkboxContainer = findViewById(R.id.checkboxContainer);
        preferences = getSharedPreferences("CheckboxPrefs", MODE_PRIVATE); // SharedPreferences 초기화

        loadScheduleInfo();
    }

    private void loadScheduleInfo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DBHelper.COLUMN_SCHEDULE_ITEM,
                DBHelper.COLUMN_SCHEDULE_DATE,
                DBHelper.COLUMN_SCHEDULE_NAME // 제목을 가져오기 위해 추가
        };
        String selection = DBHelper.COLUMN_SCHEDULE_ID + "=?";
        String[] selectionArgs = {String.valueOf(scheduleId)};
        Cursor cursor = db.query(DBHelper.TABLE_SCHEDULES, projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SCHEDULE_DATE));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SCHEDULE_NAME)); // 제목을 가져옴
            dateTextView.setText(date);
            titleTextView.setText(title); // 제목을 TextView에 설정

            // 아이템 이름 가져오기
            String itemString = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SCHEDULE_ITEM));
            String[] items = itemString.split(", ");
            for (String item : items) {
                boolean isChecked = preferences.getBoolean(scheduleId + "_" + item, false);
                addCheckbox(item, isChecked);
            }
        }

        cursor.close();
    }

    private void addCheckbox(String itemName, boolean initialChecked) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(itemName);
        checkBox.setChecked(initialChecked);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 체크박스 상태가 변경될 때 SharedPreferences에 상태를 저장합니다.
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(scheduleId + "_" + itemName, isChecked);
            editor.apply();
        });
        checkboxContainer.addView(checkBox);
    }
}