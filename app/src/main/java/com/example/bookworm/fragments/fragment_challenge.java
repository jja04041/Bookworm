package com.example.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.bookworm.Challenge.activity_createchallenge;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;

public class fragment_challenge extends Fragment {

    String strNickname, strProfile, strEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

        Button btn_create_challenge = view.findViewById(R.id.btn_create_challenge);

        //로그인 액티비티에서 값 받아오기
        Intent intent = getActivity().getIntent();
        UserInfo userInfo = (UserInfo) intent.getSerializableExtra("userinfo");
        strNickname = userInfo.getUsername();
        strProfile = userInfo.getProfileimg();
        strEmail = userInfo.getEmail();


        btn_create_challenge.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), activity_createchallenge.class);
                //파이어베이스 챌린지 컬렉션에 유저이름과 프로필 URL을 올리기 위해 activity_login에서 받은 값을 activity_createchallenge.java로 넘겨줌
                intent.putExtra("strNickname",strNickname);
                intent.putExtra("strProfile",strProfile);
                startActivity(intent);
                btn_create_challenge.clearFocus();
            }
        });

        return view;
    }

}
