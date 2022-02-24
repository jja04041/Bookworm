package com.example.bookworm.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookworm.ProfileSettingActivity;
import com.example.bookworm.R;
import com.example.bookworm.User.FollowerActivity;
import com.example.bookworm.User.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class fragment_profile extends Fragment {

    UserInfo userInfo;

    Button btnFollower;
    Button btnFollowing;
    ImageView imgProfile;
    Button btnSetting;
    Context current_context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = view.findViewById(R.id.img_frag_profile_profile);
        btnSetting = view.findViewById(R.id.btnSetting);
        btnFollower = view.findViewById(R.id.btn_follower);
        btnFollowing = view.findViewById(R.id.btn_following);
        current_context = getActivity();

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(current_context, ProfileSettingActivity.class);
                startActivity(intent);
            }
        });

        //팔로워액티비티 실행하기
        btnFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowerActivity.class);
                startActivity(intent);
            }
        });

        //팔로워액티비티 실행하기
        btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowerActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences pref = current_context.getSharedPreferences("user", MODE_PRIVATE);

        Gson gson = new GsonBuilder().create();

        String key_user = pref.getString("key_user", null);

        try {
            JSONObject json = new JSONObject(key_user);
            userInfo = gson.fromJson(json.toString(), UserInfo.class);

            Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(imgProfile); //프로필사진 로딩후 삽입.

        } catch (JSONException e) {

        }
        return view;
    }
}