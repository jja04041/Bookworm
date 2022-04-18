package com.example.bookworm.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookworm.Achievement.Achievement;
import com.example.bookworm.Bw.BookWorm;
import com.example.bookworm.ProfileSettingActivity;
import com.example.bookworm.Follow.View.FollowerActivity;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.databinding.FragmentProfileBinding;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.Core.UserData.PersonalD;

import java.util.HashMap;
import java.util.Map;

public class fragment_profile extends Fragment {

    private UserInfo userinfo;
    private BookWorm bookworm;
    private Achievement achievement;

    private FragmentProfileBinding binding;
    private Context current_context;
    private FBModule fbModule;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        current_context = getActivity();
        fbModule = new FBModule(current_context);
        userinfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.
        bookworm = new PersonalD(current_context).getBookworm();

        achievement = new Achievement(current_context, fbModule, userinfo, bookworm);

        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(current_context, ProfileSettingActivity.class);
                startActivity(intent);
            }
        });


        //팔로워액티비티 실행하기
        binding.btnFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowerActivity.class);
                intent.putExtra("token",userinfo.getToken());
                intent.putExtra("page",0);
                startActivity(intent);
            }
        });

        //팔로잉액티비티 실행하기
        binding.btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowerActivity.class);
                intent.putExtra("token",userinfo.getToken());
                intent.putExtra("page",1);
                startActivity(intent);
            }
        });

        // 지워도댐

        binding.btnH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGenre("공포");
            }
        });
        binding.btnDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGenre("추리");
            }
        });

        Glide.with(this).load(userinfo.getProfileimg()).circleCrop().into(binding.imgFragProfileProfile); //프로필사진 로딩후 삽입.

        return view;
    }
    //뷰보다 프레그먼트의 생명주기가 길어서, 메모리 누수 발생가능
    //누수 방지를 위해 뷰가 Destroy될 때, binding값을 nullify 함.
    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();

    }
    //장르를 세팅하는 함수
    private void setGenre(String key) {
        //로컬에서 업데이트
        userinfo.setGenre(key, current_context);
        //로컬 값 변경이후, 서버에 업데이트
        Map map = new HashMap();
        map.put("userinfo_genre", userinfo.getGenre());
        fbModule.readData(0, map, userinfo.getToken());

        achievement.CompleteAchievement(userinfo, current_context);
    }
}