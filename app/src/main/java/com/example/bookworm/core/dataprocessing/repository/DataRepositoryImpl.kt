package com.example.bookworm.core.dataprocessing.repository


import android.content.SharedPreferences
import com.example.bookworm.bottomMenu.Feed.items.Feed

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class DataRepositoryImpl : DataRepository {
    //변수
    val db = FirebaseFirestore.getInstance() //파이어스토어와 연결
    var pref: SharedPreferences? = null //sharedPreference과 연결
    lateinit var collectionReference: CollectionReference //참조 경로
    val query: Query? = null //파이어베이스로 날릴 쿼리

    //사용자 정보 레포지토리

    //피드 레포지토리
    inner class FeedRepositoryImpl : DataRepository.HandleFeed {
        val mainCollection: String = "feed"

        init {
            collectionReference = db.collection(mainCollection)
        }

        override fun getFeedList() {

        }

        //피드 생성
        override fun createFeed(feed: Feed) {

        }

        //피드 삭제
        override fun deleteFeed(feedId: String) {

        }

    }

    //책볼레 레포지토리
}