package com.example.bookworm.bottomMenu.profile.views;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.dataprocessing.image.ImageProcessing;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.ActivityProfileModifyBinding;

public class ProfileModifyActivity extends AppCompatActivity {

    private UserInfo NowUser;
    private ImageProcessing imageProcess;
    ActivityProfileModifyBinding binding;
    private boolean uploadCheck=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Context context = this;
//        NowUser = new PersonalD(this).getUserInfo();

        imageProcess = new ImageProcessing(this);
        UserInfoViewModel pv = new UserInfoViewModel(this);

        LiveData<Boolean> bool = pv.isDuplicated();
        pv.getUser(null, true);

        pv.getData().observe(this, userInfo -> {
            NowUser = userInfo;
            binding.tvNickname.setText(NowUser.getUsername());
            Glide.with(this)
                    .load(userInfo.getProfileimg())
                    .circleCrop()
                    .into(binding.ivProfileImage);


            //프로필 이미지 변경
            imageProcess.getBitmapUri().observe(this, observer -> {
                Glide.with(binding.getRoot()).load(observer).circleCrop().into(binding.ivProfileImage);
            });

            //프로필이미지를 업로드 하는 경우
            imageProcess.getBitmap().observe(this,bitmap -> {
                //완료버튼을 누르면 이미지 업데이트
                binding.btnFinish.setOnClickListener(view -> {
                    String imgName="profile_"+System.currentTimeMillis() + "_"+NowUser.getToken()+".jpg";
                    imageProcess.uploadImage(bitmap,imgName); // 이미지 업로드
                });
                imageProcess.getImgData().observe(this,imgurl->{
                    if(checkIdChanged()) {
                        NowUser.setProfileimg(imgurl);
                        pv.updateUser(NowUser);
                        finish();
                    }
                    else{
                        Toast.makeText(this,"다른 아이디를 입력하여 주세요.",Toast.LENGTH_SHORT).show();
                    }
                });
            });

            //이미지 없이 업로드
            binding.btnFinish.setOnClickListener(view -> {
                if (uploadCheck) {
                    pv.updateUser(NowUser);
                    finish();
                }
                else{
                    Toast.makeText(this,"다른 아이디를 입력하여 주세요.",Toast.LENGTH_SHORT).show();
                }
            });
            binding.edtNewNickname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    uploadCheck=false;
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    uploadCheck=checkIdChanged();
                }
            });

            //값이 변경 되면 알려줌
            bool.observe(this, value -> {
                if (value == false) {
                    NowUser.setUsername(binding.edtNewNickname.getText().toString());
                    uploadCheck=true;
                    Toast.makeText(context, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(context, "사용할 수 없는 닉네임입니다.", Toast.LENGTH_SHORT).show();
            });

        });

        binding.tvProfileImageModify.setOnClickListener(it -> {
            imageProcess.initProcess();
        });

        binding.checkDuplicate.setOnClickListener(it -> {
            String name = binding.edtNewNickname.getText().toString();
            if (!name.contains(" ") && !name.equals("")) pv.checkDuplicate(name);
            else Toast.makeText(context, "닉네임에는 공백을 넣을 수 없습니다.", Toast.LENGTH_SHORT).show();
        });


        //뒤로가기 버튼
        binding.btnBack.setOnClickListener(view -> finish());

    }
    private boolean checkIdChanged(){
        String name=binding.edtNewNickname.getText().toString();
        if(name.equals(""))return true;
        else if(!name.equals("")&&!uploadCheck) return false;
        else return true;
    }


}