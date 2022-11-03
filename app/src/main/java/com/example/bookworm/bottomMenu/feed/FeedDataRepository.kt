package com.example.bookworm.bottomMenu.feed

import android.util.Log
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

//게시물 관련 처리를 위한 레포지토리
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

    //게시물을 삭제하는 과정을 처리한다.
    suspend fun deletePostProcess(feed: Feed) {
        val feedRef = FireStoreLoadModule.provideQueryPathToFeedCollection()
            .document(feed.feedID!!) //게시물의 경로
        feedRef.delete().await()
    }

    //Firestore에 있는 정보를 가져오는 함수
    suspend fun loadFireStoreData(type: DataType, pageSize: Int = PAGE_SIZE): Any? {
        val query = queryForPaging!!.limit(pageSize.toLong())
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

    //한 개의 게시물의 정보만 가져오기 -> 게시물 수정시 최신의 데이터를 가져오기 위함.
    suspend fun loadOnePost(feedId: String) =
        FireStoreLoadModule.provideQueryPostByFeedID(feedId = feedId).get().await()

    //댓글 작성 관리
    suspend fun manageComment(feedId: String, comment: Comment, isAdd: Boolean): Transaction {
        val feedRef = FireStoreLoadModule.provideQueryPostByFeedID(feedId)
        val commentRef = feedRef.collection("comments").document(comment.commentID!!)
        return FireStoreLoadModule.provideFirebaseInstance().runTransaction { transaction ->
            transaction.apply {
                if (isAdd) set(commentRef, comment)
                    .update(
                        feedRef, "commentsCount", FieldValue
                            .increment(1L)
                    )
                else
                    delete(commentRef)
                        .update(
                            feedRef, "commentsCount", FieldValue
                                .increment(-1L)
                        )
            }

        }.await()
    }

    //좋아요 관리
    suspend fun manageLike(feedId: String, nowUserToken: String, isLiked: Boolean): Transaction? {
        val feedRef = FireStoreLoadModule.provideQueryPostByFeedID(feedId)
        val nowUserRef = FireStoreLoadModule.provideUserByUserToken(nowUserToken)
        return FireStoreLoadModule.provideFirebaseInstance()
            .runTransaction { transaction ->
                transaction.apply {
                    val likeCount = transaction.get(feedRef)["likeCount"] as Long //현재 상태의 좋아요 수 확인
                    if (likeCount < 0) update(feedRef, "likeCount", 0L)
                    update(
                        feedRef,
                        "likeCount",
                        FieldValue.increment(if (isLiked) 1L else -1L)
                    ).update(
                            nowUserRef,
                            "likedPost",
                            if (isLiked) FieldValue.arrayUnion(feedId) else FieldValue.arrayRemove(
                                feedId
                            )
                        )
                }
            }.await()
    }

    //게시물 업로드
    fun uploadPost(feed: Feed) =
        FireStoreLoadModule.provideQueryUploadPost(feed)

    //게시물 수정
    fun modifyPost(feed: Feed) =
        FireStoreLoadModule.provideQueryModifyPost(feed)

}