package com.example.bookworm.core.login;

import android.app.Application;

import com.kakao.auth.KakaoSDK;


//애플리케이션 전역을 다 접근 가능한 객체
//업적 처리 같은 전역처리를 이 곳에서 담당하면 될 듯 하다.

public class GlobalApplication extends Application {
    private static GlobalApplication instance;


    public static Application getInstance() {
        if (instance == null) {
            throw new IllegalStateException("this app illegal state");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Kakao Sdk 초기화
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

}
