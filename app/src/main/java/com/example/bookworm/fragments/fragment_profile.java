package com.example.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.bookworm.R;
import com.example.bookworm.User.FollowerActivity;
import com.example.bookworm.User.FollowingActivity;

public class fragment_profile extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        //팔로워 버튼 클릭시 액티비티 전환
        Button btnFollower = (Button) getView().findViewById(R.id.btn_follower);
        btnFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowerActivity.class);
                startActivity(intent);
            }
        });

        //팔로잉 버튼 클릭시 액티비티 전환
        Button btnFollowing = (Button) getView().findViewById(R.id.btn_following);
        btnFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowingActivity.class);
                startActivity(intent);
            }
        });
    }
    //이건 맞는건가...
    private void setContentView(int fragment_profile) {
    }
}