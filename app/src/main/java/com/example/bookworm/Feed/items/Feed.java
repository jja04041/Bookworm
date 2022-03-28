package com.example.bookworm.Feed.items;

import com.example.bookworm.Search.items.Book;
import com.example.bookworm.User.UserInfo;

import java.util.ArrayList;
import java.util.Map;

public class Feed {
    private Book book=null; // 선택한 책
    //라벨 Array 추가
    private String feedID=null; //피드 ID
    private String imgurl=null; //업로드 이미지 url
    private int userRating; //책볼레 사용자 평점
    private String feedText=null; //피드의 내용
    private String date=null; //현재 날짜
    private long likeCount;//좋아요 수
    private UserInfo Creator=null; //작성자 정보
    private ArrayList<String> label=null; //라벨 목록


    //comment: 작성자 데이터, 작성한 댓글 내용  {"usertoken":20200, "data": "안녕하세요"} => Comment 객체  => Arraylist<Comment> =[ Comment들 ];
    // 한 피드의 댓글 목록  [{"usertoken":20200, "data": "안녕하세요", add:[{ }]},{"usertoken":20100, "data": "안녕하세요"}]
    public Feed() {
        this.book=new Book(null);
        this.Creator=new UserInfo();
    }
    public void setData(Map data){
        this.feedID=(String)data.get("FeedID");
        this.book.setBook((Map)data.get("book"));
        this.Creator.add((Map)data.get("UserInfo"));
        this.label=(ArrayList<String>) data.get("label");
        this.likeCount=(long) data.get("likeCount");
        if(data.get("imgurl")!=null) this.imgurl=(String) data.get("imgurl");
        this.feedText=(String) data.get("feedText");
        this.date=(String) data.get("date");
    }

    public Book getBook() {
        return book;
    }

    public String getFeedID() {
        return feedID;
    }

    public String getImgurl() {
        return imgurl;
    }

    public int getUserRating() {
        return userRating;
    }

    public String getFeedText() {
        return feedText;
    }

    public String getDate() {
        return date;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public UserInfo getCreator() {
        return Creator;
    }

    public ArrayList<String> getLabel() {
        return label;
    }
}
