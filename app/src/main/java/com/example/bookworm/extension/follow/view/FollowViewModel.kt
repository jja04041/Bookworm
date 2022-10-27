package com.example.bookworm.extension.follow.view

import android.content.Context
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.bookworm.bottomMenu.feed.FireStoreLoadModule
import com.example.bookworm.bottomMenu.profile.FollowDataRepository
import com.example.bookworm.core.dataprocessing.repository.UserRepository
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.extension.follow.interfaces.FollowViewModelInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

//FollowViewModel 구현체
class FollowViewModel(context: Context) : ViewModel(), FollowViewModelInterface {
    var followList = MutableLiveData<ArrayList<UserInfo>>()
    var data = MutableLiveData<UserInfo?>()
    private val userRepository = UserRepository(context)
    private val followDataRepository = FollowDataRepository(context)

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return FollowViewModel(context) as T
        }
    }

    override suspend fun isFollowNow(userInfo: UserInfo) =
            CoroutineScope(Dispatchers.IO).async {
                var localUser = getUser(null, false) //현재 유저의 정보를 가져옴
                //현재 유저의 팔로잉 목록에서 인자로 넘겨받은 유저의 토큰이 있는지 확인
                var query = FireStoreLoadModule.provideQueryPathToUserCollection().document(localUser!!.token).collection("following")
                        .whereEqualTo(FieldPath.documentId(), userInfo.token)
                //리턴 값
                withContext(Dispatchers.Default) {
                    val it = query.get().await()
                    !it.isEmpty //리턴 값
                }
            }.await()

    override suspend fun getUser(token: String?, getFromExt: Boolean) = userRepository.getUser(token, getFromExt)

    override fun WithoutSuspendgetUser(token: String?) {
        viewModelScope.launch {
            data.value = userRepository.getUser(token, true)
        }
    }

    fun getUser(liveData: MutableLiveData<UserInfo>, token: String) {
        viewModelScope.launch {
            liveData.value = userRepository.getUser(token, true)
        }
    }

    fun getFollowList(listLiveData: MutableLiveData<MutableList<UserInfo>>, type: Boolean, token: String, isRefreshing: Boolean) {
        viewModelScope.launch {
            listLiveData.value!!.addAll(followDataRepository.getFollowList(userToken = token, type = type, isRefreshing)) //추가함.
        }
    }

    override fun follow(toUserInfo: UserInfo, type: Boolean, userLiveData: MutableLiveData<UserInfo>) {
        viewModelScope.launch {
            userLiveData.value = userRepository.follow(toUserInfo, type)
        }
    }

    //팔로우 처리
    override fun followProcessing(
            fromUserInfo: UserInfo,
            toUserInfo: UserInfo,
            type: Boolean,
    ): Task<Transaction> {
        val fromRef = FireStoreLoadModule.provideQueryPathToUserCollection().document(fromUserInfo.token) //팔로우 하려는 사용자
        val toRef = FireStoreLoadModule.provideQueryPathToUserCollection().document(toUserInfo.token) //팔로우 대상 사용자
        val fromRefFollow = fromRef.collection("following").document(toUserInfo.token) //팔로우 하려는 사용자의 following 경로
        val toRefFollow = toRef.collection("follower").document(fromUserInfo.token) //팔로우 대상 사용자의 follower 경로

        //Transaction 객체를 생성함.
        return FireStoreLoadModule.provideFirebaseInstance().runTransaction { transaction ->
            val current = transaction.get(fromRef).getLong("followingCounts") //팔로우 하려는 사용자 현재 팔로우 수 확인
            val count = if (type) 1L else -1L //타입에 따라 다르게 진행

            transaction.update(fromRef, "followingCounts", FieldValue.increment(count))
                    .update(toRef, "followerCounts", FieldValue.increment(count))
            fromUserInfo.followingCounts = current!!.plus(count).toInt()

            userRepository.updateInLocal(fromUserInfo)//새로 갱신된 데이터를 로컬과 서버 모두에 적용

            //목록에 리스트 반영
            if (type) transaction.set(fromRefFollow, toUserInfo).set(toRefFollow, fromUserInfo) //서로의 팔로잉, 팔로우 목록에 각 사용자의 토큰값을 저장한 후 세팅한다.
            else transaction.delete(fromRefFollow).delete(toRefFollow)
        }
    }
}