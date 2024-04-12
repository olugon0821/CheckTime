package com.example.android_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ScheduleListActivity extends AppCompatActivity {
    private String categoryName;
    private DBHelper dbHelper;
    private ArrayList<ScheduleItem> scheduleList;
    private ScheduleAdapter adapter;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedulelistview);

        Button button = (Button)findViewById(R.id.Addschedulebutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleListActivity.this, AddSchedule.class);
                startActivity(intent);
            }
        });

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // 이전 액티비티에서 전달한 카테고리 이름 받기
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("category_name");

        // 일정 리스트 초기화
        scheduleList = new ArrayList<>();

        // 일정 불러오기
        loadSchedules();

        // 어댑터 초기화
        adapter = new ScheduleAdapter(this, R.layout.itemlistview, scheduleList);

        // 리스트뷰 설정
        ListView listView = findViewById(R.id.listViewSchedules);
        listView.setAdapter(adapter);
    }

    // 해당 카테고리의 일정을 DB에서 불러와서 리스트에 추가하는 메소드
    private void loadSchedules() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DBHelper.COLUMN_SCHEDULE_ID, DBHelper.COLUMN_SCHEDULE_NAME};
        String selection = DBHelper.COLUMN_SCHEDULE_CATEGORY_ID + "=?";
        String[] selectionArgs = {String.valueOf(getCategoryId(categoryName))};
        Cursor cursor = db.query(DBHelper.TABLE_SCHEDULES, projection, selection, selectionArgs, null, null, null);

        // 일정이 없는 경우를 처리하기 위해 빈 리스트를 생성하여 어댑터에 설정
        if (cursor.getCount() == 0) {
            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SCHEDULE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SCHEDULE_NAME));
            scheduleList.add(new ScheduleItem(id, name, "", false));
        }

        cursor.close();
    }

    // 카테고리 이름으로 카테고리 ID를 가져오는 메소드
    private int getCategoryId(String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DBHelper.COLUMN_CATEGORY_ID};
        String selection = DBHelper.COLUMN_CATEGORY_NAME + "=?";
        String[] selectionArgs = {categoryName};
        Cursor cursor = db.query(DBHelper.TABLE_CATEGORIES, projection, selection, selectionArgs, null, null, null);
        int categoryId = -1;
        if (cursor.moveToFirst()) {
            categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY_ID));
        }
        cursor.close();
        return categoryId;
    }
}