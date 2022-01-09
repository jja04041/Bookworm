package com.example.bookworm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Fragment[] fragments = new Fragment[5];
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments[0] =new fragment_feed();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragments[0]).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tab_feed: //0
                                for (int i = 0; i < 5; i++) {
                                    if (i == 0) {
                                        if (fragments[i] ==null) {
                                            fragments[i] =new fragment_feed();
                                            fragmentManager.beginTransaction().add(R.id.container, fragments[i]).commitAllowingStateLoss();
                                        }else fragmentManager.beginTransaction().show(fragments[i]).commitAllowingStateLoss();
                                    } else {
                                        if(fragments[i]!=null) fragmentManager.beginTransaction().hide(fragments[i]).commitAllowingStateLoss();
                                    }
                                }
                                return true;
                            case R.id.tab_search:
                                for (int i = 0; i < 5; i++) {
                                    if (i == 1) {
                                        if (fragments[i] ==null) {
                                            fragments[i] =new fragment_search();
                                            fragmentManager.beginTransaction().add(R.id.container, fragments[i]).commitAllowingStateLoss();
                                        }else fragmentManager.beginTransaction().show(fragments[i]).commitAllowingStateLoss();
                                    } else {
                                        if(fragments[i]!=null) fragmentManager.beginTransaction().hide(fragments[i]).commitAllowingStateLoss();
                                    }
                                }
                                return true;
                            case R.id.tab_bookworm:
                                for (int i = 0; i < 5; i++) {
                                    if (i == 2) {
                                        if (fragments[i] ==null) {
                                            fragments[i] =new fragment_bookworm();
                                            fragmentManager.beginTransaction().add(R.id.container,fragments[i]).commitAllowingStateLoss();
                                        }else fragmentManager.beginTransaction().show(fragments[i]).commitAllowingStateLoss();
                                    }else {
                                        if(fragments[i]!=null) fragmentManager.beginTransaction().hide(fragments[i]).commitAllowingStateLoss();
                                    }
                                }
                                return true;
                            case R.id.tab_challenge:
                                for (int i = 0; i < 5; i++) {
                                    if (i == 3) {
                                        if (fragments[i] ==null) {
                                            fragments[i] =new fragment_challenge();
                                            fragmentManager.beginTransaction().add(R.id.container, fragments[i]).commitAllowingStateLoss();
                                        }else fragmentManager.beginTransaction().show(fragments[i]).commitAllowingStateLoss();
                                    } else {
                                        if(fragments[i]!=null) fragmentManager.beginTransaction().hide(fragments[i]).commitAllowingStateLoss();
                                    }
                                }
                                return true;
                            case R.id.tab_profile:
                                for (int i = 0; i < 5; i++) {
                                    if (i == 4) {
                                        if (fragments[i] ==null) {
                                            fragments[i] =new fragment_profile();
                                            fragmentManager.beginTransaction().add(R.id.container, fragments[i]).commitAllowingStateLoss();
                                        }else fragmentManager.beginTransaction().show(fragments[i]).commitAllowingStateLoss();
                                    } else {
                                        if(fragments[i]!=null) fragmentManager.beginTransaction().hide(fragments[i]).commitAllowingStateLoss();
                                    }
                                }
                                return true;
                        }


                        return false;
                    }

                });

    }
}