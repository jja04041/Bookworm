package com.example.bookworm.Follow.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.bookworm.Follow.Modules.FollowPagerAdapter;
import com.example.bookworm.R;
import com.example.bookworm.databinding.ActivityFollowerBinding;
import com.google.android.material.tabs.TabLayout;

public class FollowerActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityFollowerBinding binding;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        Intent intent=getIntent();
        String token = intent.getStringExtra("token");
        int selected= intent.getIntExtra("page",0);
        viewPager = findViewById(R.id.viewpager);
        FollowPagerAdapter adapter = new FollowPagerAdapter(getSupportFragmentManager(),token);
        viewPager.setAdapter(adapter);

        binding = ActivityFollowerBinding.inflate(getLayoutInflater());

        //shimmer 적용을 위해 기존 뷰는 일단 안보이게, shimmer는 보이게
        binding.llFollwer.setVisibility(View.GONE);
        binding.SFLFollwer.startShimmer();
        binding.SFLFollwer.setVisibility(View.VISIBLE);


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(selected).select();

        //shimmer 적용 끝내고 shimmer는 안보이게, 기존 뷰는 보이게
        binding.llFollwer.setVisibility(View.VISIBLE);
        binding.SFLFollwer.stopShimmer();
        binding.SFLFollwer.setVisibility(View.GONE);


    }
}