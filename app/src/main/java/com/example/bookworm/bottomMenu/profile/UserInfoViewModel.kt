package com.example.bookworm.bottomMenu.profile

import android.content.Context
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookworm.core.dataprocessing.repository.UserRepositoryImpl
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.extension.follow.view.FollowViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

//전반적으로 User을 관리하는 ViewModel
class UserInfoViewModel(val context: Context) : ViewModel() {
    //LiveData : 동적으로 데이터의 변경이 이루어 짐
    //MutableLiveData는 읽기/쓰기 모두 가능
    //LiveData 선언 시에는 읽기만 가능

    var data = MutableLiveData<UserInfo>()
    var followList = MutableLiveData<ArrayList<UserInfo>>()
    var lastVisibleUser: String? = null
    var isDuplicated = MutableLiveData<Boolean>()
    val repo = UserRepositoryImpl(context)

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UserInfoViewModel(context) as T
        }
    }

    //사용자 가져오기
    fun getUser(token: String?) {
        viewModelScope.launch {
            data.value = repo.getUser(token) //데이터 변경을 감지하면, 값이 업데이트 된다.
        }
    }

    //사용자 생성
    suspend fun createUser(userInfo: UserInfo) =
        viewModelScope.async {
            repo.createUser(userInfo) //값을 가져올 필요는 없으므로
        }.await()


    //이름 중복 확인
    fun checkDuplicate(name: String) {
        viewModelScope.launch {
            var collection = FirebaseFirestore.getInstance().collection("users")
            var query = collection.whereEqualTo("UserInfo.username", name)
            launch {
                query.get()
                    .addOnSuccessListener {
                        isDuplicated.value = !it.isEmpty //비어있다면(isEmpty=true) 중복이 아닌 것이고,
                        Log.d("result", Arrays.toString(it.documents.toTypedArray()))
                        //비어 있지 않다면 (isEmpty=false) 중복인 것.
                    }.addOnFailureListener({
                        Log.e("resultErr", "cannot get result ")
                    })
            }.join()
        }

    }

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
        followList.value = resultList
    }

}

