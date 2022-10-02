package com.example.bookworm.bottomMenu.feed.comments

import android.os.Parcelable
import com.example.bookworm.core.userdata.UserInfo
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
        var commentID: String = "", //댓글 ID
        var userToken: String ?= "", //댓글 작성자 토큰
        var contents: String ?= "", // 댓글 내용
        var madeDate: String ?= "", // 댓글 생성일자
        @Exclude
        var position: Int ?=0, //댓글의 위치
        @Exclude
        var duration: String? ="", //댓글 생성일자(~일전 등)
        @Exclude
        var creator: UserInfo?= null, // 댓글 작성자 정보
) : Parcelable //Content를 통해 전달할 수도 있을까봐 적용해둠
