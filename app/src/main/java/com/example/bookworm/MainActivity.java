package com.example.bookworm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Fragment fragment_feed;
    Fragment fragment_search;
    Fragment fragment_bookworm;
    Fragment fragment_challenge;
    Fragment fragment_profile;

    Fragment [] fragments= {fragment_feed, fragment_search, fragment_bookworm, fragment_challenge, fragment_profile};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i=0; i<5; ++i)
            fragments[i] = new Fragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_feed).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId())
                        {
                            case R.id.tab_feed:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_feed).commit();
                                return true;
                            case R.id.tab_search:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_search).commit();
                                return true;
                            case R.id.tab_bookworm:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_bookworm).commit();
                                return true;
                            case R.id.tab_challenge:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_challenge).commit();
                                return true;
                            case R.id.tab_profile:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_profile).commit();
                                return true;
                        }



                        return false;
                    }

        });

    }
}