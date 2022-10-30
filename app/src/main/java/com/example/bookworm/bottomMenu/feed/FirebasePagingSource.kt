package com.example.bookworm.bottomMenu.feed

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

//파이어베이스에서 데이터를 가져오는 코드(피드 전용)
class FirebasePagingSource(PAGE_SIZE:Int) :
    PagingSource<QuerySnapshot, Feed>() {

    private val queryPostsByName = FireStoreLoadModule.provideQueryLoadPostsOrderByFeedID(pageSize = PAGE_SIZE)
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
            result
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}