package com.example.android_project;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class ScheduleContents extends AppCompatActivity {
    private DBHelper dbHelper;
    private int scheduleId;
    private TextView titleTextView;
    private TextView dateTextView;
    private LinearLayout checkboxContainer;
    private SharedPreferences preferences; // SharedPreferences 추가
    private PendingIntent alarmIntent;
    private AlarmManager alarmManager;
    private Switch switchAlarm;
    private DatePicker datePicker;
    private Switch alarmSwitch;
    private TimePicker timePicker;
    private static final String ALARM_SWITCH_PREF_KEY = "alarm_switch_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_contents);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);

        dbHelper = new DBHelper(this);
        scheduleId = getIntent().getIntExtra("schedule_id", -1);
        titleTextView = findViewById(R.id.textViewscheduletitle);
        dateTextView = findViewById(R.id.textViewscheduledate);
        checkboxContainer = findViewById(R.id.checkboxContainer);
        alarmSwitch = findViewById(R.id.switchAlarm);

        // SharedPreferences 초기화
        preferences = getSharedPreferences("CheckboxPrefs", MODE_PRIVATE);

        // 알람 매니저 초기화
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), PendingIntent.FLAG_IMMUTABLE);

        // 알람 스위치 초기화
        initAlarmSwitch();

        // 일정 정보 로드
        loadScheduleInfo();
    }

    private void initAlarmSwitch() {
        // SharedPreferences에서 알람 스위치 상태를 불러옵니다. 기본값은 false(끔)로 설정합니다.
        boolean isAlarmOn = preferences.getBoolean(ALARM_SWITCH_PREF_KEY, false);

        // 알람 스위치를 초기 상태를 설정합니다.
        alarmSwitch.setChecked(isAlarmOn);

        // 알람 스위치의 상태가 변경될 때마다 SharedPreferences에 상태를 저장합니다.
        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(ALARM_SWITCH_PREF_KEY, isChecked);
            editor.apply();

            if (isChecked) {
                setAlarm();
            } else {
                cancelAlarm();
            }
        });
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

    // 알람 설정 버튼을 누를 때 호출되는 메서드
    private void setAlarm() {
        boolean allChecked = true; // 모든 체크박스가 체크되었는지 확인하기 위한 변수

        // 모든 체크박스를 확인하여 하나라도 체크되어 있지 않으면 allChecked를 false로 설정합니다.
        for (int i = 0; i < checkboxContainer.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) checkboxContainer.getChildAt(i);
            if (!checkBox.isChecked()) {
                allChecked = false;
                break;
            }
        }

        // 모든 체크박스가 체크되어 있지 않은 경우에만 알람을 설정합니다.
        if (!allChecked) {
            // 사용자가 선택한 날짜 가져오기
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();

            // 사용자가 선택한 시간 가져오기
            int hour;
            int minute;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = timePicker.getHour();
                minute = timePicker.getMinute();
            } else {
                hour = timePicker.getCurrentHour();
                minute = timePicker.getCurrentMinute();
            }

            // Calendar 객체 생성하여 알람 시간 설정
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            long triggerTime = calendar.getTimeInMillis();

            // 알람 설정
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, alarmIntent);

            Toast.makeText(this, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 모든 체크박스가 체크되어 있으면 알람을 설정하지 않습니다.
            Toast.makeText(this, "모든 아이템이 체크되어 있습니다. 알람을 설정할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    private void cancelAlarm() {
        // 알람 매니저를 사용하여 알람을 취소합니다.
        alarmManager.cancel(alarmIntent);

        // 취소된 알람에 대한 메시지를 표시합니다.
        Toast.makeText(this, "알람이 취소되었습니다.", Toast.LENGTH_SHORT).show();
    }
}