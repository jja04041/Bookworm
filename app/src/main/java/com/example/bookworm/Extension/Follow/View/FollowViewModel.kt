package com.example.bookworm.Extension.Follow.View

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookworm.Core.DataProcessing.Repository.UserRepositoryImpl
import com.example.bookworm.Core.UserData.UserInfo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


//팔로우 관련 뷰 모델
class FollowViewModel(val context: Context) : ViewModel() {
    var followList = MutableLiveData<ArrayList<UserInfo>>()
    var lastVisibleUser: String? = null
    val repo = UserRepositoryImpl(context)


    //사용자가 팔로우중인지 확인하는 메소드
    suspend fun isFollowNow(userInfo: UserInfo) = repo.isFollowNow(userInfo)

    //사용자 가져오기
    suspend fun getUser(token: String?) = repo.getUser(token)

    // 팔로우 목록 가져오기
    // 작업 루틴 :
    // 토큰리스트 -> 사용자목록을 가져옴 => 팔로워인지 아닌지 확인 ->  확인된 UserInfo 리스트를 반환
    fun getFollowerList(token: String, check: Int) = viewModelScope.launch {
        var resultList: ArrayList<UserInfo>
        val deferredTokenList: Deferred<ArrayList<String>> = async(Dispatchers.IO) {
            repo.getFollowTokenList(token, check, lastVisibleUser)
        }
        var tokenList = deferredTokenList.await()
        val deferredFolowerList: Deferred<ArrayList<UserInfo>> = async(Dispatchers.IO) {
            if (!tokenList.isEmpty()) lastVisibleUser =
                tokenList.get(tokenList.size - 1)   //마지막에 가져온 사용자 정보를 저장
            var tmpFollowList = ArrayList<UserInfo>()
            for (i in tokenList) {
                tmpFollowList.add(repo.getUser(i)!!)
            }
            tmpFollowList
        }
        resultList = deferredFolowerList.await()
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

    //type : true 면 팔로우 , false 면 언팔로우
    suspend fun follow(toUserInfo: UserInfo, type: Boolean): UserInfo {
        var fromUserInfo = viewModelScope.async {
            repo.getUser(null)
        }.await()!!
        repo.followProcessing(fromUserInfo, toUserInfo, type).await()
        val returnValue = viewModelScope.async {
            getUser(toUserInfo.token)
        }.await()
        repo.updateUser(fromUserInfo)//새로 갱신된 데이터를 로컬과 서버 모두에 적용
        return returnValue!!
    }
}