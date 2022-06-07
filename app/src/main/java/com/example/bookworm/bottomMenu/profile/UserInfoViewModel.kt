package com.example.bookworm.bottomMenu.profile

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.core.dataprocessing.repository.UserRepositoryImpl
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.extension.follow.view.FollowViewModelImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

//전반적으로 User을 관리하는 ViewModel
class UserInfoViewModel(val context: Context) : ViewModel() {

    //LiveData : 동적으로 데이터의 변경이 이루어 짐
    //MutableLiveData는 읽기/쓰기 모두 가능
    //LiveData 선언 시에는 읽기만 가능

    var data = MutableLiveData<UserInfo>() // 사용자 데이터 LiveData
    var bwdata = MutableLiveData<BookWorm>() // 사용자의 BookWorm LiveData
    var albumdata = MutableLiveData<ArrayList<AlbumData>>()
    var isDuplicated = MutableLiveData<Boolean>() //중복 여부를 체크 하는 LiveData
    val repo = UserRepositoryImpl(context)
    var fv: FollowViewModelImpl
    var feedList = MutableLiveData<ArrayList<Feed>>()

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UserInfoViewModel(context) as T
        }
    }

    init {
        fv = ViewModelProvider(
            context as AppCompatActivity,
            FollowViewModelImpl.Factory(context)
        ).get(
            FollowViewModelImpl::class.java
        )
    }

    //사용자 가져오기
    fun getUser(token: String?, getFromExt: Boolean) {
        viewModelScope.launch {
            data.value = repo.getUser(token, getFromExt) //데이터 변경을 감지하면, 값이 업데이트 된다.
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

    fun getFollowerList(token: String, getFollower: Boolean) =
        fv.getFollowerList(token, getFollower)

    fun getBookWorm(token: String) =
        viewModelScope.launch {
            bwdata.value = repo.getBookWorm(token)
        }

    fun updateUser(user: UserInfo) {
        viewModelScope.launch {
            repo.updateUser(user)
        }
    }

    fun updateBw(token: String?, bookWorm: BookWorm) {
        viewModelScope.launch {
            repo.updateBookWorm(token, bookWorm)
        }
    }

    fun getalbums(token: String?) {
        viewModelScope.launch {
            albumdata.value = repo.getAlbums(token)
        }
    }

    fun getFeedList(token: String) {
        viewModelScope.launch {
            var data =
                FirebaseFirestore.getInstance().collection("feed").whereEqualTo("UserToken", token)
                    .orderBy(
                        "FeedID",
                        Query.Direction.DESCENDING
                    )
                    .get().await()
            var arrayList = ArrayList<Feed>()
            data.documents.forEach {
                var feed = Feed()
                feed.setFeedData(it.getData())
                arrayList.add(feed)
            }
            feedList.value = arrayList
        }
    }

}

