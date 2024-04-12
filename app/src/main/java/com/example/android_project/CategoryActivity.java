package com.example.android_project;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private CategoryAdapter adapter;
    private ArrayList<CategoryItem> categoryList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // DBHelper 초기화
        dbHelper = new DBHelper(this);

        // 카테고리 리스트 초기화
        categoryList = new ArrayList<>();
        loadCategories();

        // 어댑터 초기화
        adapter = new CategoryAdapter(this, R.layout.itemlistview, categoryList);

        // 리스트뷰 설정
        ListView listView = findViewById(R.id.listViewCategories);
        listView.setAdapter(adapter);

        // 새로운 카테고리 추가 버튼 클릭 시 다이얼로그 보이기
        Button addCategoryButton = findViewById(R.id.addCategoryButton);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });

        // 리스트뷰에서 아이템을 클릭했을 때 해당 카테고리 정보를 다루는 액티비티로 이동
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(CategoryActivity.this, ScheduleListActivity.class);
            intent.putExtra("category_name", categoryList.get(position).getTitle());
            startActivity(intent);
        });

        // 리스트뷰에서 아이템을 길게 눌렀을 때 카테고리 삭제 다이얼로그 표시
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteCategoryDialog(position);
            return true;
        });
    }

    // 카테고리를 DB에서 불러와서 리스트에 추가하는 메소드
    private void loadCategories() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_CATEGORIES, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CATEGORY_NAME));
            int imageResource = R.drawable.trip; // 기본 이미지 리소스 ID 또는 DB에서 이미지를 가져와야 함
            categoryList.add(new CategoryItem(imageResource, id, name));
        }

        cursor.close();
        dbHelper.close();
    }

    // 새로운 카테고리를 추가하는 다이얼로그 보이기
    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("새로운 카테고리 추가");

        // 다이얼로그에 입력을 받을 수 있는 EditText 추가
        final EditText input = new EditText(this);
        builder.setView(input);

        // 다이얼로그에 확인 버튼 추가
        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoryName = input.getText().toString().trim();
                if (!categoryName.isEmpty()) {
                    addCategory(categoryName); // 입력된 카테고리를 리스트에 추가하는 메소드 호출
                }
            }
        });

        // 다이얼로그에 취소 버튼 추가
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // 다이얼로그 닫기
            }
        });

        // 다이얼로그 보이기
        builder.show();
    }

    // 카테고리를 DB에 추가하는 메소드
    private void addCategory(String categoryName) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CATEGORY_NAME, categoryName);

        long newRowId = dbHelper.insertCategory(values);
        if (newRowId != -1) {
            int imageResource = R.drawable.trip;
            // DB에 추가된 카테고리를 리스트에 추가
            categoryList.add(new CategoryItem((int) newRowId, imageResource, categoryName));
            adapter.notifyDataSetChanged();
        }
    }

    // 카테고리 삭제 다이얼로그 표시
    private void showDeleteCategoryDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("카테고리 삭제");
        builder.setMessage("이 카테고리를 삭제하시겠습니까?");

        // 확인 버튼을 누르면 해당 카테고리를 삭제하고 리스트뷰를 갱신하는 코드
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCategory(position);
            }
        });

        // 취소 버튼을 누르면 다이얼로그를 닫습니다.
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 다이얼로그를 보여줍니다.
        builder.show();
    }

    // 카테고리를 DB에서 삭제하는 메소드
    private void deleteCategory(int position) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DBHelper.COLUMN_CATEGORY_ID + "=?";
        String[] selectionArgs = {String.valueOf(categoryList.get(position).getId())};
        dbHelper.deleteCategory(db, selection, selectionArgs);
        db.close();

        categoryList.remove(position);
        adapter.notifyDataSetChanged();
    }
}