package com.example.bookworm.Core;


import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.R;
import com.example.bookworm.fragments.fragment_feed;
import com.example.bookworm.fragments.functions.frag_functions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    public static Dialog achievedialog;

    Fragment fragment_feed, fragment_search, fragment_bookworm, fragment_challenge, fragment_profile;
    Fragment[] fragments = {fragment_feed, fragment_search, fragment_bookworm, fragment_challenge, fragment_profile};
    // 위험 권한을 부여할 권한 지정

    FragmentManager fragmentManager;

    frag_functions frag_functions = new frag_functions();

    String FCMToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        FCMToken = token;
                        // Log and toast
                    }

                });

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
                                frag_functions.show_fragment(fragments, fragmentManager, 0);
                                return true;


                            case R.id.tab_search:
                                frag_functions.show_fragment(fragments, fragmentManager, 1);
                                return true;


                            case R.id.tab_bookworm:
                                frag_functions.show_fragment(fragments, fragmentManager, 2);
                                return true;


                            case R.id.tab_challenge:
                                frag_functions.show_fragment(fragments, fragmentManager, 3);
                                return true;


                            case R.id.tab_profile:
                                frag_functions.show_fragment(fragments, fragmentManager, 4);
                                return true;
                        }


                        return false;
                    }

                });

    }

}