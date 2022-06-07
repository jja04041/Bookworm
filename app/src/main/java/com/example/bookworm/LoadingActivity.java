package com.example.bookworm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookworm.appLaunch.views.MainActivity;
import com.example.bookworm.core.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.auth.Session;

public class LoadingActivity extends AppCompatActivity {

    private GoogleSignInAccount gsa;

    @Override
    protected void onStart() {
        super.onStart();
        gsa = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        Handler mHander = new Handler();
        mHander.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager cm = (ConnectivityManager) LoadingActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {

                    if (gsa == null && !Session.getCurrentSession().checkAndImplicitOpen()) {
                        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {

                        //자동 로그인 (구글 , 카카오)
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    new AlertDialog.Builder(LoadingActivity.this)
                            .setMessage("인터넷 접속 후 다시 시도해 주세요")
                            .setPositiveButton("네트워크 설정", (dialog, which) -> {
                                dialog.dismiss();
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                finish();
                            }).setNegativeButton("닫기", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    }).show();
                }
            }
        }, 2000);


    }
}


