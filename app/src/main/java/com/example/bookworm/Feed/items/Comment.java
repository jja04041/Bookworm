package com.example.bookworm.Feed.items;

import com.example.bookworm.User.UserInfo;

import java.util.Map;

public class Comment {
    //유저 정보
    private String userToken;
    private String userName;
    private String userThumb;
    //댓글 내용
    private String contents;
    //생성된 시각
    private String madeDate;
    public Comment(UserInfo userInfo, String contents, String madeDate){
        this.userToken = userInfo.getToken();
        this.userName = userInfo.getUsername();
        this.userThumb = userInfo.getProfileimg();
        this.contents = contents;
        this.madeDate = madeDate;
    }
    public void setData(Map data){

    }

    public String getUserToken() {
        return userToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserThumb() {
        return userThumb;
    }

    public String getContents() {
        return contents;
    }

    public String getMadeDate() {
        return madeDate;
    }

}
