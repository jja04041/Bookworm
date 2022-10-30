package com.example.bookworm.bottomMenu.profile.submenu.album

import android.os.Parcelable
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.core.userdata.UserInfo
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import java.io.Serializable

//앨범 객체
@Parcelize
data class AlbumData(
        var albumId //앨범 아이디
        : String = "",
        var thumbnail //앨범커버
        : String = "",
        var albumName //앨범 이름
        : String = "",
        var selectedFeedList //앨범이 가지는 피드 리스트
        : ArrayList<Feed> = ArrayList(),
        var creatorToken
        : String = "", //앨범 작성자 토큰
        @get:Exclude @set:Exclude @Exclude
        var creatorInfo
        : UserInfo = UserInfo(),
        var albumDesc //앨범 설명
        :String = ""
) : Parcelable

