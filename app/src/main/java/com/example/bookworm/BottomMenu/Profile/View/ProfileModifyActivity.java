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

        //shimmer 적용을 위해 기존 뷰는 일단 안보이게, shimmer는 보이게
        showShimmer(true);

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


        //shimmer 적용 끝내고 shimmer는 안보이게, 기존 뷰는 보이게
        showShimmer(false);

    }


    //shimmer을 켜고 끄고 하는 메소드
    private void showShimmer(Boolean bool) {
        if (bool) {
            binding.llModify.setVisibility(View.GONE);
            binding.SFLModify.startShimmer();
            binding.SFLModify.setVisibility(View.VISIBLE);
        } else {
            binding.llModify.setVisibility(View.VISIBLE);
            binding.SFLModify.stopShimmer();
            binding.SFLModify.setVisibility(View.GONE);
        }
    }

}