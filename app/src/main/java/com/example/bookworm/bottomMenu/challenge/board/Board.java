package com.example.bookworm.bottomMenu.challenge.board;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;


public class Board implements Parcelable {
    private String boardID; //인증글 ID
    private String imgurl; //업로드 이미지 url
    private String boardText; //인증글 내용
    private String date; //현재 날짜
    private String userToken; //작성자 토큰
    private String challengeName; //챌린지 명
    private String masterToken; //챌린지 생성자 토큰
    private Book book; //챌린지에 사용된 책
    private long likeCount; //좋아요 수
    private long commentsCount; //사용자 댓글 수
    private boolean allowed; //인증글 승인 여부


    public Board(Map data) {
        book = new Book();
        if (data != null) {
            this.boardID = (String) data.get("boardID");
            if (data.get("imgurl") != null) this.imgurl = (String) data.get("imgurl");
            this.boardText = (String) data.get("boardText");
            this.date = (String) data.get("date");
            this.userToken = (String) data.get("UserToken");
            this.challengeName = (String) data.get("challengeName");
            this.masterToken = (String) data.get("masterToken");
//            this.book.setBook((Map) data.get("book"));
//            this.book = convertToBook(data);
            convertToBook((Map) data.get("book"));
            this.likeCount = (long) data.get("likeCount");
            this.commentsCount = (long) data.get("commentsCount");
            this.allowed = (boolean) data.get("allowed");
        }
    }

    protected Board(Parcel in) {
        boardID = in.readString();
        imgurl = in.readString();
        boardText = in.readString();
        date = in.readString();
        userToken = in.readString();
        challengeName = in.readString();
        masterToken = in.readString();
        book = in.readParcelable(Book.class.getClassLoader());
        likeCount = in.readLong();
        commentsCount = in.readLong();
        allowed = in.readByte() != 0;
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

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

    public long getCommentsCount() {
        return commentsCount;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public String getMasterToken() {
        return masterToken;
    }

    public boolean isAllowed() {
        return allowed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(boardID);
        parcel.writeString(imgurl);
        parcel.writeString(boardText);
        parcel.writeString(date);
        parcel.writeString(userToken);
        parcel.writeString(challengeName);
        parcel.writeString(masterToken);
        parcel.writeParcelable(book, i);
        parcel.writeLong(likeCount);
        parcel.writeLong(commentsCount);
        parcel.writeByte((byte) (allowed ? 1 : 0));
    }


    private void convertToBook(Map data) {
//        Book book = new Book();
        book.setImgUrl((String) data.get("imgUrl"));
        book.setTitle((String) data.get("title"));
        book.setCategoryName((String) data.get("categoryName"));
        book.setContent((String) data.get("content"));
        book.setPublisher((String) data.get("publisher"));
        book.setAuthor((String) data.get("author"));
        book.setItemId((String) data.get("itemId"));
        book.setIsbn((String) data.get("isbn"));

//        return book;
    }
}
