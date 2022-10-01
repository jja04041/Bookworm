package com.example.bookworm.bottomMenu.challenge

import android.content.Context
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.bottomMenu.feed.FireStoreLoadModule
import kotlinx.coroutines.tasks.await

//서버와 직접적인 교류를 가지는 Repository
class ChallengeDataRepository(context: Context) : ContractChallengeRepo {

    //챌린지 목록을 가져오는 메소드
    override suspend fun loadChallenges(keyword: String, lastVisible: String?, pageSize: Long) = try {
        ArrayList<Challenge>(
                FireStoreLoadModule.provideQueryChallenges().apply {
                    if (keyword != "") this.startAt(keyword).endAt(keyword + "\uf8ff")
                    if (lastVisible != "") this.startAfter(lastVisible)
                }.limit(pageSize).get().await().toObjects(Challenge::class.java)
        )
    } catch (e: Exception) {
        null
    }


    override suspend fun createChallenge(challenge: Challenge): Boolean {
        return try {
            FireStoreLoadModule.provideQueryPathToChallengeCollection()
                    .document(challenge.id).set(challenge).await()
            true
        } catch (e: Exception) {
            false
        }

    }

    override suspend fun allowToJoinChallenge(challengeID: String) {
        TODO("Not yet implemented")
    }

    override suspend fun loadBoards() {
        TODO("Not yet implemented")
    }

    override suspend fun createBoard() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBoard() {
        TODO("Not yet implemented")
    }

    override suspend fun updateBoard() {
        TODO("Not yet implemented")
    }


}