package com.example.bookworm.Follow.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.bookworm.Follow.Modules.FollowPagerAdapter;
import com.example.bookworm.R;
import com.google.android.material.tabs.TabLayout;

public class FollowerActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        Intent intent=getIntent();
        String token = intent.getStringExtra("token");
        int selected= intent.getIntExtra("page",0);
        viewPager = findViewById(R.id.viewpager);
        FollowPagerAdapter adapter = new FollowPagerAdapter(this,getSupportFragmentManager(),token);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(selected).select();
    }
}