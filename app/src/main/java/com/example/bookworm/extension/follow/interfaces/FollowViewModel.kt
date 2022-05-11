package com.example.bookworm.extension.follow.interfaces

import com.example.bookworm.core.userdata.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.Job


//팔로우 관련 뷰 모델
//각 함수에 대한 정보
interface FollowViewModel {
    suspend fun getUser(token: String?, getFromExt: Boolean): UserInfo //사용자의 정보를 비동기적으로 가져온다
    suspend fun isFollowNow(userInfo: UserInfo): Boolean    //사용자가 팔로우중인지 확인하는 메소드
    fun WithoutSuspendgetUser(token: String?) //LiveData의 값을 변경하여, UserInfo값을 얻어온다.
    suspend fun follow(toUserInfo: UserInfo, type: Boolean): UserInfo //type : true 면 팔로우 , false 면 언팔로우
    /* 팔로우 목록 가져오기
        작업 루틴 :
        토큰리스트 -> 사용자목록을 가져옴
        => 팔로워인지 아닌지 확인 ->  확인된 UserInfo 리스트를 반환 */
    fun getFollowerList(token: String, getFollower: Boolean): Job
    suspend fun getFollowTokenList(
        token: String,
        getFollower: Boolean,
        lastVisible: String?
    ): ArrayList<String>//사용자의 정보를 가져옴
    fun followProcessing(
        fromUserInfo: UserInfo,
        toUserInfo: UserInfo,
        type: Boolean
    ): Task<Transaction>




}