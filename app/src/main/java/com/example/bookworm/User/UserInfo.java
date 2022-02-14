package com.example.bookworm.User;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;

public class UserInfo {

    public Profile profile;
    public String profileimg; // 회원가입시 프로필사진
    public String username; // 회원가입시 닉네임
    public String email; // 로그인한 이메일
    public String platform;
    public UserInfo() {
    }

    public void add(UserAccount kakaoAccount) {
        this.profile = kakaoAccount.getProfile();
        this.profileimg = profile.getProfileImageUrl();
        this.username = profile.getNickname();
        this.email = kakaoAccount.getEmail();
        this.platform="Kakao";
    }
    public void add(GoogleSignInAccount account) {
        this.username= account.getDisplayName();
        this.email=account.getEmail();
        this.platform="Google";
    }

}
