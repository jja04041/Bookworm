package com.example.bookworm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileInfoActivity extends AppCompatActivity {

    TextView tvfollow;
    Button back;

    //자신이나 타인의 프로필을 클릭했을때 나오는 화면
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        tvfollow = findViewById(R.id.tv_follow);
        back = findViewById(R.id.btnBack);

        //팔로우 버튼을 클릭했을때 버튼 모양, 상태 변경
        tvfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvfollow.isSelected()) {
                    tvfollow.setSelected(false);
                    tvfollow.setText("팔로우");
                } else {
                    tvfollow.setSelected(true);
                    tvfollow.setText("팔로잉");
                }
            }
        });

        //뒤로가기
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}