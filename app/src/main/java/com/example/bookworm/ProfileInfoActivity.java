package com.example.bookworm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Feed.items.Feed;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.User.followCounter;
import com.example.bookworm.databinding.ActivityProfileInfoBinding;
import com.example.bookworm.modules.personalD.PersonalD;

public class ProfileInfoActivity extends AppCompatActivity {

    ActivityProfileInfoBinding binding;
    UserInfo userInfo, nowUser; //타인 userInfo, 현재 사용자 nowUser
    Context context;

    //자신이나 타인의 프로필을 클릭했을때 나오는 화면
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        //작성자 UserInfo
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
        binding.tvNickname.setText(userInfo.getUsername());
        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(binding.ivProfileImage);

        //현재 사용자 UserInfo
        nowUser = new PersonalD(this).getUserInfo();

        //팔로우 버튼을 클릭했을때 버튼 모양, 상태 변경
        binding.tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.tvFollow.isSelected()) {
                    binding.tvFollow.setSelected(false);
                    binding.tvFollow.setText("팔로우");
                    new followCounter().unfollow(userInfo, nowUser, context);

                } else {
                    binding.tvFollow.setSelected(true);
                    binding.tvFollow.setText("팔로잉");
                    new followCounter().follow(userInfo, nowUser, context);
                }
            }
        });

        //뒤로가기
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}