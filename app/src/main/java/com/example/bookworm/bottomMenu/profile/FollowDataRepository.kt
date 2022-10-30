package com.example.bookworm.bottomMenu.profile

import android.content.Context
import android.widget.Toast
import com.example.bookworm.bottomMenu.feed.FireStoreLoadModule
import com.example.bookworm.core.dataprocessing.repository.UserRepository
import com.example.bookworm.core.userdata.UserInfo
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FollowDataRepository(context: Context) {
    val PAGE_SIZE = 10L
    private var lastFollowerToken = ""
    private var lastFollowingToken = ""
    private val userDataRepository = UserRepository(context) //사용자 정보를 관리하는 레포지토리

    //해당 사용자의 팔로우목록을 가져오는 함수 -> 서버와의 연동
    //true = follower , false = following
    suspend fun getFollowList(
        userToken: String,
        type: Boolean = false,
        isRefreshing: Boolean = false
    ): MutableList<UserInfo>? {
        val tmp = FireStoreLoadModule.provideQueryPathToUserCollection()
            .document(userToken)
        val query: Query
        val documents: List<DocumentSnapshot>

        //만약 새로고침 한 경우 페이징 커서를 리셋함.
        if (isRefreshing) {
            if (type) lastFollowerToken = "" else lastFollowingToken = ""
        }

        try {
            //팔로워 목록인 경우
            if (type) {
                query = tmp.collection("follower")
                    .orderBy("token")
                    .limit(PAGE_SIZE)
                documents = if (lastFollowerToken != "") query.startAfter(lastFollowerToken).get()
                    .await().documents
                else query.get().await().documents
                lastFollowerToken = documents.last().id //임시 저장
            } else { //팔로잉 목록인 경우
                query = tmp.collection("following")
                    .orderBy("token")
                    .limit(PAGE_SIZE)
                documents = if (lastFollowingToken != "") query.startAfter(lastFollowingToken).get()
                    .await().documents
                else query.get().await().documents
                lastFollowingToken = documents.last().id //임시 저장
            }

            //먼저 토큰 목록을 가져옴
            val tokenList = emptyList<String>().toMutableList()
            for (i in documents) tokenList.add(i.id)
            //가져온 토큰목록을 사용자 정보로 치환해야함.
            val userList =
                FireStoreLoadModule.provideQueryPathToUserCollection().whereIn("token", tokenList)
                    .get().await()
            return userList.toObjects(UserInfo::class.java).map { userData ->
                return@map withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                    val isFollow = isFollowNow(userData.token)
                    if (!type || isFollow) userData.isFollowed =
                        true //팔로잉 목록이거나, 팔로워 목록이지만 해당 유저를 맞팔한 경우
                    return@withContext userData
                }
            }.toMutableList()
        } catch (e: Exception) {
            return null
        }
    }

    //사용자가 현재 팔로우 중인지 확인
    suspend fun isFollowNow(userToken: String): Boolean {
        val localUser = userDataRepository.getUser(null, false) //현재 유저의 정보를 가져옴
        //현재 유저의 팔로잉 목록에서 인자로 넘겨받은 유저의 토큰이 있는지 확인
        val query =
            FireStoreLoadModule.provideQueryPathToUserCollection().document(localUser!!.token)
                .collection("following")
                .whereEqualTo(FieldPath.documentId(), userToken)
        return !query.get().await().isEmpty
    }

    //사용자를 팔로우/언팔로우 세팅하는 메소드
    suspend fun setFollowUser(toUserInfo: UserInfo, isFollow: Boolean): UserInfo? {
        val fromUserInfo = userDataRepository.getUser(null, false)!! //현재 유저의 정보를 가져온다.
        //각 유저의 문서 위치를 가지고 있는 변수들
        val fromUserRef =
            FireStoreLoadModule.provideQueryPathToUserCollection().document(fromUserInfo.token)
        val toUserRef =
            FireStoreLoadModule.provideQueryPathToUserCollection().document(toUserInfo.token)
        //각 유저의 팔로잉과 팔로워 콜렉션의 위치를 가지고 있는 변수들
        val fromUserFollowingCol = fromUserRef.collection("following").document(toUserInfo.token)
        val toUserFollowingCol = toUserRef.collection("follower").document(fromUserInfo.token)
        //트랜잭션 객체
        val transaction = FireStoreLoadModule.provideFirebaseInstance().runTransaction { ts ->
            //각 사용자의 팔로워/팔로잉 콜렉션에 서로의 토큰을 추가한다.
            if (isFollow) ts.set(fromUserFollowingCol, toUserInfo)
                .set(toUserFollowingCol, fromUserInfo)
            else ts.delete(fromUserFollowingCol).delete(toUserFollowingCol)
            //각 사용자의 팔로워/팔로잉 수를 조작한다.
            val count = if (isFollow) 1L else -1L
            ts.update(fromUserRef, "followingCounts", FieldValue.increment(count))
                .update(toUserRef, "followerCounts", FieldValue.increment(count))
        }.await()
        if (transaction != null) {
            userDataRepository.apply {
                val returnValue = getUser(toUserInfo.token, true)
                updateInLocal(getUser(null, true)!!) //업데이트 된 사용자 정보를 로컬에도 반영
                return returnValue!!
            }
        } else return null
    }
}