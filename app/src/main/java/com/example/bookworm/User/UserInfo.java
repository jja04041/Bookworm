package com.example.bookworm.User;

import android.content.Context;
import android.util.Log;

import com.example.bookworm.Bw.enum_wormtype;
import com.example.bookworm.R;
import com.example.bookworm.modules.personalD.PersonalD;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static enum_wormtype wormtype = enum_wormtype.디폴트;

    private static int enum_size = enum_wormtype.enumsize.value();


    // 업적 달성시 이 벡터에 책볼레 drawble id값을 추가합니다.
    private Vector<Integer> wormvec = new Vector<>();
    private HashMap<String, Boolean> achievementmap = new HashMap<>();
    private Vector<Integer> genre = new Vector<>();




    public UserInfo() {

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

    public void Initbookworm() {
        genre.setSize(enum_size);


        for (int i = 0; i < enum_size; ++i) {
            genre.set(i, 0);
        }
        wormtype = enum_wormtype.디폴트;

        this.wormtype = enum_wormtype.디폴트;
        this.wormvec.add(R.drawable.bw_default);
        InitAchievemap();

    }

    public void InitAchievemap()
    {
        this.achievementmap.put("디폴트", true);
        this.achievementmap.put("공포왕", false);
        this.achievementmap.put("추리왕", false);
        this.achievementmap.put("로맨스왕", false);
        this.achievementmap.put("피드왕", false);
    }

    //파이어베이스에서 값을 가져옴
    public void add(Map document) {
        this.username = (String) document.get("username");
        this.email = (String) document.get("email");
        this.profileimg = (String) document.get("profileimg");
        this.token = (String) document.get("token");
        this.platform = (String) document.get("platform");

        this.wormvec = new Vector<>((ArrayList<Integer>)document.get("wormvec"));
        this.genre = new Vector<>((ArrayList<Integer>) document.get("genre"));
        this.achievementmap = new HashMap<>((HashMap<String, Boolean>)document.get("achievementmap"));

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

    public enum_wormtype getWormtype() {
        return wormtype;
    }

    public void setWormtype(enum_wormtype wormtype) {
        this.wormtype = wormtype;
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

    public Vector<Integer> getWormvec() {
        return wormvec;
    }

//    public Vector<String> getWormimgvec() {
//        return wormimgvec;
//    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }


    public HashMap<String, Boolean> getAchievementmap() {
        return achievementmap;
    }

    public void setAchievementmap(HashMap<String, Boolean> achievementmap) {
        this.achievementmap = achievementmap;
    }





}