package com.example.bookworm.bottomMenu.challenge

import android.content.Context
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.bottomMenu.feed.FireStoreLoadModule
import kotlinx.coroutines.tasks.await

//서버와 직접적인 교류를 가지는 Repository
class ChallengeDataRepository(context: Context) : ContractChallengeRepo {

    //챌린지 목록을 가져오는 메소드
    override suspend fun loadChallenges(keyword: String, lastVisible: String?, pageSize: Long) =
        try {
            ArrayList<Challenge>(
                FireStoreLoadModule.provideQueryChallenges().apply {
                    if (keyword != "") this.startAt(keyword).endAt(keyword + "\uf8ff")
                    if (lastVisible != "") this.startAfter(lastVisible)
                }.limit(pageSize).get().await().toObjects(Challenge::class.java)
            )
        } catch (e: Exception) {
            null
        }


    override suspend fun createChallenge(challenge: Challenge) {
        FireStoreLoadModule.provideQueryPathToChallengeCollection()
            .document(challenge.id).set(challenge).await()

    }

    //참여 가능한 경우에는 정상적으로 참여가 되게 하고, 안되는 경우에는 에러를 띄워준다.
    override suspend fun joinChallenge(challengeID: String, userToken: String) {
        val challengeRef = FireStoreLoadModule
            .provideQueryPathToChallengeCollection().document(challengeID)
        FireStoreLoadModule.provideFirebaseInstance().runTransaction { transaction ->
            transaction.apply {
                val doc = get(challengeRef)
                val currentList = doc["currentPart"] as MutableList<String>
                if (currentList.size < doc["maxPart"] as Long) {
                    currentList.add(userToken)
                    update(challengeRef, "currentPart", currentList)
                } else throw ArrayIndexOutOfBoundsException() //배열 초과 에러를 출력
            }
        }.await()
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