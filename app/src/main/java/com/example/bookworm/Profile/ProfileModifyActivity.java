package com.example.bookworm.Profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.databinding.ActivityProfileModifyBinding;
import com.example.bookworm.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileModifyActivity extends AppCompatActivity {

    private UserInfo userInfo;
    ActivityProfileModifyBinding binding;
    String strProfile;
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

        //shimmer 적용을 위해 기존 뷰는 일단 안보이게, shimmer는 보이게
        binding.llModify.setVisibility(View.GONE);
        binding.SFLModify.startShimmer();
        binding.SFLModify.setVisibility(View.VISIBLE);


        Glide.with(this).load(strProfile).circleCrop().into(ivProfileImage); //프로필사진 로딩후 삽입.

        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);

        Gson gson = new GsonBuilder().create();

        String key_user = pref.getString("key_user", null);

        try {
            JSONObject json = new JSONObject(key_user);
            userInfo = gson.fromJson(json.toString(), UserInfo.class);

            Nickname.setText(userInfo.getUsername());
            Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(ivProfileImage); //프로필사진 로딩후 삽입.

        } catch (JSONException e) {

        }

        //뒤로가기 버튼
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //shimmer 적용 끝내고 shimmer는 안보이게, 기존 뷰는 보이게
        binding.llModify.setVisibility(View.VISIBLE);
        binding.SFLModify.stopShimmer();
        binding.SFLModify.setVisibility(View.GONE);

    }
}