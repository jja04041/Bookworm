package com.example.bookworm.bottomMenu.challenge.items

import android.os.Parcelable
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.core.userdata.UserInfo
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import kotlin.collections.ArrayList

@Parcelize
data class Challenge(
        var id
        : String = "",
        var book // Book 객체 자체를 사용
        : Book = Book(),
        var startDate //챌린지 시작일
        : String = "",
        var endDate // 챌린지 마감일
        : String = "",
        var title //챌린지 명
        : String = "",
        var currentPart //현재 참가자 목록
        : ArrayList<String> = ArrayList(),
        var maxPart // 최대 참가 가능한 인원 수
        : Long = 0L,
        @Exclude @set:Exclude @get:Exclude
        var masterData //방장의 유저데이터
        : UserInfo = UserInfo(),
        @Exclude @set:Exclude @get:Exclude
        var dDay //디데이 설정
        : String = "",
        @Exclude @set:Exclude @get:Exclude
        var isFull //인원이 가득 찼는지 확인
        : Boolean = false,
        var masterToken //방장의 토큰값
        : String = "",
        var description //챌린지 설명
        : String = "",
) : Parcelable