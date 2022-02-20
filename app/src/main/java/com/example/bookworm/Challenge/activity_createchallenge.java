package com.example.bookworm.Challenge;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_main;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class activity_createchallenge extends AppCompatActivity {
    Button btn_search, btn_dupli, btn_back;
    TextView tv_bookname, tv_challenge_start, tv_challenge_end;
    EditText et_challenge_date, et_challenge_name, et_challenge_max, et_challenge_info;
    Button btn_confirm, btn_start_challenge;
    ImageView Thumbnail;
    String strNickname, strProfile, strEmail; //회원정보 받아오기
    String strBookname, strChallengeName, strChallengeInfo, strChallengeStartDate, strChallengeEndDate, strCurrentParticipation, strMaxParticipation, strChallengeDate;
    Book selected_book; //선택한 책 객체
    Calendar Start_calendar;
    Calendar End_calendar;
    private FBModule fbModule;
    Context mContext;

    //액티비티 간 데이터 전달 핸들러(검색한 데이터의 값을 전달받는 매개체가 된다.)
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    this.selected_book = (Book) intent.getSerializableExtra("data");
                    tv_bookname.setText(selected_book.getTitle()); //책 제목만 세팅한다.
                    Glide.with(this).load(selected_book.getImg_url()).into(Thumbnail); //책 표지 로딩후 삽입.
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_challenge_createchallenge);

        btn_search = findViewById(R.id.btn_createchallenge_search);
        btn_dupli = findViewById(R.id.btn_createchallenge_duplicheck);
        btn_start_challenge = findViewById(R.id.btn_start_challenge);
        btn_back = findViewById(R.id.btnBack);
        tv_bookname = findViewById(R.id.tv_createchallenge_bookname);
        tv_challenge_start = findViewById(R.id.tv_createchallenge_start);
        tv_challenge_end = findViewById(R.id.tv_createchallenge_end);
        et_challenge_date = findViewById(R.id.et_createchallenge_challengedate);
        et_challenge_name = findViewById(R.id.et_createchallenge_challengename);
        et_challenge_max = findViewById(R.id.etMax);
        et_challenge_info = findViewById(R.id.et_createchallenge_challengeinfo);
        Thumbnail = findViewById(R.id.ivThumbnail);

        fbModule = new FBModule(mContext);
        mContext = this;

        //파이어베이스 챌린지 컬렉션에 유저이름과 프로필 URL을 올리기 위해 fragment_challenge.java에서 받아옴
        Intent intent = this.getIntent();
        strNickname = intent.getStringExtra("strNickname");
        strProfile = intent.getStringExtra("strProfile");


//        DatePickerDialog.OnDateSetListener StartDatePicker = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Start_calendar.set(Calendar.YEAR, year);
//                Start_calendar.set(Calendar.MONTH, month);
//                Start_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel(tv_selectdate_start, Start_calendar);
//            }
//        };
//
//        DatePickerDialog.OnDateSetListener EndDatePicker = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                End_calendar.set(Calendar.YEAR, year);
//                End_calendar.set(Calendar.MONTH, month);
//                End_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel(tv_selectdate_end, End_calendar);
//            }
//        };

        //챌린지 시작일에 오늘 날짜가 나오게 함
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        tv_challenge_start.setText(df.format(cal.getTime()));


        //챌린지 기간설정 EditText의 내용이 바뀔때 이벤트
        et_challenge_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //바뀌기 전 이벤트
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //바뀌는 동시에 이벤트
                String addDate = et_challenge_date.getText().toString();

                if (!addDate.equals("")) { //EditText가 공백이 아니면 적힌 값만큼 날짜를 더해서 출력
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    cal.add(Calendar.DATE, Integer.parseInt(addDate));
                    tv_challenge_end.setText(df.format(cal.getTime()));
                } else { //EditText가 공백이면 종료일이라고 출력
                    tv_challenge_end.setText("종료일");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //바뀌고 나서 이벤트
            }
        });

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
//        tv_selectdate_start.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog(activity_createchallenge.this, StartDatePicker, Start_calendar.get(Calendar.YEAR), Start_calendar.get(Calendar.MONTH), Start_calendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });

        //챌린지 시작 버튼
        btn_start_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChallenge();
            }
        });

        //뒤로가기 버튼
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //검색창을 열어서 책을 검색한다.
    public void getBook() {
        Intent intent = new Intent(this, search_fragment_subActivity_main.class);
        intent.putExtra("classindex", 2);
        startActivityResult.launch(intent); //검색 결과를 받는 핸들러를 작동한다.
    }

    private void updateLabel(TextView tv, Calendar calendar) {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        tv.setText(sdf.format(calendar.getTime()));
    }

    private void createChallenge() {
        //파이어베이스에 올릴것들
        strBookname = tv_bookname.getText().toString();
        strChallengeName = et_challenge_name.getText().toString();
        strChallengeInfo = et_challenge_info.getText().toString();
        strChallengeStartDate = tv_challenge_start.getText().toString();
        strChallengeEndDate = tv_challenge_end.getText().toString();
        strCurrentParticipation = "0";
        strMaxParticipation = et_challenge_max.getText().toString();
        strChallengeDate = et_challenge_date.getText().toString();

        //입력 안한 항목 있는지 찾기
        if(strBookname.equals("")||strChallengeName.equals("")||strChallengeInfo.equals("")||strMaxParticipation.equals("")||strChallengeDate.equals("")){
            Toast.makeText(this, "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
        }else{//다 입력 했다면
            HashMap<String, String> map = new HashMap<>();

            map.put("user_name", strNickname);
            map.put("ProfileURL", strProfile);
            map.put("thumbnailURL", selected_book.getImg_url());
            map.put("bookname", strBookname);
            map.put("BookId", selected_book.getItemId());
            map.put("strChallengeName", strChallengeName);
            map.put("challengeInfo", strChallengeInfo);
            map.put("challengeStartDate", strChallengeStartDate);
            map.put("ChallengeEndDate", strChallengeEndDate);
            map.put("CurrentParticipation", strCurrentParticipation);
            map.put("MaxParticipation", strMaxParticipation);

            //파이어베이스에 해당 챌린지명이 등록돼있지 않다면
            fbModule.readData(2, strChallengeName, map);
            finish();
            Toast.makeText(this, "챌린지 등록 성공", Toast.LENGTH_SHORT).show();
        }
    }

}
