package com.example.bookworm.bottomMenu.profile

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumData
import com.example.bookworm.core.dataprocessing.repository.UserRepository
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.extension.follow.view.FollowViewModelImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

//전반적으로 User을 관리하는 ViewModel
class UserInfoViewModel(context: Context) : ViewModel() {

    //LiveData : 동적으로 데이터의 변경이 이루어 짐
    //MutableLiveData는 읽기/쓰기 모두 가능
    //LiveData 선언 시에는 읽기만 가능

    var userInfoLiveData = MutableLiveData<UserInfo>() // 사용자 데이터 LiveData
    var bwdata = MutableLiveData<BookWorm>() // 사용자의 BookWorm LiveData
    var albumdata = MutableLiveData<ArrayList<AlbumData>>()
    var isDuplicated = MutableLiveData<Boolean>() //중복 여부를 체크 하는 LiveData
    val repo = UserRepository(context)
    var feedList = MutableLiveData<ArrayList<Feed>>()

    enum class State { Loading, Done, Error } //로딩중 , 로딩 끝 , 에러

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserInfoViewModel(context) as T
        }
    }

    fun setUserLiveData(liveData: MutableLiveData<UserInfo>) {
        userInfoLiveData = liveData;
    }

    //사용자 가져오기
    fun getUser(token: String?, getFromExt: Boolean) {
        viewModelScope.launch {
            userInfoLiveData.value = repo.getUser(token, getFromExt) //데이터 변경을 감지하면, 값이 업데이트 된다.
        }
    }

    suspend fun suspendGetUser(token: String?) = repo.getUser(token, token != null)

    //사용자 가져오기
    fun getUser(token: String?, liveData: MutableLiveData<UserInfo>,getFromExt: Boolean = true) {
        viewModelScope.launch {
            liveData.value = repo.getUser(token, getFromExt) //데이터 변경을 감지하면, 값이 업데이트 된다.
        }
    }

    //사용자 생성
    fun createUser(userInfo: UserInfo, liveData: MutableLiveData<Boolean>) =

            viewModelScope.launch {
                liveData.value = repo.createUser(userInfo) //값을 가져올 필요는 없으므로
            }


    //이름 중복 확인
    fun checkDuplicate(name: String) {
        viewModelScope.launch {
            val collection = FirebaseFirestore.getInstance().collection("users")
            val query = collection.whereEqualTo("UserInfo.username", name)
            launch {
                query.get()
                        .addOnSuccessListener {
                            isDuplicated.value = !it.isEmpty //비어있다면(isEmpty=true) 중복이 아닌 것이고,
                            Log.d("result", Arrays.toString(it.documents.toTypedArray()))
                            //비어 있지 않다면 (isEmpty=false) 중복인 것.
                        }.addOnFailureListener {
                            Log.e("resultErr", "cannot get result ")
                        }
            }.join()
        }
    }


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
                    FirebaseFirestore.getInstance().collection("feed").whereEqualTo("userToken", token)
                            .orderBy(
                                    "feedID",
                                    Query.Direction.DESCENDING
                            )
                            .get().await()
            var arrayList = ArrayList<Feed>()
            data.documents.forEach {
                var feed = it.toObject(Feed::class.java)!!
                feed.apply {
                    creatorInfo = suspendGetUser(userToken)!!
                }
                arrayList.add(feed)
            }
            feedList.value = arrayList
        }
    }

    fun deleteUser(stateLiveData: MutableLiveData<State>) {
        stateLiveData.value = State.Loading
        viewModelScope.launch {
            repo.deleteUser()
            stateLiveData.value = State.Done
        }

    }

    fun logOut() = repo.logOut()


    //장르를 설정하는 코드
    fun setGenre(categoryname: String?, user: UserInfo): String {
        val tokenizer = StringTokenizer(categoryname, ">")
        tokenizer.nextToken() // 두번째 분류를 원하기 때문에 맨 앞 분류 꺼냄
        var category = tokenizer.nextToken()
        when (category) {
            "자기계발", "에세이", "예술/대중문화" -> {
                category = "자기계발"
            }
            "소설/시/희곡", "장르소설", "전집/중고전집" -> {
                category = "소설"
            }
            "좋은부모" -> {
                category = "육아"
            }
            "사회과학", "경제경영" -> {
                category = "사회"
            }
            "어린이", "유아" -> {
                category = "어린이"
            }
            "종교/역학", "인문학", "역사", "고전" -> {
                category = "인문"
            }
            "가정/요리/뷰티", "건강/취미/레저", "여행", "잡지", "달력/기타" -> {
                category = "생활"
            }
            "외국어", "대학교재", "초중고참고서", "수험서/자격증", "공무원 수험서", "컴퓨터/모바일" -> {
                category = "공부"
            }
        }
        var unboxint = 0
        user.apply {
            if (null == genre!![category]) {
                genre!![category] = 1
            } else {
                unboxint = genre!![category]!!
                unboxint++
                genre!!.replace(category, unboxint)
            }
        }
        return category
    }
}

