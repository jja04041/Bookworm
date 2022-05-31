package com.example.bookworm.core.userdata;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.Exclude;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

// import io.reactivex.Observer;

public class UserInfo implements Serializable {

    private String profileimg = null; // 회원가입시 프로필사진
    private String username = "(알 수 없음)"; // 회원가입시 닉네임
    private String email = null; // 로그인한 이메일
    private String platform = null;
    private String introduce = "안녕하세요~";

    private boolean isMainUser = false;

    private String FCMtoken = null;


    private String token = null;


    private ArrayList<String> likedPost;


    private int followerCounts = 0;
    private int followingCounts = 0;

    private HashMap<String, Integer> genre = null;

    private boolean followed = false;


    public UserInfo() {
        genre = new HashMap<>();
        likedPost = new ArrayList<>();

    }

    @Exclude
    public boolean isMainUser() {
        return isMainUser;
    }

    public void setMainUser(boolean mainUser) {
        isMainUser = mainUser;
    }


    public void setFollowingCounts(int followingCounts) {
        this.followingCounts = followingCounts;
    }

    @Exclude
    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
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

        if ((ArrayList<String>) document.get("likedPost") != null)
            this.likedPost = (ArrayList<String>) document.get("likedPost");
        else this.likedPost = new ArrayList<>();

        if ((String) document.get("introduce") != null)
            this.introduce = (String) document.get("introduce");

        this.FCMtoken = (String) document.get("fcmtoken");
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

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setProfileimg(String profileimg) {
        this.profileimg = profileimg;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFollowerCounts() {
        return followerCounts;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getFollowingCounts() {
        return followingCounts;
    }

    public HashMap<String, Integer> getGenre() {
        return genre;
    }

    public String getToken() {
        return token;
    }

    public void setGenre(String categoryname, Context context) {

        StringTokenizer tokenizer = new StringTokenizer(categoryname, ">");

        tokenizer.nextToken(); // 두번째 분류를 원하기 때문에 맨 앞 분류 꺼냄

        String category = tokenizer.nextToken();

        if (category.equals("자기계발") || category.equals("에세이") || category.equals("예술/대중문화")) {
            category = "자기계발";
        } else if (category.equals("소설/시/희곡") || category.equals("장르소설") || category.equals("전집/중고전집")) {
            category = "소설";
        } else if (category.equals("좋은부모")) {
            category = "육아";
        } else if (category.equals("사회과학") || category.equals("경제경영")) {
            category = "사회";
        } else if (category.equals("어린이") || category.equals("유아")) {
            category = "어린이";
        } else if (category.equals("종교/역학") || category.equals("인문학") || category.equals("역사") || category.equals("고전")) {
            category = "인문";
        } else if (category.equals("가정/요리/뷰티") || category.equals("건강/취미/레저") || category.equals("여행") || category.equals("잡지") || category.equals("달력/기타")) {
            category = "생활";
        } else if (category.equals("외국어") || category.equals("대학교재") || category.equals("초중고참고서") || category.equals("수험서/자격증")
                || category.equals("공무원 수험서") || category.equals("컴퓨터/모바일")) {
            category = "공부";
        }

        int unboxint = 0;
        if (null == this.genre.get(category)) {
            this.genre.put(category, 1);
        } else {
            unboxint = (this.genre.get(category));
            unboxint++;
            this.genre.replace(category, unboxint);
        }
    }

    public String getFCMtoken() {
        return FCMtoken;
    }

    public void setFCMtoken(String FCMtoken) {
        this.FCMtoken = FCMtoken;
    }

    public ArrayList<String> getLikedPost() {
        return likedPost;
    }

    public void setLikedPost(ArrayList<String> likedPost) {
        this.likedPost = likedPost;
    }

}


//
//    public BookWorm getBookworm() {
//        return bookworm;
//    }
//
//    public void setBookworm(BookWorm bookworm) {
//        this.bookworm = bookworm;
//    }
