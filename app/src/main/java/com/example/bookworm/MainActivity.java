package com.example.bookworm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {

    private FragmentPagerAdapter fragmentPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);
        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        viewPager.setAdapter(fragmentPagerAdapter);
        TabLayout.setupWithViewPager(viewPager);
    }
}