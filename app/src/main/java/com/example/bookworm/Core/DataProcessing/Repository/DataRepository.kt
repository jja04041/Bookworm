package com.example.bookworm.Core.DataProcessing.Repository

import com.example.bookworm.BottomMenu.Feed.items.Feed
import com.example.bookworm.Core.UserData.UserInfo

interface DataRepository {
    interface HandleFeed {
        fun getFeedList() //피드 리스트를 가져옴
        fun createFeed(feed: Feed) //피드를 업로드함
        fun deleteFeed(feedId:String) //피드를 삭제함
    }

    interface HandleUser {
        suspend fun getUser(token: String?): UserInfo? //사용자의 정보를 가져옴
        suspend fun getFollowTokenList(
            token: String,
            check: Int,
            lastVisible: String?
        ) : ArrayList<String>//사용자의 정보를 가져옴
        fun updateUser(user: UserInfo) //User 갱신 => 프로필 수정
        suspend fun createUser(user: UserInfo) //사용자를 생성
        fun deleteUser() //회원 탈퇴
    }


    interface HandleBookWorm {

    }
}