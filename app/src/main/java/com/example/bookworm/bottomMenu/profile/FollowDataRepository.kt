package com.example.bookworm.bottomMenu.profile

import android.content.Context
import com.example.bookworm.bottomMenu.feed.FireStoreLoadModule
import com.example.bookworm.core.dataprocessing.repository.UserRepository
import com.example.bookworm.core.userdata.UserInfo
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
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
    private val userDataRepository = UserRepository(context)

    //해당 사용자의 팔로우목록을 가져오는 함수 -> 서버와의 연동
    //false = follower , true = following
    suspend fun getFollowList(userToken: String, type: Boolean = false, isRefreshing: Boolean = false): MutableList<UserInfo> {
        val tmp = FireStoreLoadModule.provideQueryPathToUserCollection()
                .document(userToken)
        val query: Query
        val documents: List<DocumentSnapshot>

        //만약 새로고침 한 경우 페이징 커서를 리셋함.
        if (isRefreshing) {
            if (type) lastFollowingToken = "" else lastFollowerToken = ""
        }
        //팔로워 목록인 경우
        if (!type) {
            query = tmp.collection("follower")
                    .orderBy("userToken")
                    .limit(PAGE_SIZE)
            documents = if (lastFollowerToken != "") query.startAfter(lastFollowerToken).get().await().documents
            else query.get().await().documents
            lastFollowerToken = documents.last().id //임시 저장
        } else { //팔로잉 목록인 경우
            query = tmp.collection("following")
                    .orderBy("userToken")
                    .limit(PAGE_SIZE)
            documents = if (lastFollowingToken != "") query.startAfter(lastFollowingToken).get().await().documents
            else query.get().await().documents
            lastFollowingToken = documents.last().id //임시 저장
        }

        //먼저 토큰 목록을 가져옴
        val tokenList = emptyList<String>().toMutableList()
        for (i in documents) tokenList.add(i.id)
        //가져온 토큰목록을 사용자 정보로 치환해야함.
        val userList = FireStoreLoadModule.provideQueryPathToUserCollection().whereIn("userToken", tokenList).get().await()
        return userList.toObjects(UserInfo::class.java).map { userData ->
            return@map withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                if (!type || isFollowNow(userData)) userData.isFollowed = true //팔로잉 목록이거나, 팔로워 목록이지만 해당 유저를 맞팔한 경우
                return@withContext userData
            }
        }.toMutableList()
    }

    //사용자가 현재 팔로우 중인지 확인
    suspend fun isFollowNow(user: UserInfo): Boolean {
        val localUser = userDataRepository.getUser(null, false) //현재 유저의 정보를 가져옴
        //현재 유저의 팔로잉 목록에서 인자로 넘겨받은 유저의 토큰이 있는지 확인
        val query = FireStoreLoadModule.provideQueryPathToUserCollection().document(localUser!!.token).collection("following")
                .whereEqualTo(FieldPath.documentId(), user.token)
        val result = query.get().await() //진행 결과
        return result != null
    }
}