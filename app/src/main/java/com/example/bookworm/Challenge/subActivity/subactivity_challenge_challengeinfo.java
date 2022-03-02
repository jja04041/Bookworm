package com.example.bookworm.Challenge.subActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookworm.Challenge.items.Challenge;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;

public class subactivity_challenge_challengeinfo extends AppCompatActivity {

    Button btn_back, btn_join;
    TextView tv_bookname, tv_challenge_end, tv_Dday, tv_challenge_current, tv_challenge_max, tv_end_date, tv_creator, tv_current_participants;
    FrameLayout board;
    ImageView Thumbnail;
    Challenge challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity_challenge_challengeinfo);

        btn_back = findViewById(R.id.btnBack);
        tv_bookname = findViewById(R.id.tv_challengeinfo_bookname); // 책 제목
        tv_challenge_end = findViewById(R.id.tv_challenge_end); // ~~~~에 종료됨
        tv_Dday = findViewById(R.id.Dday); // #일 남음
        tv_challenge_current = findViewById(R.id.tv_Current_participants); // 현재 참여자 #/#
        tv_challenge_max = findViewById(R.id.tv_max_participants); // 최대 참여자 #/#
        tv_end_date = findViewById(R.id.tv_challengeinfo_end); // 마감일
        tv_creator = findViewById(R.id.tv_challengeinfo_creator); // 생성자 닉네임
        btn_join = findViewById(R.id.btn_challenge_join); // 챌린지 참여 버튼
        board = findViewById(R.id.FL_board); // 인증게시판
        tv_current_participants = findViewById(R.id.tv_Current_participants); // 참여자 ##명

        Thumbnail = findViewById(R.id.ivThumbnail); // 썸네일

        Intent intent = getIntent();
        challenge = (Challenge) intent.getSerializableExtra("challengeInfo");

        tv_bookname.setText(challenge.getBookTitle());

    }
}