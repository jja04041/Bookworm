package com.example.bookworm.bottomMenu.feed.temp

import androidx.lifecycle.ComputableLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FeedViewModel : ViewModel() {
    var queryPostsByName: Query
    val PAGE_SIZE = 5

    init {
        queryPostsByName = FireStoreLoadModule.provideQueryPostsByUserID(PAGE_SIZE)
    }

    val flow = Pager(PagingConfig(pageSize = PAGE_SIZE))
    {
        FirebasePagingSource(queryPostsByName)
    }.flow.cachedIn(viewModelScope)

    fun getLastComment(feedId:String,liveData: MutableLiveData<Comment>) {
        viewModelScope.launch {
            liveData.value = FireStoreLoadModule.provideQueryCommentsLately(feedId).get().await().toObjects(Comment::class.java)[0]
        }
    }
}