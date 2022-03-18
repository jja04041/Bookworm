package com.example.bookworm.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;

import java.util.HashMap;
import java.util.Map;

public class fragment_profile extends Fragment {
    UserInfo userInfo;

    Button btnFollower, btnFollowing, btnSetting, btnd, btnh, btnde;
    ImageView imgProfile;
    Context current_context;
    FBModule fbModule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = view.findViewById(R.id.img_frag_profile_profile);
        btnSetting = view.findViewById(R.id.btnSetting);
        btnFollower = view.findViewById(R.id.btn_follower);
        btnFollowing = view.findViewById(R.id.btn_following);

        // 지워도됨
        btnd = view.findViewById(R.id.btn_d);
        btnh = view.findViewById(R.id.btn_h);
        btnde = view.findViewById(R.id.btn_de);


        current_context = getActivity();
        fbModule = new FBModule(current_context);
        userInfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.

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

        // 지워도댐

//        btnd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                userInfo.setGenre(0);
//            }
//        });
//        btnh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                userInfo.setGenre(1);
//            }
//        });
//        btnde.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                userInfo.setGenre(2);
//            }
//        });

        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(imgProfile); //프로필사진 로딩후 삽입.

        return view;
    }
    //장르를 세팅하는 함수
    private void setGenre(int idx) {
        //로컬에서 업데이트
        userInfo.setGenre(idx);
        //로컬 값 변경이후, 서버에 업데이트
        Map map = new HashMap();
        map.put("genre", userInfo.getGenre());
        fbModule.readData(0,map,userInfo.getToken());
    }
}