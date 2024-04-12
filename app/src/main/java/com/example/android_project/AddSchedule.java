package com.example.android_project;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AddSchedule extends AppCompatActivity {
    private EditText dateEditText;
    private EditText itemEditText;
    private Button addButton;
    private Button saveButton;
    private ListView itemListView;
    private ArrayList<String> itemsList;
    private ArrayAdapter<String> adapter;

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
    }
}