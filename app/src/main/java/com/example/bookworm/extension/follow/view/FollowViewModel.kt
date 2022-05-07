package com.example.bookworm.extension.follow.view

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookworm.core.dataprocessing.repository.UserRepositoryImpl
import com.example.bookworm.core.userdata.UserInfo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


//팔로우 관련 뷰 모델
class FollowViewModel(val context: Context) : ViewModel(){
    var followList = MutableLiveData<ArrayList<UserInfo>>()
    var data = MutableLiveData<UserInfo>()
    var lastVisibleUser: String? = null
    val repo = UserRepositoryImpl(context)

    class Factory(val context: Context):ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FollowViewModel(context) as T
        }
    }

    //사용자가 팔로우중인지 확인하는 메소드
    suspend fun isFollowNow(userInfo: UserInfo) = repo.isFollowNow(userInfo)

    //사용자 가져오기
    suspend fun getUser(token: String?) = repo.getUser(token)
    fun WithoutSuspendgetUser(token: String?){
        viewModelScope.launch {
            data.value = repo.getUser(token)
        }
    }
    /* 팔로우 목록 가져오기
     작업 루틴 :
     토큰리스트 -> 사용자목록을 가져옴
     => 팔로워인지 아닌지 확인 ->  확인된 UserInfo 리스트를 반환 */
    fun getFollowerList(token: String, check: Int) = viewModelScope.launch {
        var resultList: ArrayList<UserInfo>
        val deferredTokenList: Deferred<ArrayList<String>> = async(Dispatchers.IO) {
            repo.getFollowTokenList(token, check, lastVisibleUser)
        }
        var tokenList = deferredTokenList.await()
        val deferredFolowerList: Deferred<ArrayList<UserInfo>> = async(Dispatchers.IO) {
            if (!tokenList.isEmpty()) lastVisibleUser =
                tokenList.get(tokenList.size - 1)   //마지막에 가져온 사용자 정보를 저장
            var tmpFollowList = ArrayList<UserInfo>() //아직은 팔로우 여부를 체크하지 않은 User들의 정보가 들어감
            for (i in tokenList) tmpFollowList.add(repo.getUser(i)!!)
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

    //type : true 면 팔로우 , false 면 언팔로우
    suspend fun follow(toUserInfo: UserInfo, type: Boolean): UserInfo {
        var fromUserInfo = viewModelScope.async {
            repo.getUser(null)
        }.await()!!
        repo.followProcessing(fromUserInfo, toUserInfo, type).await()
        val returnValue = viewModelScope.async {
            getUser(toUserInfo.token)
        }.await()
        //새로운 값으로 뷰페이지 업데이트
        data.value = repo.getUser(null)
        return returnValue!!
    }
}