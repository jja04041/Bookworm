package com.example.bookworm.bottomMenu.feed.temp

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.*
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FeedViewModel(val context: Context) : ViewModel() {
    private val PAGE_SIZE = 5
    private val queryPostsByName = FireStoreLoadModule.provideQueryPostsByUserID(PAGE_SIZE)
    private val modificationEvents = MutableStateFlow<List<FeedViewEvents>>(emptyList())


    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedViewModel(context) as T
        }
    }

    sealed class FeedViewEvents {
        data class Edit(val entity: Feed) : FeedViewEvents()
        data class Remove(val entity: Feed) : FeedViewEvents()
    }

    private val flow = Pager(PagingConfig(pageSize = PAGE_SIZE))
    {
        FirebasePagingSource(queryPostsByName)
    }.flow.map { pagingData ->
        pagingData.map { feed ->

            //피드에 담을 데이터를 불러온 후 매핑
            val userInfoViewModel = ViewModelProvider(context as MainActivity, UserInfoViewModel.Factory(context))[UserInfoViewModel::class.java]
            return@map withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                var it = feed
                it.Creator = userInfoViewModel.suspendGetUser(it.UserToken!!)
                it.duration = getDateDuration(it.date)
                if (it.commentsCount > 0L) {
                    it.comment = FireStoreLoadModule
                            .provideQueryCommentsLately(it.FeedID!!)
                            .get().await()
                            .toObjects(Comment::class.java)[0] //형변환을 자동으로 해줌.
                    it.comment!!.creator = userInfoViewModel.suspendGetUser(it.comment!!.userToken)
                    it.comment!!.duration = getDateDuration(it.comment!!.madeDate)
                }
                return@withContext it
            }
        }
    }.cachedIn(viewModelScope)
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEvents(acc, event)
                }
            }

    //현재 상태의 피드를 유지하는 것
    val pagingDataViewStates = flow.asLiveData()


    fun getLastComment(feedId: String, liveData: MutableLiveData<Comment>) {
        viewModelScope.launch {
            liveData.value = FireStoreLoadModule
                    .provideQueryCommentsLately(feedId)
                    .get().await()
                    .toObjects(Comment::class.java)[0] //형변환을 자동으로 해줌.
        }
    }

    //이벤트를 설정한다.
    fun onViewEvent(events: FeedViewEvents) {
        modificationEvents.value += events
    }

    //이벤트를 적용
    private fun applyEvents(pagingData: PagingData<Feed>, viewEvents: FeedViewEvents): PagingData<Feed> {
        return when (viewEvents) {
            is FeedViewEvents.Remove -> {
                //입력된 데이터를 제외한 다른 데이터들만 페이징 데이터로 가져간다.
                pagingData.filter { viewEvents.entity.FeedID != it.FeedID }
            }
            //수정
            else -> {
                //전달된 데이터로 교체
                pagingData.map {
                    if ((viewEvents as FeedViewEvents.Edit).entity.FeedID == it.FeedID) return@map viewEvents.entity
                    else return@map it
                }
            }
        }
    }
//피드 메뉴

    //피드 삭제
    fun deleteFeed(feed: Feed) {
        onViewEvent(FeedViewEvents.Remove(feed))
    }

    //        viewModelScope.launch {
////            FireStoreLoadModule.provideQueryPostByFeedID(feedId).delete()
//            //서버에 요청해서 삭제하는 것도 방법!
//            onViewEvent(FeedViewEvents.Remove(feed))
//시간차 구하기 n분 전, n시간 전 등등
    fun getDateDuration(createdTime: String?): String {
        var dateDuration = ""
        val now = System.currentTimeMillis()
        val dateNow = Date(now) //현재시각
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val dateCreated = dateFormat.parse(createdTime)
            val duration = dateNow.time - dateCreated.time //시간차이 mills
            dateDuration = if (duration / 1000 / 60 == 0L) {
                "방금"
            } else if (duration / 1000 / 60 <= 59) {
                (duration / 1000 / 60).toString() + "분 전"
            } else if (duration / 1000 / 60 / 60 <= 23) {
                (duration / 1000 / 60 / 60).toString() + "시간 전"
            } else if (duration / 1000 / 60 / 60 / 24 <= 29) {
                (duration / 1000 / 60 / 60 / 24).toString() + "일 전"
            } else if (duration / 1000 / 60 / 60 / 24 / 30 <= 12) {
                (duration / 1000 / 60 / 60 / 24 / 30).toString() + "개월 전"
            } else {
//                (duration / 1000 / 60 / 60 / 24 / 30 / 12).toString() + "년 전"
                SimpleDateFormat("yyyy-MM-dd").format(duration)
            }
            return dateDuration
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }
}
