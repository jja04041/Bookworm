package com.example.bookworm;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.fragments.fragment_feed;
import com.example.bookworm.fragments.functions.frag_functions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Fragment fragment_feed, fragment_search, fragment_bookworm, fragment_challenge, fragment_profile;
    Fragment[] fragments = {fragment_feed, fragment_search, fragment_bookworm, fragment_challenge, fragment_profile};


    FragmentManager fragmentManager;

    frag_functions frag_functions = new frag_functions();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기화면 설정
        fragments[0] =new fragment_feed();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragments[0]).commitAllowingStateLoss();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.tab_feed:
                                frag_functions.show_fragment(fragments,fragmentManager, 0);
                                return true;


                            case R.id.tab_search:
                                frag_functions.show_fragment(fragments,fragmentManager, 1);
                                return true;


                            case R.id.tab_bookworm:
                                frag_functions.show_fragment(fragments,fragmentManager, 2);
                                return true;


                            case R.id.tab_challenge:
                                frag_functions.show_fragment(fragments,fragmentManager, 3);
                                return true;


                            case R.id.tab_profile:
                                frag_functions.show_fragment(fragments,fragmentManager, 4);
                                return true;
                        }



                        return false;
                    }

        });

    }
}