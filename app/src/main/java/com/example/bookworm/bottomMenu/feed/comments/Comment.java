package com.example.bookworm.bottomMenu.feed.comments;

import com.example.bookworm.core.userdata.UserInfo;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;

public class Comment implements Serializable {
    //댓글 ID
    private String commentID;
    //유저 정보
    private String userToken;
    //댓글 내용
    private String contents;
    //생성된 시각
    private String madeDate;

    @Exclude
    private UserInfo creator;

    public UserInfo getCreator() {
        return creator;
    }

    public void setCreator(UserInfo creator) {
        this.creator = creator;
    }

    @Exclude
    private int position; //댓글 삭제시 리사이클러뷰의 포지션을 가져올때 사용

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Exclude
    String duration;
    public Comment() {

    }

    public void getData(String userToken, String contents, Long madeDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.commentID = madeDate + "_" + userToken;
        this.userToken = userToken;
        this.contents = contents;
        this.madeDate = dateFormat.format(madeDate);
    }
    public void getData(String userToken, String contents, String madeDate) {
        this.commentID = madeDate + "_" + userToken;
        this.userToken = userToken;
        this.contents = contents;
        this.madeDate = madeDate;
    }
    public String getCommentID() {
        return commentID;
    }

    public void setData(Map data) {
        this.commentID = (String) data.get("commentID");
        this.contents = (String) data.get("contents");
        this.madeDate = (String) data.get("madeDate");
        this.userToken = (String) data.get("userToken");
    }

    public String getUserToken() {
        return userToken;
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
