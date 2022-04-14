package com.example.bookworm.User;

import android.content.Context;
import android.util.Log;

import com.example.bookworm.Bw.enum_wormtype;
import com.example.bookworm.modules.personalD.PersonalD;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

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

    private final int enum_size = enum_wormtype.enumsize.value();

    private Vector<Integer> genre = new Vector<>();
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
        genre.setSize(enum_size);

        for (int i = 0; i < enum_size; ++i) {
            genre.set(i, 0);
        }
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

        this.genre = new Vector<>((ArrayList<Integer>) document.get("genre"));

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

    public Vector<Integer> getGenre() {
        return genre;
    }

    public void setGenre(int idx, Context context) {
        int unboxint = (this.genre.get(idx));
        unboxint++;

        this.genre.set(idx, unboxint);

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