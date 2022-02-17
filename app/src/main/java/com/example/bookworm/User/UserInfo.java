package com.example.bookworm.User;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private String profileimg; // 회원가입시 프로필사진
    private String username; // 회원가입시 닉네임
    private String email; // 로그인한 이메일
    private String platform;
    private String token;
    public UserInfo() {
    }

    public void add(UserAccount kakaoAccount) {
        Profile profile= kakaoAccount.getProfile();
        this.profileimg = profile.getProfileImageUrl();
        this.username = profile.getNickname();
        this.email = kakaoAccount.getEmail();
        this.platform="Kakao";
    }
    public void add(GoogleSignInAccount account) {
        try {
            Log.d("profile", account.getPhotoUrl().toString());
            this.profileimg=account.getPhotoUrl().toString();
        }catch (NullPointerException e){
            Log.d("profile", "Null");
        }
        this.username= account.getDisplayName();
        this.email=account.getEmail();
        this.platform="Google";
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken(){
        return this.token;
    }

    public String getProfileimg() {
        return profileimg;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPlatform() {
        return platform;
    }
}
