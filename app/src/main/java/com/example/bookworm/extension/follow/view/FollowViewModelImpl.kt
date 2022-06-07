package com.example.bookworm.extension.follow.view

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookworm.core.dataprocessing.repository.UserRepositoryImpl
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.extension.follow.interfaces.FollowViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

//FollowViewModel 구현체
class FollowViewModelImpl(val context: Context) : ViewModel(), FollowViewModel {
    val db = FirebaseFirestore.getInstance() //파이어스토어와 연결
    var collectionReference = db.collection("users")
    var followList = MutableLiveData<ArrayList<UserInfo>>()
    var data = MutableLiveData<UserInfo>()
    var lastVisibleUser: String? = null
    val repo = UserRepositoryImpl(context)

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FollowViewModelImpl(context) as T
        }
    }

    override suspend fun isFollowNow(userInfo: UserInfo) =
        CoroutineScope(Dispatchers.IO).async {
            var localUser = getUser(null, false) //현재 유저의 정보를 가져옴
            //현재 유저의 팔로잉 목록에서 인자로 넘겨받은 유저의 토큰이 있는지 확인
            var query = collectionReference.document(localUser!!.token).collection("following")
                .whereEqualTo(FieldPath.documentId(), userInfo.token)
            async {
                var it = query.get().await()
                !it.isEmpty //리턴 값
            }.await()
        }.await()

    override suspend fun getUser(token: String?, getFromExt: Boolean) =
        repo.getUser(token, getFromExt)

    override fun WithoutSuspendgetUser(token: String?) {
        viewModelScope.launch {
            data.value = repo.getUser(token, true)
        }
    }

    override suspend fun getFollowTokenList(
        token: String,
        getFollower: Boolean,
        lastVisible: String?
    ) = CoroutineScope(Dispatchers.IO).async {
        var tokenList = ArrayList<String>()
        launch {
            val type = if (getFollower) "follower" else "following"
            var query = collectionReference.document(token).collection(type).orderBy("token")
            if (lastVisible != null) query = query.startAfter(lastVisible)
            var it = query.limit(10).get().await()
            for (i in it.documents) tokenList.add(i.id)
        }
        tokenList
    }.await()

    override fun getFollowerList(token: String, getFollower: Boolean) = viewModelScope.launch {
        var resultList: ArrayList<UserInfo> //최종적으로 출력할 데이터
        val deferredTokenList: Deferred<ArrayList<String>> = async(Dispatchers.IO) {
            getFollowTokenList(token, getFollower, lastVisibleUser)
        }
        var tokenList = deferredTokenList.await()
        val deferredFolowerList: Deferred<ArrayList<UserInfo>> = async(Dispatchers.IO) {
            if (!tokenList.isEmpty()) lastVisibleUser =
                tokenList.get(tokenList.size - 1)   //마지막에 가져온 사용자 정보를 저장
            var tmpFollowList = ArrayList<UserInfo>() //아직은 팔로우 여부를 체크하지 않은 User들의 정보가 들어감
            for (i in tokenList) tmpFollowList.add(repo.getUser(i, false)!!)
            tmpFollowList
        }
        resultList = deferredFolowerList.await() //위 작업이 진행 된 후 결과값이 넘어옴
        //리스트에 담긴 내용이 차례대로 진행된다 .
        (0..resultList.size - 1).map {
            var user = resultList.get(it)
            var data = async(Dispatchers.IO) {
                repo.isFollowNow(user)
            }.await()
            if (data == true) {
                user.isFollowed = true
                resultList.set(it, user)
            }
        }
        followList.value = resultList//가져온 값을 결과로 셋팅
    }

    override suspend fun follow(toUserInfo: UserInfo, type: Boolean): UserInfo {
        var fromUserInfo = viewModelScope.async {
            getUser(null, true)
        }.await()!!
        followProcessing(fromUserInfo, toUserInfo, type).await()
        val returnValue = viewModelScope.async {
            getUser(toUserInfo.token, true)
        }.await()
        //새로운 값으로 뷰페이지 업데이트
        data.value = getUser(null, true)
        return returnValue!!
    }

    //팔로우 처리
    override fun followProcessing(
        fromUserInfo: UserInfo,
        toUserInfo: UserInfo,
        type: Boolean
    ): Task<Transaction> {
        val fromRef = collectionReference.document(fromUserInfo.token)
        val toRef = collectionReference.document(toUserInfo.token)
        val fromRefFollow = fromRef.collection("following").document(toUserInfo.token)
        val toRefFollow = toRef.collection("follower").document(fromUserInfo.token)

        return db.runTransaction {
            var current = it.get(fromRef).getLong("UserInfo.followingCounts")
            var count = if (type) 1 else -1.toLong()
            current = current?.plus(count)
            it.update(fromRef, "UserInfo.followingCounts", current)
                .update(toRef, "UserInfo.followerCounts", FieldValue.increment(count))
            fromUserInfo.followingCounts = current!!.toInt()
            repo.updateInLocal(fromUserInfo)//새로 갱신된 데이터를 로컬과 서버 모두에 적용
            if (type) {
                it.set(fromRefFollow, toUserInfo).set(toRefFollow, fromUserInfo)
            } else {
                it.delete(fromRefFollow)
                    .delete(toRefFollow)
            }
        }
    }
}