package com.example.android_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ScheduleListActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private ArrayList<ScheduleItem> scheduleList;
    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedulelistview);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // 일정 리스트 초기화 및 불러오기
        scheduleList = new ArrayList<>();
        refreshSchedules();

        // 저장 버튼 클릭 시 일정 추가 액티비티로 이동
        Button addButton = findViewById(R.id.Addschedulebutton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleListActivity.this, AddSchedule.class);
                startActivity(intent);
            }
        });
    }

    // 일정을 새로고침하는 메서드
    private void refreshSchedules() {
        // 일정 리스트 초기화
        scheduleList.clear();

        // 일정 불러오기
        loadSchedules();

        // 어댑터 초기화 및 설정
        adapter = new ScheduleAdapter(this, R.layout.scheduleitemlist, scheduleList);
        ListView listView = findViewById(R.id.listViewSchedules);
        listView.setAdapter(adapter);

        // 리스트뷰에서 항목을 클릭할 때 해당 일정의 내용을 보여주는 엑티비티로 이동
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 항목의 ScheduleItem을 가져옴
                ScheduleItem clickedItem = scheduleList.get(position);
                // 일정 내용을 보여주는 엑티비티로 이동
                Intent intent = new Intent(ScheduleListActivity.this, ScheduleContents.class);
                // 일정 ID를 넘겨줌
                intent.putExtra("schedule_id", clickedItem.getId());
                startActivity(intent);
            }
        });

        // 리스트뷰에서 항목을 길게 클릭할 때 삭제 기능을 수행
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 길게 클릭된 항목의 ScheduleItem을 가져옴
                final ScheduleItem itemToRemove = scheduleList.get(position);
                // 다이얼로그를 통해 삭제 여부를 물음
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleListActivity.this);
                builder.setMessage("스케줄을 삭제 하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 사용자가 확인을 선택한 경우 항목 삭제
                                removeItem(itemToRemove);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 사용자가 취소를 선택한 경우 다이얼로그 닫기
                                dialog.dismiss();
                            }
                        });
                // 다이얼로그 표시
                builder.create().show();
                return true; // 이벤트가 소비되었음을 반환
            }
        });
    }

    // 해당 카테고리의 일정을 DB에서 불러와서 리스트에 추가하는 메소드
    private void loadSchedules() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {DBHelper.COLUMN_SCHEDULE_ID, DBHelper.COLUMN_SCHEDULE_NAME, DBHelper.COLUMN_SCHEDULE_DATE};
        Cursor cursor = db.query(DBHelper.TABLE_SCHEDULES, projection, null, null, null, null, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SCHEDULE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SCHEDULE_NAME));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SCHEDULE_DATE));
            scheduleList.add(new ScheduleItem(id, name, date, false));
        }

        cursor.close();
    }

    // 항목을 리스트와 DB에서 삭제하는 메소드
    private void removeItem(ScheduleItem itemToRemove) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DBHelper.COLUMN_SCHEDULE_ID + "=?";
        String[] selectionArgs = {String.valueOf(itemToRemove.getId())};
        db.delete(DBHelper.TABLE_SCHEDULES, selection, selectionArgs);
        scheduleList.remove(itemToRemove);
        adapter.notifyDataSetChanged();
    }
}