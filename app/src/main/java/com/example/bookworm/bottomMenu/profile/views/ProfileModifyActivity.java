package com.example.bookworm.bottomMenu.profile.views;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.PersonalD;
import com.example.bookworm.core.userdata.UserInfo;
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
        Context context=this;
        NowUser = new PersonalD(this).getUserInfo();
        binding.tvNickname.setText(NowUser.getUsername());
        Glide.with(this).load(NowUser.getProfileimg()).circleCrop().into(binding.ivProfileImage); //프로필사진 로딩후 삽입.
        liveImg=new MutableLiveData();
        UserInfoViewModel pv = new UserInfoViewModel(this);

        LiveData<Boolean> bool= pv.isDuplicated();

        //값이 변경 되면 알려줌
        bool.observe(this, value->{
            if(value==false) Toast.makeText(context,"사용 가능한 닉네임입니다.",Toast.LENGTH_SHORT).show();
            else Toast.makeText(context,"사용할 수 없는 닉네임입니다.",Toast.LENGTH_SHORT).show();
        });


        binding.checkDuplicate.setOnClickListener(it->{
            String name =binding.edtNewNickname.getText().toString();
            if(!name.contains(" ")&&!name.equals("")) pv.checkDuplicate(name);
            else Toast.makeText(context,"닉네임에는 공백을 넣을 수 없습니다.",Toast.LENGTH_SHORT).show();
        });


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