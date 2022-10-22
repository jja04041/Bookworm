package com.example.bookworm.bottomMenu.feed

import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

//페이징 처리를 위한 레포지토리
class FeedDataRepository() {
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

    suspend fun deleteFeed(feed: Feed) {
        FireStoreLoadModule.provideQueryPathToFeedCollection()
                .document(feed.feedID!!).delete().await()
    }

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
            lastVisibleData = currentPage!!.documents.last() //마지막 데이터를 담아놔서 그 이후의 데이터를 가져올 수 있게 함
            when (type) {
                DataType.FeedType -> //피드 데이터인 경우
                {
                    try {
                        if (pageSize == 1) {
                            currentPage!!.toObjects(Feed::class.java)[0]
                        } else {
                            currentPage!!.toObjects(Feed::class.java)
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        null
                    }
                }
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

    //댓글 작성 관리
    suspend fun manageComment(feedId: String, comment: Comment, isAdd: Boolean): Transaction {
        val feedRef = FireStoreLoadModule.provideQueryPostByFeedID(feedId)
        val commentRef = feedRef.collection("comments").document(comment.commentID!!)
        return FireStoreLoadModule.provideFirebaseInstance().runTransaction { transaction ->
            transaction.apply {
                if (isAdd) set(commentRef, comment)
                        .update(feedRef, "commentsCount", FieldValue
                                .increment(1L))
                else
                delete(commentRef)
                        .update(feedRef, "commentsCount", FieldValue
                                .increment(-1L))
            }

        }.await()
    }

}