package com.example.bookworm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookworm.User.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileModifyActivity extends AppCompatActivity {

    private UserInfo userInfo;

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
    }
}