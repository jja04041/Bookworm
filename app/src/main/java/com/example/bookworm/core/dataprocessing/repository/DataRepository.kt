package com.example.bookworm.core.dataprocessing.repository

import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.core.userdata.UserInfo

interface DataRepository {

    interface HandleFeed {
        fun getFeedList(lastVisible:String?) //피드 리스트를 가져옴
        fun createFeed(feed: Feed) //피드를 업로드함
        fun deleteFeed(feedId:String) //피드를 삭제함
    }

    //사용자 관련 데이터를 다루는 공간
    interface HandleUser {
        suspend fun getBookWorm(token: String):BookWorm //책볼레 정보를 가져온다.
        suspend fun updateUser(user: UserInfo) //User 갱신 => 프로필 수정
        suspend fun createUser(user: UserInfo) :Boolean //사용자를 생성
        fun deleteUser() //회원 탈퇴
        suspend fun getUser(token: String?, isFirst: Boolean): UserInfo?//사용자의 정보를 가져옴
        suspend fun updateBookWorm(token: String,bookWorm: BookWorm)

    }

    interface HandleChallenge{
        suspend fun getChallenges(token: String):ArrayList<Challenge>

    }
}