package com.example.bookworm.User;

import android.content.Context;
import android.util.Log;

import com.example.bookworm.modules.personalD.PersonalD;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// import io.reactivex.Observer;

public class UserInfo implements Serializable {

    private String              profileimg; // 회원가입시 프로필사진
    private String              username; // 회원가입시 닉네임
    private String              email; // 로그인한 이메일
    private String              platform;
    private String              token;
    private ArrayList<String>   likedPost;


    private int                 followerCounts;
    private int                 followingCounts;



    private HashMap<String, Integer> genre = new HashMap<>();
//    private BookWorm bookworm;






    public UserInfo() {
//        bookworm = new BookWorm();
//        bookworm.Initbookworm();
    }

    public void add(UserAccount kakaoAccount) {
        Profile profile = kakaoAccount.getProfile();
        this.profileimg = profile.getProfileImageUrl();
        this.username = profile.getNickname();
        this.email = kakaoAccount.getEmail();
        this.platform = "Kakao";

    }

    public void add(GoogleSignInAccount account) {
        try {
            Log.d("profile", account.getPhotoUrl().toString());
            this.profileimg = account.getPhotoUrl().toString();
        } catch (NullPointerException e) {
            Log.d("profile", "Null");
        }
        this.username = account.getDisplayName();
        this.email = account.getEmail();
        this.platform = "Google";
    }

    public void InitGenre() {
        genre.put("공포", 0);
        genre.put("추리", 0);

    }

    //파이어베이스에서 값을 가져옴
    public void add(Map document) {
        this.username = (String) document.get("username");
        this.email = (String) document.get("email");
        this.profileimg = (String) document.get("profileimg");
        this.token = (String) document.get("token");
        this.platform = (String) document.get("platform");

        this.followerCounts = Integer.parseInt(String.valueOf(document.get("followerCounts")));
        this.followingCounts = Integer.parseInt(String.valueOf(document.get("followingCounts")));

        this.genre = new HashMap<String, Integer>((HashMap<String, Integer>) document.get("genre"));

        if ((ArrayList<String>) document.get("likedPost")!=null) this.likedPost=(ArrayList<String>) document.get("likedPost");
        else this.likedPost=new ArrayList<>();
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


    public int getFollowerCounts() {
        return followerCounts;
    }

    public int getFollowingCounts() {
        return followingCounts;
    }

    public HashMap<String, Integer> getGenre() {
        return genre;
    }



    public void setGenre(String categoryname, Context context) {

        String abd = categoryname;

        // a>b1/b2>c>d
        int unboxint = (this.genre.get("추리"));
        unboxint++;

        this.genre.replace("추리", unboxint);

        new PersonalD(context).saveUserInfo(this);
    }

    public void setLikedPost(ArrayList<String> likedPost) {
        this.likedPost = likedPost;
    }

    public ArrayList<String> getLikedPost() {
        return likedPost;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }


//
//    public BookWorm getBookworm() {
//        return bookworm;
//    }
//
//    public void setBookworm(BookWorm bookworm) {
//        this.bookworm = bookworm;
//    }

}