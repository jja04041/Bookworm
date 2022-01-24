package com.example.bookworm.User;

public class UserInfo {

    public String profileimg; // 회원가입시 프로필사진
    public String username; // 회원가입시 닉네임
    public String email; // 로그인한 이메일

    public UserInfo(String profileimg, String username, String email)
    {
        this.profileimg = profileimg;
        this.username = username;
        this.email = email;
    }
}
