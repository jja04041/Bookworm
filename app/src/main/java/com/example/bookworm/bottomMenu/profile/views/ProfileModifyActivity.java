package com.example.bookworm.bottomMenu.profile.views;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bookworm.bottomMenu.challenge.items.Challenge;
import com.example.bookworm.bottomMenu.profile.ChallengeViewModel;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.dataprocessing.image.ImageProcessing;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.ActivityProfileModifyBinding;

public class ProfileModifyActivity extends AppCompatActivity {

    private UserInfo NowUser;
    private ImageProcessing imageProcess;
    ActivityProfileModifyBinding binding;
    private boolean uploadCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileModifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Context context = this;


        imageProcess = new ImageProcessing(this);
        UserInfoViewModel pv = new ViewModelProvider(this, new UserInfoViewModel.Factory(context)).get(UserInfoViewModel.class);
        ChallengeViewModel cv = new ViewModelProvider(this, new ChallengeViewModel.Factory(context)).get(ChallengeViewModel.class);
        LiveData<Boolean> bool = pv.isDuplicated();
        pv.getUser(null, false);

        pv.getData().observe(this, userInfo -> {
            NowUser = userInfo;
            binding.tvNickname.setText(NowUser.getUsername());
            Glide.with(this)
                    .load(userInfo.getProfileimg())
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.ivProfileImage);

            binding.btnFavGenre.setOnClickListener(it -> {
                cv.getChallengeList(userInfo.getToken());
            });
            //프로필 이미지 변경
            imageProcess.getBitmapUri().observe(this, observer -> {
                Glide.with(binding.getRoot()).load(observer).circleCrop().into(binding.ivProfileImage);
            });

            //프로필이미지를 업로드 하는 경우
            imageProcess.getBitmap().observe(this, bitmap -> {
                //완료버튼을 누르면 이미지 업데이트
                binding.btnFinish.setOnClickListener(view -> {
                    String imgName = "profile_" + NowUser.getToken() + ".jpg";
                    imageProcess.uploadImage(bitmap, imgName); // 이미지 업로드
                });
                imageProcess.getImgData().observe(this, imgurl -> {
                    if (checkIdChanged()) {
                        NowUser.setProfileimg(imgurl);
                        pv.updateUser(NowUser);
                        finish();
                    } else {
                        Toast.makeText(this, "아이디 중복 체크 후 진행해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            //이미지 없이 업로드
            binding.btnFinish.setOnClickListener(view -> {
                if (uploadCheck) {
                    pv.updateUser(NowUser);
                    finish();
                } else {
                    Toast.makeText(this, "아이디 중복 체크 후 진행해 주세요.", Toast.LENGTH_SHORT).show();
                }
            });
            binding.edtNewNickname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    uploadCheck = false;
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    uploadCheck = checkIdChanged();
                }
            });


            //값이 변경 되면 알려줌
            bool.observe(this, value -> {
                if (value == false) {
                    NowUser.setUsername(binding.edtNewNickname.getText().toString());
                    uploadCheck = true;
                    Toast.makeText(context, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(context, "사용할 수 없는 닉네임입니다.", Toast.LENGTH_SHORT).show();
            });

        });

        cv.getChallengeList().observe(this, it -> {
            for (Challenge i : it) {
                Log.d("챌린지 데이터: ", i.getMaster());
            }
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

    private boolean checkIdChanged() {
        String name = binding.edtNewNickname.getText().toString();
        if (name.equals("")) return true;
        else if (!name.equals("") && !uploadCheck) return false;
        else return true;
    }


}