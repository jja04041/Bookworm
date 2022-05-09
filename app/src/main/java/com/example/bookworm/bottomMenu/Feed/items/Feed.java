package com.example.bookworm.bottomMenu.Feed.items;

import com.example.bookworm.bottomMenu.Feed.comments.Comment;
import com.example.bookworm.bottomMenu.search.items.Book;
import com.example.bookworm.core.userdata.UserInfo;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Feed implements Serializable {
    private Book book = null; // 선택한 책
    //라벨 Array 추가
    private String feedID = null; //피드 ID
    private String imgurl = null; //업로드 이미지 url
    private long commentCount; //사용자 댓글 수
    private String feedText = null; //피드의 내용
    private String date = null; //현재 날짜
    private long likeCount;//좋아요 수
    private UserInfo Creator = null; //작성자 정보
    private String userToken=null;
    private ArrayList<String> label = null; //라벨 목록
    @Exclude
    private Comment comment=null; //최상단의 댓글을 가져옴 -> 서버에 업로드 시엔 이 필드를 제외함. (Exclude Annotation 이용)
    @Exclude
    private int position; //피드 수정,삭제시 리사이클러뷰의 포지션을 가져올때 사용


    //comment: 작성자 데이터, 작성한 댓글 내용  {"usertoken":20200, "data": "안녕하세요"} => Comment 객체  => Arraylist<Comment> =[ Comment들 ];
    // 한 피드의 댓글 목록  [{"usertoken":20200, "data": "안녕하세요", add:[{ }]},{"usertoken":20100, "data": "안녕하세요"}]
    public Feed() {
        this.book = new Book(null);
        this.Creator = new UserInfo();

    }

    public void setData(Map Adata, Map Bdata) {
        setFeedData(Adata);
        setFeedTopComment(Bdata);
    }

    private void setFeedData(Map data) {
        this.feedID = (String) data.get("FeedID");
        if(data.get("book") instanceof Map){
            this.book.setBook((Map) data.get("book"));
        }else{
            this.book = (Book) data.get("book");//피드 수정일경우 맵객체가 아닌 북객체가 들어오기 때문
        }
        this.userToken=(String)data.get("UserToken");
        this.label = (ArrayList<String>) data.get("label");
        this.commentCount = (long) data.get("commentsCount");
        this.likeCount = (long) data.get("likeCount");
        if (data.get("imgurl") != null) this.imgurl = (String) data.get("imgurl");
        this.feedText = (String) data.get("feedText");
        this.date = (String) data.get("date");
    }

    private void setFeedTopComment(Map data) {
        if (data!=null) {
            this.comment = new Comment();
            comment.setData(data);
        }
    }

    public Comment getComment() {
        return comment;
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

    public String getFeedText() {
        return feedText;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public String getDate() {
        return date;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public String getUserToken() {
        return userToken;
    }

    public ArrayList<String> getLabel() {
        return label;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
