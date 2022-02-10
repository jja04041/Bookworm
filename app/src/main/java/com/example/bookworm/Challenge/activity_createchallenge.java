package com.example.bookworm.Challenge;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.bookworm.R;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class activity_createchallenge extends Activity {

    String bookid = "default";
    String bookname;
    Button btn_search, btn_dupli;
    public TextView tv_bookname;
    TextView tv_selectdate_start, tv_selectdate_end;
    Button btn_confirm;

    Calendar Start_calendar;
    Calendar End_calendar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createchallenge);

        btn_search = findViewById(R.id.btn_createchallenge_search);
        btn_dupli = findViewById(R.id.btn_createchallenge_duplicheck);
        tv_bookname= findViewById(R.id.tv_createchallenge_bookname);
        tv_selectdate_start =  findViewById(R.id.tv_createchallenge_selectdate_start);
        tv_selectdate_end =  findViewById(R.id.tv_createchallenge_selectdate_end);

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
                Intent intent=new Intent(getApplicationContext(), search_fragment_subActivity_main.class);
                intent.putExtra("classindex", 2);
                startActivity(intent);
            }
        });
        btn_search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), search_fragment_subActivity_main.class);
                intent.putExtra("classindex", 2);
                startActivity(intent);
                btn_search.clearFocus();
            }
        });

        tv_selectdate_start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(activity_createchallenge.this, StartDatePicker, Start_calendar.get(Calendar.YEAR), Start_calendar.get(Calendar.MONTH), Start_calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    //tv_bookname.setText(json.getString("title"));

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    public void Set_Bookid(TextView tv, String bookname)
    {
        tv.setText(bookname);
    }

    private void updateLabel(TextView tv, Calendar calendar) {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        tv.setText(sdf.format(calendar.getTime()));
    }
}
