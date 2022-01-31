package com.example.bookworm.User;

import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;

public class UserInfo {

    public Profile profile;
    public String profileimg; // 회원가입시 프로필사진
    public String username; // 회원가입시 닉네임
    public String email; // 로그인한 이메일


    public UserInfo(UserAccount kakaoAccount)
    {
        this.profile = kakaoAccount.getProfile();
        this.profileimg = profile.getProfileImageUrl();
        this.username = profile.getNickname();
        this.email = kakaoAccount.getEmail();
    }

}
