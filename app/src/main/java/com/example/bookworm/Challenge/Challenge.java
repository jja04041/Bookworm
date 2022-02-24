package com.example.bookworm.Challenge;

import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Challenge {
    private String bookId; //챌린지에서 사용하는 도서의 Id값
    private String StartDate; //챌린지 시작일
    private String EndDate; // 챌린지 마감일
    private String Title; //챌린지 명
    private ArrayList<String> CurrentPart; //현재 참가자 목록
    private int MaxPart; // 최대 참가 가능한 인원 수
    private String masterToken; //방장의 토큰값
//    private UserInfo master; //방장의 UserInfo
    private String master;//방장명
    public Challenge(ArrayList<String> arr,int Max,String... strings) {
        CurrentPart=arr;
        MaxPart=Max;
        bookId=strings[0];
        StartDate=strings[1];
        EndDate=strings[2];
        Title=strings[3];
    }

    public String getBookId() {
        return bookId;
    }

    public String getStartDate() {
        return StartDate;
    }

    public String getTitle() {
        return Title;
    }

    public String getEndDate() {
        return EndDate;
    }

    public ArrayList<String> getCurrentPart() {
        return CurrentPart;
    }

    public int getMaxPart() {
        return MaxPart;
    }

    public String getMasterToken() {
        return masterToken;
    }

//    public UserInfo getMaster() {
//        return master;
//    }
}
