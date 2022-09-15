package com.example.bookworm.bottomMenu.feed

import android.os.Parcelable
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.example.bookworm.bottomMenu.search.items.Book
import com.example.bookworm.core.userdata.UserInfo
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

//Feed에 대한 데이터 클래스

@Parcelize
data class Feed(
        var book: Book? = null,
        //라벨 Array 추가
        var FeedID //피드 ID
        : String? = null,
        var imgurl //업로드 이미지 url
        : String = "",
        var commentsCount //사용자 댓글 수
        : Long = 0,
        var feedText: //피드의 내용
        String? = null,

        val date //현재 날짜
        : String? = null,

        var likeCount //좋아요 수
        : Long = 0,
        var Creator //작성자 정보
        : UserInfo? = null,

        val UserToken
        : String? = null,

        val label //라벨 목록
        : ArrayList<String>? = null,

        @Exclude
        var comment //최상단의 댓글을 가져옴 -> 서버에 업로드 시엔 이 필드를 제외함. (Exclude Annotation 이용)
        : Comment? = null,

        @Exclude
        var position //피드 수정,삭제시 리사이클러뷰의 포지션을 가져올때 사용
        : Int = 0,

        @Exclude
        var duration //작성된 후 지난 기간
        : String? = null,

        @Exclude
        var isUserLiked // 현재 사용하는 유저가 좋아하는 포스트인지 판단하는  Boolean
        : Boolean = false,

        @Exclude
        var isUserPost //이것이 현재 사용자의 게시물인지 확인하는 변수
        : Boolean = false
) : Parcelable

