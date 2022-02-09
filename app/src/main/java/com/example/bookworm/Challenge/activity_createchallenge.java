package com.example.bookworm.Challenge;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_main;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class activity_createchallenge extends AppCompatActivity {
    Button btn_search, btn_dupli;
    TextView tv_bookname, tv_selectdate_start, tv_selectdate_end;
    Button btn_confirm;
    Book selected_book; //선택한 책 객체
    Calendar Start_calendar;
    Calendar End_calendar;

    //액티비티 간 데이터 전달 핸들러(검색한 데이터의 값을 전달받는 매개체가 된다.)
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    this.selected_book = (Book) intent.getSerializableExtra("data");
                    tv_bookname.setText(selected_book.getTitle()); //책 제목만 세팅한다.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createchallenge);

        btn_search = findViewById(R.id.btn_createchallenge_search);
        btn_dupli = findViewById(R.id.btn_createchallenge_duplicheck);
        tv_bookname = findViewById(R.id.tv_createchallenge_bookname);
        tv_selectdate_start = findViewById(R.id.tv_createchallenge_selectdate_start);
        tv_selectdate_end = findViewById(R.id.tv_createchallenge_selectdate_end);

        DatePickerDialog.OnDateSetListener StartDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Start_calendar.set(Calendar.YEAR, year);
                Start_calendar.set(Calendar.MONTH, month);
                Start_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(tv_selectdate_start, Start_calendar);
            }
        };

        DatePickerDialog.OnDateSetListener EndDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                End_calendar.set(Calendar.YEAR, year);
                End_calendar.set(Calendar.MONTH, month);
                End_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(tv_selectdate_end, End_calendar);
            }
        };


        tv_bookname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBook();
            }
        });
        btn_search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBook();
                btn_search.clearFocus();
            }
        });
        tv_selectdate_start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(activity_createchallenge.this, StartDatePicker, Start_calendar.get(Calendar.YEAR), Start_calendar.get(Calendar.MONTH), Start_calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //검색창을 열어서 책을 검색한다.
    public void getBook(){
        Intent intent = new Intent(this, search_fragment_subActivity_main.class);
        intent.putExtra("classindex", 2);
        startActivityResult.launch(intent); //검색 결과를 받는 핸들러를 작동한다.
    }


    private void updateLabel(TextView tv, Calendar calendar) {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        tv.setText(sdf.format(calendar.getTime()));
    }
}
