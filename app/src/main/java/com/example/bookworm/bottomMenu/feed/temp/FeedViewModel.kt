package com.example.bookworm.bottomMenu.feed.temp

import androidx.lifecycle.*
import androidx.paging.*
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FeedViewModel : ViewModel() {
    private val PAGE_SIZE = 5
    private val queryPostsByName = FireStoreLoadModule.provideQueryPostsByUserID(PAGE_SIZE)
    private val modificationEvents = MutableStateFlow<List<FeedViewEvents>>(emptyList())


    sealed class FeedViewEvents {
        data class Edit(val entity: Feed) : FeedViewEvents()
        data class Remove(val entity: Feed) : FeedViewEvents()
    }

    private val flow = Pager(PagingConfig(pageSize = PAGE_SIZE))
    {
        FirebasePagingSource(queryPostsByName)
    }.flow.cachedIn(viewModelScope)
            .combine(modificationEvents) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEvents(acc, event)
                }
            }
    
    //현재 상태의 피드를 유지하는 것
    val pagingDataViewStates= flow.asLiveData()


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


}