package com.example.bookworm.User;

import android.util.Log;

import com.example.bookworm.Bw.enum_wormtype;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private String profileimg; // 회원가입시 프로필사진
    private String username; // 회원가입시 닉네임
    private String email; // 로그인한 이메일
    private String platform;
    private String token;

    private enum_wormtype wormtype = enum_wormtype.디폴트;
    int enum_size = enum_wormtype.values().length;

    // genre index의 1번 = enum_wormtype의 공포 2번은 추리 .... 이하 동문
    // 가장 최댓값을 가진 index를 벌레의 종류로 설정할 계획
    // 가장 최댓값의 index의 번호에 따라 최고 선호 장르를 설정합니다.
    private int [] genre = new int [enum_size];

    public UserInfo() {
    }

    public void add(UserAccount kakaoAccount) {
        Profile profile= kakaoAccount.getProfile();
        this.profileimg = profile.getProfileImageUrl();
        this.username = profile.getNickname();
        this.email = kakaoAccount.getEmail();
        this.platform="Kakao";
        this.wormtype = enum_wormtype.디폴트;


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
        this.wormtype = enum_wormtype.디폴트;
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

    public enum_wormtype getWormtype() {
        return wormtype;
    }

    public int[] getGenre() {
        return genre;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken(){
        return this.token;
    }
}