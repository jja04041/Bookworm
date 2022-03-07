package com.example.bookworm.Challenge.subActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Challenge.items.Challenge;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class subactivity_challenge_challengeinfo extends AppCompatActivity {

    Button btn_back, btn_join;
    TextView tv_bookname, tv_challenge_end, tv_Dday, tv_challenge_current, tv_challenge_max, tv_end_date, tv_creator, tv_current_participants, tv_challenge_description;
    FrameLayout board;
    ProgressBar progressBar;
    ImageView Thumbnail;
    Challenge challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_challenge_challengeinfo);

        btn_back = findViewById(R.id.btnBack);
        btn_join = findViewById(R.id.btn_challenge_join); // 챌린지 참여 버튼

        Thumbnail = findViewById(R.id.ivThumbnail); // 썸네일

        tv_challenge_end = findViewById(R.id.tv_challenge_end); // ~~~~에 종료됨
        tv_Dday = findViewById(R.id.Dday); // #일 남음
        tv_challenge_current = findViewById(R.id.tv_Current_participants); // 현재 참여자 #/#
        tv_challenge_max = findViewById(R.id.tv_max_participants); // 최대 참여자 #/#
        tv_bookname = findViewById(R.id.tv_challengeinfo_bookname); // 책 제목
        tv_end_date = findViewById(R.id.tv_challengeinfo_end); // 마감일
        tv_current_participants = findViewById(R.id.tv_challengeinfo_Current_participants); // 참여자 ##명
        tv_creator = findViewById(R.id.tv_challengeinfo_creator); // 생성자 닉네임
        tv_challenge_description = findViewById(R.id.tv_challenge_Description); // 챌린지 설명

        progressBar = findViewById(R.id.progress); // 현재 인원 프로그레스 바

        board = findViewById(R.id.FL_board); // 인증게시판

        //넘겨받은 값 챌린지 객체에 넣음
        Intent intent = getIntent();
        challenge = (Challenge) intent.getSerializableExtra("challengeInfo");

        Glide.with(this).load(challenge.getBookThumb()).into(Thumbnail);

        tv_challenge_end.setText(challenge.getEndDate());
        tv_Dday.setText(countdday(challenge.getEndDate()));
        tv_challenge_current.setText(String.valueOf(challenge.getCurrentPart().size())); // 받아온 ArrayList 의 길이를 넣음 (현재 참여 인원 수 )
        tv_challenge_max.setText(String.valueOf(challenge.getMaxPart()));
        tv_end_date.setText(challenge.getEndDate());
        tv_current_participants.setText(String.valueOf(challenge.getCurrentPart().size()));
        tv_creator.setText(challenge.getMaster());
        tv_challenge_description.setText(challenge.getChallengeDescription());

        tv_bookname.setText(challenge.getBookTitle()); // 책 제목
        tv_bookname.setSingleLine(true);    // 한줄로 표시하기
        tv_bookname.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        tv_bookname.setSelected(true);      // 선택하기

        progressBar.setProgress(challenge.getCurrentPart().size());
        progressBar.setMax(Integer.parseInt(String.valueOf(challenge.getMaxPart())));

    }

    public String countdday(String EndDate) { //D-day 계산
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar todaCal = Calendar.getInstance(); //오늘날짜 가져오기
            Calendar ddayCal = Calendar.getInstance(); //오늘날짜를 가져와 변경시킴

            int year, month, day;

            year = Integer.parseInt(EndDate.substring(0,4));
            month = Integer.parseInt(EndDate.substring(5,7));
            day = Integer.parseInt(EndDate.substring(8,10));

            month -= 1; // 받아온날짜에서 -1을 해줘야함.
            ddayCal.set(year,month,day);// D-day의 날짜를 입력

            long today = todaCal.getTimeInMillis()/86400000; //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
            long dday = ddayCal.getTimeInMillis()/86400000;
            long count = dday - today; // 오늘 날짜에서 dday 날짜를 빼주게 됩니다.

            if(count<0){
                return "종료된 챌린지입니다.";
            }else{
                return String.valueOf(count) + "일 남음";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "error";
        }
    }

}