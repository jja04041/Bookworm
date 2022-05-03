package com.example.bookworm.Core;


import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.R;
import com.example.bookworm.BottomMenu.Feed.fragment_feed;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static Dialog achievedialog;

    Fragment fragment_feed, fragment_search, fragment_bookworm, fragment_challenge, fragment_profile;
    Fragment[] fragments = {fragment_feed, fragment_search, fragment_bookworm, fragment_challenge, fragment_profile};
    // 위험 권한을 부여할 권한 지정

    FragmentManager fragmentManager;

    MoveFragment MoveFragment = new MoveFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // 초기화면 설정
        fragments[0] = new fragment_feed();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragments[0],"0").commitAllowingStateLoss();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tab_feed:
                                MoveFragment.show_fragment(fragments, fragmentManager, 0);
                                return true;


                            case R.id.tab_search:
                                MoveFragment.show_fragment(fragments, fragmentManager, 1);
                                return true;


                            case R.id.tab_bookworm:
                                MoveFragment.show_fragment(fragments, fragmentManager, 2);
                                return true;


                            case R.id.tab_challenge:
                                MoveFragment.show_fragment(fragments, fragmentManager, 3);
                                return true;


                            case R.id.tab_profile:
                                MoveFragment.show_fragment(fragments, fragmentManager, 4);
                                return true;
                        }


                        return false;
                    }

                });

    }

}