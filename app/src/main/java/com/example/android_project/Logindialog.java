package com.example.android_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Logindialog extends AppCompatActivity {

    private EditText editTextID, editTextPassword;
    private Button buttonlogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logindialog);

        sharedPreferences = getSharedPreferences("ID_PW", MODE_PRIVATE);

        editTextID = findViewById(R.id.username_Edit);
        editTextPassword = findViewById(R.id.password_Edit);
        buttonlogin = findViewById(R.id.button_start);

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        String inputID = editTextID.getText().toString();
        String inputPassword = editTextPassword.getText().toString();

        // 저장된 ID와 비밀번호 가져오기
        String storedID = sharedPreferences.getString("id", "");
        String storedPassword = sharedPreferences.getString("password", "");

        // 입력된 ID와 비밀번호가 저장된 값과 일치하는지 확인
        if (inputID.equals(storedID) && inputPassword.equals(storedPassword)) {
            // 로그인 성공
            Toast.makeText(Logindialog.this, "로그인 성공", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Logindialog.this, CategoryActivity.class));
        } else {
            // 로그인 실패
            Toast.makeText(Logindialog.this, "로그인 실패: 아이디 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}