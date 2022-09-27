package com.example.bookworm.bottomMenu.feed


import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.example.bookworm.LoadState
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.core.userdata.UserInfo
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FeedViewModel(context: Context) : ViewModel() {
    private val loadPagingRepo = LoadPagingDataRepository()
    private val userInfoViewModel = ViewModelProvider(
            when (context) {
                is MainActivity -> context
                is SubActivityComment -> context
                else -> context as SearchMainActivity
            },
            UserInfoViewModel.Factory(context))[UserInfoViewModel::class.java]
    var postsData: List<Feed>? = emptyList() //불러오는 피드 데이터 목록을 저장
    var commentsData: List<Comment>? = emptyList() //불러오는 댓글 데이터 목록을 저장
    val nowFeedLoadState = MutableLiveData<LoadState>() //현재 피드 로드 상태를 추적하는 LiveData
    val nowCommentLoadState = MutableLiveData<LoadState>() //현재 댓글 로드 상태를 추적하는 LiveData
    val nowFeedUploadState = MutableLiveData<LoadState>() //현재 피드 업로드 상태를 추적하는 LiveData
    val nowLikeState = MutableLiveData<LoadState>()



    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedViewModel(context) as T
        }
    }

    //피드를 업로드할 때 사용하는 함수
    //선택한 이미지 비트맵을 URI로 변경 후, 서버에 업로드 => 파이어베이스에 피드 등록
    fun uploadFeed(feed: Feed, imgBitmap: Bitmap?, imageProcess: ImageProcessing) {
        nowFeedUploadState.value = LoadState.Loading

        val imageJob =  //이미지 처리 작업
                viewModelScope.async {
                    if (imgBitmap != null) {
                        val imageName = "feed_ ${feed.FeedID}.jpg"
                        val imgUrl = imageProcess.uploadImg(imgBitmap, imageName)
                        imgUrl //null값인 경우, 업로드 실패, 아닌 경우 제대로 처리 된 것
                    } else ""
                }
        viewModelScope.launch {
            val url = imageJob.await()
            if (url != null) {
                feed.imgurl = url
                //파이어스토어에 피드 업로드
                FireStoreLoadModule.provideQueryUploadPost(feed)
                        .addOnSuccessListener {    //정상적으로 업로드 되는 경우
                            //유저의 정보 업데이트
                            feed.Creator!!.apply {
//                                setGenre(feed.book!!.categoryname, context) //장르 설정
                                CoroutineScope(Dispatchers.IO).launch {
                                    userInfoViewModel.getBookWorm(token).join().apply {
                                        var data = userInfoViewModel.bwdata.value
                                        data!!.readCount++
                                        userInfoViewModel.updateBw(token, data)
                                    }
                                    userInfoViewModel.updateUser(this@apply)
                                    nowFeedUploadState.value = LoadState.Done
                                }
                            }
                            // 업적 업데이트


                        }.addOnFailureListener { nowFeedUploadState.value = LoadState.Error }
            } else nowFeedUploadState.value = LoadState.Error
        }
    }

    //댓글을 추가/삭제하는 함수
    fun manageComment(comment: Comment, feedId: String, isAdd: Boolean = true) {
        val feedRef = FireStoreLoadModule.provideQueryPostByFeedID(feedId)
        val commentRef = feedRef.collection("comments").document(comment.commentID!!)

        viewModelScope.launch {
            nowCommentLoadState.value = LoadState.Loading
            FireStoreLoadModule.provideFirebaseInstance().apply {
                runTransaction { transaction ->
                    transaction.set(commentRef, comment)
                            .update(feedRef, "commentsCount", FieldValue
                                    .increment(if (isAdd) 1L else -1L))
                }.addOnSuccessListener {
                    nowCommentLoadState.value = LoadState.Done
                }.addOnFailureListener {
                    nowCommentLoadState.value = LoadState.Error
                }
            }
        }
    }

    //좋아요를 관리하는 함수
    fun manageLike(feedId: String, nowUser: UserInfo, isLiked: Boolean) {
        val feedRef = FireStoreLoadModule.provideQueryPostByFeedID(feedId)
        val nowUserRef = FireStoreLoadModule.provideUserByUserToken(nowUser.token)
        nowLikeState.value = LoadState.Loading
        viewModelScope.launch {
            FireStoreLoadModule.provideFirebaseInstance() // Base For Query
                    .runTransaction { transaction ->
                        transaction.apply {
                            update(feedRef, "likeCount", if (isLiked) FieldValue.increment(1L) else FieldValue.increment(-1L))
                                    .update(nowUserRef, "UserInfo.likedPost",
                                            if (isLiked) FieldValue.arrayUnion(feedId) else FieldValue.arrayRemove(feedId))
                        }
                    }.addOnSuccessListener {
                        viewModelScope.launch {
                            val user = userInfoViewModel.suspendGetUser(nowUser.token)
                            userInfoViewModel.updateUser(user!!)
                            //업적 처리
//                        userInfoViewModel.getBookWorm(nowUser.token)
//                        userInfoViewModel.bwdata.value
                            nowLikeState.value = LoadState.Done
                        }
                    }.addOnFailureListener {
                        nowLikeState.value = LoadState.Error
                    }
        }
    }

    //피드를 불러오는 함수
    fun loadPosts(isRefresh: Boolean = false) {

        nowFeedLoadState.value = LoadState.Loading //로딩 상태로 변경
        viewModelScope.launch {
            //새로고침 또는 첫 로드 시에는 변수 리셋과 쿼리 재장착
            if (isRefresh) {
                loadPagingRepo.reset()
                loadPagingRepo.setQuery(FireStoreLoadModule.provideQueryLoadPostsOrderByFeedID()) //쿼리 세팅
            }
            var loadedData = loadPagingRepo.loadFireStoreData(LoadPagingDataRepository.DataType.FeedType)
            if (loadedData != null) {
                postsData =
                        (loadedData as MutableList<Feed>).map { feed: Feed ->
                            return@map withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                                val tempUserInfo = userInfoViewModel.suspendGetUser(null)?.apply {
                                    feed.isUserLiked = likedPost.contains(feed.FeedID)
                                }
                                feed.Creator = userInfoViewModel.suspendGetUser(feed.UserToken!!)
                                feed.isUserPost = (tempUserInfo!!.token == feed.UserToken)
                                feed.duration = getDateDuration(feed.date)
                                if (feed.commentsCount > 0L) {
                                    feed.comment = FireStoreLoadModule.provideQueryCommentsLately(feed.FeedID!!)
                                            .get().await()
                                            .toObjects(Comment::class.java)[0] //형변환을 자동으로 해줌.
                                    feed.comment!!.creator = userInfoViewModel.suspendGetUser(feed.comment!!.userToken)!!
                                    feed.comment!!.duration = getDateDuration(feed.comment!!.madeDate)
                                }
                                return@withContext feed
                            }
                        }
            } else postsData = null

            nowFeedLoadState.value = LoadState.Done
        }
    }

    fun loadComment(feedId: String, isRefresh: Boolean) {
        nowCommentLoadState.value = LoadState.Loading
        viewModelScope.launch {
            if (isRefresh) {
                loadPagingRepo.reset()
                loadPagingRepo.setQuery(FireStoreLoadModule.provideQueryCommentsInFeedByFeedID(feedId))
            }
            val loadedData = loadPagingRepo.loadFireStoreData(LoadPagingDataRepository.DataType.CommentType)
            commentsData = if (loadedData != null) {
                (loadedData as MutableList<Comment>).map { comment: Comment ->
                    return@map withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                        comment.creator = userInfoViewModel.suspendGetUser(comment.userToken)
                        return@withContext comment
                    }
                }
            } else null
            nowCommentLoadState.value = LoadState.Done
        }
    }

    //단일 게시물을 불러올 때
    fun loadPost() {

    }

    //시간차 구하기 n분 전, n시간 전 등등
    fun getDateDuration(createdTime: String?): String {
        var dateDuration = ""
        val now = System.currentTimeMillis()
        val dateNow = Date(now) //현재시각
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault())
        try {
            val dateCreated = createdTime?.let { dateFormat.parse(it) }
            val duration = dateNow.time - dateCreated!!.time //시간차이 mills
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
                SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(duration)
            }
            return dateDuration
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }
}