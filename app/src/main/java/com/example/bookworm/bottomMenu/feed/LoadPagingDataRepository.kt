package com.example.bookworm.bottomMenu.feed

import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

//페이징 처리를 위한 레포지토리
class LoadPagingDataRepository() {
    private val PAGE_SIZE = 5
    private var queryForPaging: Query? = null
    private var lastVisibleData: DocumentSnapshot? = null
    private var currentPage: QuerySnapshot? = null

    fun setQuery(query: Query) {
        queryForPaging = query
    }


    fun reset() {
        queryForPaging = null
        currentPage = null
        lastVisibleData = null
    }

    //데이터 타입을 관리
    enum class DataType { FeedType, CommentType }

    suspend fun loadFireStoreData(type: DataType, pageSize: Int = PAGE_SIZE): Any? {
        var query = queryForPaging!!.limit(pageSize.toLong())
        currentPage = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            //현재 페이지 그리고 마지막으로 표시된 데이터 이 세가지 요소의 조화를 이뤄야 함.
            if (lastVisibleData != null)
                query.startAfter(
                        when (type) {
                            DataType.FeedType -> lastVisibleData!!.data!!["feedID"]
                            DataType.CommentType -> lastVisibleData!!.data!!["CommentID"]
                            else -> {}
                        }
                ).get().await()
            else query.get().await()
        }
        return if (currentPage != null && !currentPage!!.isEmpty) {
            lastVisibleData = currentPage!!.documents.last()
            when (type) {
                DataType.FeedType -> //피드 데이터인 경우
                    currentPage!!.toObjects(Feed::class.java)
                DataType.CommentType -> //댓글 데이터인 경우
                    currentPage!!.toObjects(Comment::class.java)
                else -> {

                }
            }
        } else {
            lastVisibleData = null
            null
        }

    }

}