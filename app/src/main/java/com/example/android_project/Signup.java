package com.example.android_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import java.util.Calendar;
import android.widget.Toast;

public class Signup extends AppCompatActivity {

    private EditText editTextID, editTextPassword, editTextName, editTextBirthDate;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextID = findViewById(R.id.editTextID);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextBirthDate = findViewById(R.id.editTextBirthDate);
        button = findViewById(R.id.button);

        editTextBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        button.setOnClickListener(view -> signUp());
    }

    private void signUp(){
        String id = editTextID.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextName.getText().toString();
        String birthdate = editTextBirthDate.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("ID_PW", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("id", id);
        editor.putString("password", password);
        editor.putString("name", name);
        editor.putString("birthdate", birthdate);
        editor.apply();
        Toast.makeText(Signup.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Signup.this, MainActivity.class));
    }

    private void showDatePickerDialog(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String birthDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        editTextBirthDate.setText(birthDate);
                    }
                },
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }
}