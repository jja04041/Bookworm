package com.example.bookworm.Feed.Comments;

import com.example.bookworm.User.UserInfo;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;

public class Comment implements Serializable {
    //댓글 ID
    private String CommentID;
    //유저 정보
    private String userToken;
    private String userName;
    private String userThumb;
    //댓글 내용
    private String contents;
    //생성된 시각
    private String madeDate;

    @Exclude
    private int position; //댓글 삭제시 리사이클러뷰의 포지션을 가져올때 사용

    public Comment() {

    }

    public void getData(UserInfo userInfo, String contents, Long madeDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.CommentID = madeDate + "_" + userInfo.getToken();
        this.userToken = userInfo.getToken();
        this.userName = userInfo.getUsername();
        this.userThumb = userInfo.getProfileimg();
        this.contents = contents;
        this.madeDate = dateFormat.format(madeDate);
    }

    public String getCommentID() {
        return CommentID;
    }

    public void setData(Map data) {
        this.CommentID = (String) data.get("commentID");
        this.contents = (String) data.get("contents");
        this.madeDate = (String) data.get("madeDate");
        this.userName = (String) data.get("userName");
        this.userThumb = (String) data.get("userThumb");
        this.userToken = (String) data.get("userToken");

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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
