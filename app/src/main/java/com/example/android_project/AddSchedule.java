package com.example.android_project;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddSchedule extends AppCompatActivity {
    private EditText titleEditText;
    private EditText itemEditText;
    private Button addButton;
    private Button saveButton;
    private ListView itemListView;
    private ArrayList<String> itemsList;
    private ArrayAdapter<String> adapter;
    private DBHelper dbHelper;
    private ArrayList<Boolean> itemCheckedList;
    private CalendarView calendarView;
    private TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        dbHelper = new DBHelper(this);

        titleEditText = findViewById(R.id.titleEditText);
        itemEditText = findViewById(R.id.itemEditText);
        addButton = findViewById(R.id.addButton);
        saveButton = findViewById(R.id.saveButton);
        itemListView = findViewById(R.id.itemListView);
        dateTextView = findViewById(R.id.dateTextView);
        calendarView = findViewById(R.id.calendarView);

        itemsList = new ArrayList<>();
        itemCheckedList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsList);
        itemListView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            String newItem = itemEditText.getText().toString().trim();
            if (!newItem.isEmpty()) {
                itemsList.add(newItem);
                itemCheckedList.add(false);
                adapter.notifyDataSetChanged();
                itemEditText.setText("");
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // 사용자가 선택한 날짜를 TextView에 표시
                dateTextView.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
            }
        });

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String date = dateTextView.getText().toString().trim(); // 수정된 부분
            if (!title.isEmpty() && !date.isEmpty() && !itemsList.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.COLUMN_SCHEDULE_NAME, title);
                values.put(DBHelper.COLUMN_SCHEDULE_DATE, date);
                long result = dbHelper.insertSchedule(values, itemsList, itemCheckedList);
                if (result != -1) {
                    Toast.makeText(this, "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "오류 : 일정이 추가되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(AddSchedule.this, ScheduleListActivity.class);
                intent.putExtra("category_name", "");
                startActivity(intent);
            } else {
                Toast.makeText(this, "모든 영역을 기입해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}