package com.example.bookworm.core.dataprocessing.repository

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.bottomMenu.feed.FireStoreLoadModule
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.extension.follow.view.FollowViewModelImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import java.lang.NullPointerException

class ChallengeRepository(context: Context) : DataRepository.HandleChallenge {
    var followViewModel =
            ViewModelProvider(context as AppCompatActivity, FollowViewModelImpl.Factory(context)).get(
                    FollowViewModelImpl::class.java
            )
    private val challengeColRef = FireStoreLoadModule.provideQueryPathToChallengeCollection()

    //파이어베이스 서버로 부터 챌린지 목록을 가져온다.
    override suspend fun getChallenges(token: String, keyword: String?, lastVisible: String, pageSize: Long) =
            try {
                ArrayList<Challenge>(
                        if (keyword == null) {
                            challengeColRef
                        } else { //검색인 경우
                            challengeColRef
                                    .orderBy("id")
                                    .startAt(keyword)
                                    .endAt(keyword + "\uf8ff")
                        }.apply {
                            if (lastVisible !="") this.startAfter(lastVisible) //만약 이전 데이터를 이어서 받아오는 경우
                        }.limit(pageSize)
                                .get()
                                .await()
                                .toObjects(Challenge::class.java))//전달받은 데이터를 자동으로 Challenge 객체로 변환해 준다.)
            } catch (e: NullPointerException) {
                null //데이터를 가져오는데 오류가 난 경우
            }

    //파이어베이스 서버에 챌린지를 추가한다.
    override suspend fun createChallenge(challenge: Challenge) {
        TODO("Not yet implemented")
    }

}