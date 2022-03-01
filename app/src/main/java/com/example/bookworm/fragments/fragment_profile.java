package com.example.bookworm.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.bookworm.Login.activity_login.gsi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookworm.Login.activity_login;
import com.example.bookworm.ProfileInfoActivity;
import com.example.bookworm.ProfileSettingActivity;
import com.example.bookworm.R;
import com.example.bookworm.User.FollowerActivity;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.network.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

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
        userInfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.

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


        Glide.with(this).load(userInfo.getProfileimg()).circleCrop().into(imgProfile); //프로필사진 로딩후 삽입.
        return view;
    }
}