package com.example.bookworm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookworm.appLaunch.views.MainActivity;
import com.example.bookworm.core.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.common.model.KakaoSdkError;
import com.kakao.sdk.user.UserApiClient;

import java.util.Objects;


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
        mHander.postDelayed(() -> {
            ConnectivityManager cm = (ConnectivityManager) LoadingActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                NetworkCapabilities actNetwork = cm.getNetworkCapabilities(cm.getActiveNetwork());
                Boolean bool = actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                Boolean bool2 = actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                if (bool || bool2) runMainThread();
                else runCloseApp().show();
            }catch (Exception e){
                runCloseApp().show();
            }

        }, 200);


    }

    private void runMainThread() {
        //자동 로그인 작업
        if (AuthApiClient.getInstance().hasToken()) {
            //카카오 자동 로그인 -> 유효한 토큰이 있다면 자동 로그인 수행
            UserApiClient.getInstance().accessTokenInfo((token, error) -> {
                if (error != null) {
                    if (error instanceof KakaoSdkError && ((KakaoSdkError) error).isInvalidTokenError()) {
                        //로그인 필요
                        moveToLogin();
                    } else {
                        //기타 에러
                        Log.e("카카오 자동로그인 중 에러 발생", "기타오류");
                    }
                } else {
//                    Log.d("카카오토큰", Objects.requireNonNull(AuthApiClient.getInstance().getTokenManagerProvider().getManager().getToken()).getAccessToken());
                    //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                    moveToMain();
                }
                return null;
            });
        } else {
            if (gsa != null) //만약 구글로 로그인한 기록이 남아 있다면
                moveToMain();
            else moveToLogin();
        }
    }

    private AlertDialog.Builder runCloseApp() {
        return new AlertDialog.Builder(LoadingActivity.this)
                .setMessage("인터넷 접속 후 다시 시도해 주세요")
                .setPositiveButton("네트워크 설정", (dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    finish();
                }).setNegativeButton("닫기", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                });
    }

    private void moveToLogin() {
        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void moveToMain() {
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}


