package com.example.android_project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;

public class AddSchedule extends AppCompatActivity {
    private EditText dateEditText;
    private EditText itemEditText;
    private Button addButton;
    private Button saveButton;
    private ListView itemListView;
    private ArrayList<String> itemsList;
    private ArrayAdapter<String> adapter;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        dateEditText = findViewById(R.id.EditTextdate);
        itemEditText = findViewById(R.id.itemEditText);
        addButton = findViewById(R.id.addButton);
        saveButton = findViewById(R.id.saveButton);
        itemListView = findViewById(R.id.itemListView);

        itemsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsList);
        itemListView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            String newItem = itemEditText.getText().toString().trim();
            if (!newItem.isEmpty()) {
                itemsList.add(newItem);
                adapter.notifyDataSetChanged();
                itemEditText.setText("");
            }
        });

        saveButton.setOnClickListener(v -> {
            // 여기서 입력한 정보를 처리하고 이전 엑티비티로 돌아가는 로직을 추가합니다.
        });

        // DatePickerDialog를 초기화하고, EditText를 클릭하면 DatePickerDialog를 보여줍니다.
        initializeDatePickerDialog();
        dateEditText.setOnClickListener(v -> showDatePickerDialog());
    }

    // DatePickerDialog를 초기화하는 메소드
    private void initializeDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // 선택한 날짜를 EditText에 표시합니다.
                    String selectedDate = selectedMonth + 1 + "/" + selectedDay + "/" + selectedYear;
                    dateEditText.setText(selectedDate);
                }, year, month, day);
    }

    // DatePickerDialog를 보여주는 메소드
    private void showDatePickerDialog() {
        datePickerDialog.show();
    }
}