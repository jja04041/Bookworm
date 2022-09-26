package com.example.bookworm.core.userdata.interfaces

import com.google.firebase.firestore.Exclude
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


//사용자 정보를 담는 객체
data class UserInfo(
        var profileimg // 회원가입시 프로필사진
        : String = "",
        var username // 회원가입시 닉네임
        : String = "(알 수 없음)",
        var email// 로그인한 이메일
        : String? = null,
        var platform //플랫폼 확인
        : String? = null,
        var introduce // 자기소개
        : String = "안녕하세요~",
        var completedChallenge //인증 완료된 챌린지 개수
        : Long? = 0,
        var tier //인증 완료된 챌린지에 따른 티어(닉네임 옆 메달 표시용)
        : Long? = 0,
        var medalAppear: Boolean? = true,
        var fCMtoken: String? = null,
        var token: String = "",
        var likedPost: ArrayList<String>?,
        var followerCounts: Int = 0,
        var followingCounts: Int = 0,

        var genre: HashMap<String, Int?>? = HashMap(),
        var prefergenre: ArrayList<String> = ArrayList(),

        @Exclude
        var isMainUser //메인 유저인지 확인하는 변수
        : Boolean = false,

        @Exclude
        var isFollowed//팔로우 여부 확인 하는 변수
        : Boolean = false,
)
