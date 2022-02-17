package com.example.bookworm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookworm.User.UserInfo;

public class ProfileModifyActivity extends AppCompatActivity {

    String strNickname, strProfile, strEmail;
    ImageView ivProfileImage;
    TextView Nickname;

    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modify);

        btnBack = findViewById(R.id.btnBack);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        Nickname = findViewById(R.id.tvNickname);

        Intent intent = this.getIntent();
        UserInfo userInfo = (UserInfo) intent.getSerializableExtra("userinfo");
        strNickname = userInfo.getUsername();
        strProfile = userInfo.getProfileimg();
        strEmail = userInfo.getEmail();

        Nickname.setText(strNickname);
        Glide.with(this).load(strProfile).circleCrop().into(ivProfileImage); //프로필사진 로딩후 삽입.

        //뒤로가기 버튼
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}