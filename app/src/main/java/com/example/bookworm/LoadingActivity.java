package com.example.bookworm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.bookworm.Core.Login.activity_login;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.auth.Session;

public class LoadingActivity extends activity_login {

    private GoogleSignInAccount gsa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Handler mHander = new Handler();
        mHander.postDelayed(new Runnable() {
            @Override
            public void run() {

                gsa = GoogleSignIn.getLastSignedInAccount(LoadingActivity.this);

                // 구글 자동 로그인
                if (gsa == null && !Session.getCurrentSession().checkAndImplicitOpen()) {
                    Intent intent = new Intent(LoadingActivity.this, activity_login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }


            }
        }, 2000);

    }
}


