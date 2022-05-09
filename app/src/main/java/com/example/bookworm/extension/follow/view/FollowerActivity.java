package com.example.bookworm.extension.follow.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.bookworm.extension.follow.modules.FollowPagerAdapter;
import com.example.bookworm.R;
import com.google.android.material.tabs.TabLayout;

public class FollowerActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        int selected = intent.getIntExtra("page", 0);
        viewPager = findViewById(R.id.viewpager);
        FollowPagerAdapter adapter = new FollowPagerAdapter(this, getSupportFragmentManager(), token);
        viewPager.setAdapter(adapter);
        FollowViewModel fv = new ViewModelProvider(this, new FollowViewModel.Factory(this)).get(FollowViewModel.class);

        //전달받은 토큰을 이용하여 데이터를 조회한다 .
        fv.WithoutSuspendgetUser(token);

        //탭 레이아웃 구성
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        //팔로잉 팔로워가 변하면 데이터를 인식함
        fv.getData().observe(this, userInfo -> {
            tabLayout.getTabAt(0).setText(userInfo.getFollowerCounts() + " 팔로워");
            tabLayout.getTabAt(1).setText(userInfo.getFollowingCounts() + " 팔로잉");
            tabLayout.getTabAt(selected).select();
        });


    }
}