package com.example.bookworm.Challenge.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Challenge implements Serializable {
    private String bookId; //챌린지에서 사용하는 도서의 Id값
    private String bookThumb; //책 썸네일
    private String bookTitle; //책 이름
    private String StartDate; //챌린지 시작일
    private String EndDate; // 챌린지 마감일
    private String Title; //챌린지 명
    private ArrayList<String> CurrentPart; //현재 참가자 목록
    private Long MaxPart; // 최대 참가 가능한 인원 수
    private String master; //방장명
    private String masterThumb;//방장의 프로필 썸네일
    private String masterToken; //방장의 토큰값

    public Challenge(Map data) {
        if (data!=null) {
            CurrentPart = (ArrayList<String>) data.get("CurrentParticipation");
            MaxPart = (Long) data.get("MaxParticipation");
            bookId = (String) data.get("BookId");
            StartDate = (String) data.get("challengeStartDate");
            EndDate = (String) data.get("ChallengeEndDate");
            Title = (String) data.get("strChallengeName");
            bookTitle = (String) data.get("bookname");
            bookThumb = (String) data.get("thumbnailURL");
            masterThumb=(String) data.get("Profileimg");
            masterToken = (String) data.get("masterToken");
            master=(String)data.get("Username");
        }
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookThumb() {
        return bookThumb;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getStartDate() {
        return StartDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public String getMaster() {
        return master;
    }

    public String getMasterThumb() {
        return masterThumb;
    }

    public String getTitle() {
        return Title;
    }

    public ArrayList<String> getCurrentPart() {
        return CurrentPart;
    }

    public Long getMaxPart() {
        return MaxPart;
    }

    public String getMasterToken() {
        return masterToken;
    }
}
