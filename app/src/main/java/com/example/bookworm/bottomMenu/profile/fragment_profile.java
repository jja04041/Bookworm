package com.example.bookworm.bottomMenu.profile;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;

import com.bumptech.glide.Glide;
import com.example.bookworm.achievement.Achievement;
import com.example.bookworm.bottomMenu.bookworm.BookWorm;
import com.example.bookworm.bottomMenu.profile.views.ProfileSettingActivity;
import com.example.bookworm.databinding.FragmentProfileBinding;
import com.example.bookworm.extension.follow.view.FollowerActivity;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.userdata.PersonalD;

public class fragment_profile extends Fragment implements LifecycleObserver {

    private UserInfo userinfo;
    private BookWorm bookworm;
    private Achievement achievement;

    private FragmentProfileBinding binding;
    private Context current_context;
    private FBModule fbModule;
    UserInfoViewModel pv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        current_context = getActivity();
        pv= new UserInfoViewModel(current_context);
        fbModule = new FBModule(current_context);

        //뷰모델 안에서 데이터가 배치된다.

        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(current_context, ProfileSettingActivity.class);
                startActivity(intent);
            }
        });
        pv.getUser(null);


        //데이터 수정을 감지함
        pv.getData().observe(getViewLifecycleOwner(), userinfo -> {
            bookworm = new PersonalD(current_context).getBookworm();
            achievement = new Achievement(current_context, fbModule, userinfo, bookworm);
            binding.tvUserName.setText(userinfo.getUsername());


            binding.tvUserName.setOnClickListener(it->{
                pv.getFollowerList(userinfo.getToken(),0);
            });
            //팔로워액티비티 실행하기
            binding.btnFollower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(current_context, FollowerActivity.class);
                    intent.putExtra("token", userinfo.getToken());
                    intent.putExtra("page", 0);
                    startActivity(intent);
                }
            });

            //팔로잉액티비티 실행하기
            binding.btnFollowing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(current_context, FollowerActivity.class);
                    intent.putExtra("token", userinfo.getToken());
                    intent.putExtra("page", 1);
                    startActivity(intent);
                }
            });
            Glide.with(this).load(userinfo.getProfileimg()).circleCrop().into(binding.imgFragProfileProfile); //프로필사진 로딩후 삽입.
        });


        return view;
    }

    //뷰보다 프레그먼트의 생명주기가 길어서, 메모리 누수 발생가능
    //누수 방지를 위해 뷰가 Destroy될 때, binding값을 nullify 함.
    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

//    //장르를 세팅하는 함수
//    private void setGenre(String key) {
//        //로컬에서 업데이트
//        userinfo.setGenre(key, current_context);
//        //로컬 값 변경이후, 서버에 업데이트
//        Map map = new HashMap();
//        map.put("userinfo_genre", userinfo.getGenre());
//        fbModule.readData(0, map, userinfo.getToken());
//
//        achievement.CompleteAchievement(userinfo, current_context);
//    }
}