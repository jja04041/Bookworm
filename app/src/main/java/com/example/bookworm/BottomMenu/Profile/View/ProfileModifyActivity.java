package com.example.bookworm.BottomMenu.Profile.View;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.bookworm.Core.UserData.PersonalD;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.databinding.ActivityProfileModifyBinding;

public class ProfileModifyActivity extends AppCompatActivity {

    private UserInfo NowUser;
    ActivityProfileModifyBinding binding;
    private MutableLiveData liveImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NowUser = new PersonalD(this).getUserInfo();
        binding.tvNickname.setText(NowUser.getUsername());
        Glide.with(this).load(NowUser.getProfileimg()).circleCrop().into(binding.ivProfileImage); //프로필사진 로딩후 삽입.
        liveImg=new MutableLiveData();


        //데이터 변경이 감지되면 이미지의 소스를 변경함
        liveImg.observe(this, observer -> {
            Glide.with(binding.getRoot()).load(observer).into(binding.ivProfileImage);
        });

        //뒤로가기 버튼
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}