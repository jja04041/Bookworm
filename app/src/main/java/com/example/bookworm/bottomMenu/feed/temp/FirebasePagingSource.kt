package com.example.bookworm.bottomMenu.feed.temp

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

//파이어베이스에서 데이터를 가져오는 코드(피드 전용)
class FirebasePagingSource(private val queryPostsByName: Query) :
    PagingSource<QuerySnapshot, Feed>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Feed>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Feed> {
        return try {
            val currentPage = params.key ?: queryPostsByName.get().await()
            val lastVisibleFeed = currentPage.documents[currentPage.size() - 1] //마지막으로 가져온 게시물
            val nextPage = queryPostsByName.startAfter(lastVisibleFeed).get().await()
            val result = LoadResult.Page(
                data = currentPage.toObjects(Feed::class.java),
                prevKey = null,
                nextKey = nextPage
            )
            Log.d("불러온 데이터",result.data.size.toString())
            result
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}