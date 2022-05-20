package com.example.bookworm.bottomMenu.challenge.board;



import com.example.bookworm.bottomMenu.search.items.Book;

import java.io.Serializable;
import java.util.Map;

public class Board implements Serializable {
    private String boardID; //인증글 ID
    private String imgurl; //업로드 이미지 url
    private String boardText; //인증글 내용
    private String date; //현재 날짜
    private String userToken; //작성자 토큰
    private String challengeName; //챌린지 명
    private Book book; //챌린지에 사용된 책
    private long likeCount; //좋아요 수
    private long commentCount; //사용자 댓글 수


    public Board(Map data) {
        book = new Book(null);
        if (data!=null) {
            this.boardID = (String) data.get("boardID");
            if (data.get("imgurl") != null) this.imgurl = (String) data.get("imgurl");
            this.boardText = (String) data.get("boardText");
            this.date = (String) data.get("date");
            this.userToken = (String) data.get("UserToken");
            this.challengeName = (String) data.get("challengeName");
            this.book.setBook((Map) data.get("book"));
            this.likeCount = (long) data.get("likeCount");
            this.commentCount = (long) data.get("commentsCount");
        }
    }

    public String getBoardID() {
        return boardID;
    }

    public String getImgurl() {
        return imgurl;
    }

    public String getBoardText() {
        return boardText;
    }

    public String getDate() {
        return date;
    }

    public String getUserToken() {
        return userToken;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public Book getBook() {
        return book;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public String getChallengeName() {
        return challengeName;
    }
}
