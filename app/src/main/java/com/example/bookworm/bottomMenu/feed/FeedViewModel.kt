package com.example.bookworm.bottomMenu.feed


import android.accounts.NetworkErrorException
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookworm.LoadState
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.search.searchtest.views.BookDetailActivity
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
import com.example.bookworm.core.dataprocessing.image.ImageProcessing
import com.example.bookworm.core.userdata.UserInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
class FeedViewModel(val context: Context) : ViewModel() {
    private val dataRepository = FeedDataRepository()
    private val userInfoViewModel = ViewModelProvider(
        when (context) {
            is MainActivity -> context
            is SubActivityComment -> context
            is SubActivityCreatePost -> context
            is SubActivityModifyPost -> context
            is BookDetailActivity -> context //책 리뷰 받기 위해
            else -> context as SearchMainActivity
        },
        UserInfoViewModel.Factory(context)
    )[UserInfoViewModel::class.java]
    var postsData: List<Feed>? = emptyList() //불러오는 피드 데이터 목록을 저장
    var commentsData: List<Comment>? = emptyList() //불러오는 댓글 데이터 목록을 저장
    val nowFeedLoadState = MutableLiveData<LoadState>() //현재 피드 로드 상태를 추적하는 LiveData
    val nowCommentLoadState = MutableLiveData<LoadState>() //현재 댓글 로드 상태를 추적하는 LiveData
    val nowFeedUploadState = MutableLiveData<LoadState>() //현재 피드 업로드 상태를 추적하는 LiveData
    val nowLikeState = MutableLiveData<LoadState>()

    //이미지 처리 작업
    val imageJob = { imgBitmap: Bitmap?, imageProcess: ImageProcessing, feed: Feed ->
        viewModelScope.async {
            if (imgBitmap != null) {
                val imageName = "feed_ ${feed.feedID}.jpg"
                val imgUrl = imageProcess.uploadImg(imgBitmap, imageName)
                imgUrl //null값인 경우, 업로드 실패, 아닌 경우 제대로 처리 된 것
            } else ""
        }
    }

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedViewModel(context) as T
        }
    }

    fun deletePost(feed: Feed, liveData: MutableLiveData<LoadState>) {
        liveData.value = LoadState.Loading
        viewModelScope.launch {
            try {
                dataRepository.deletePostProcess(feed)
                updateUserInfo(feed, true)
                liveData.value = LoadState.Done
            } catch (e: Exception) {
                liveData.value = LoadState.Error
            }
        }
    }

    fun getFeedImgUrl(
        feed: Feed,
        bitmap: Bitmap?,
        imageProcess: ImageProcessing,
        liveData: MutableLiveData<String>
    ) {
        liveData.value = null
        viewModelScope.launch {
            if (bitmap != null) {
                liveData.value = imageJob(bitmap, imageProcess, feed).await()
            } else liveData.value = ""
        }
    }

    //피드를 업로드할 때 사용하는 함수
    //선택한 이미지 비트맵을 URI로 변경 후, 서버에 업로드 => 파이어베이스에 피드 등록
    fun uploadPost(feed: Feed, imgBitmap: Bitmap? = null, imageProcess: ImageProcessing) {
        nowFeedUploadState.value = LoadState.Loading
        viewModelScope.launch {
            val url = imageJob(imgBitmap, imageProcess, feed).await()
            if (url != null) {
                if (!feed.modified) feed.imgurl = url
                //파이어스토어에 게시물 업로드
                try {
                    //수정된 게시물인 경우
                    if (feed.modified) {
                        dataRepository.modifyPost(feed).await()
                        nowFeedUploadState.value = LoadState.Done
                    }
                    //새로 생성된 게시물인 경우
                    else {
                        dataRepository.uploadPost(feed).await()
                        updateUserInfo(feed)
                        nowFeedUploadState.value = LoadState.Done
                    }
                } catch (e: Exception) {
                    nowFeedLoadState.value = LoadState.Error
                }
            }
        }
    }

    //댓글을 추가/삭제하는 함수
    fun manageComment(
        comment: Comment,
        feedId: String,
        isAdd: Boolean = true,
        state: MutableLiveData<LoadState>? = null
    ) {
        viewModelScope.launch {
            if (state != null) state.value = LoadState.Loading
            else nowCommentLoadState.value = LoadState.Loading
            try {
                dataRepository.manageComment(feedId, comment, isAdd)
                if (state != null) state.value = LoadState.Done
                else nowCommentLoadState.value = LoadState.Done
            } catch (e: NetworkErrorException) {
                Toast.makeText(context, "네트워크 오류입니다 다시 시도해 주세요. ", Toast.LENGTH_SHORT).show()
                if (state != null) state.value = LoadState.Error
                else nowCommentLoadState.value = LoadState.Error
            } catch (e: Exception) {
                if (state != null) state.value = LoadState.Error
                else nowCommentLoadState.value = LoadState.Error
            }
        }
    }

    //좋아요를 관리하는 함수
    fun manageLike(feed: Feed, nowUser: UserInfo) {
        nowLikeState.value = LoadState.Loading
        viewModelScope.launch {
            try {
                dataRepository.manageLike(feed.feedID!!, nowUser.token, feed.isUserLiked) ?: throw Exception()
                val user = userInfoViewModel.suspendGetUser(nowUser.token)!!
                userInfoViewModel.updateUser(user)
                nowLikeState.value = LoadState.Done
            } catch (e: Exception) {
                //좋아요 처리가 불가한 경우
                nowLikeState.value = LoadState.Error
            }
        }
    }

    //유저의 정보 업데이트 (게시물 업로드시)
    private fun updateUserInfo(feed: Feed, isDelete: Boolean = false) {
        feed.creatorInfo.apply {
            // 리뷰 업로드 시
            if (!isDelete) {
                userInfoViewModel.setGenre(feed.book.categoryName, this) //장르 업데이트
                userInfoViewModel.getBookWorm(token).onJoin
                val data = userInfoViewModel.bwdata.value
                data!!.readCount++
                userInfoViewModel.updateBw(token, data)
                userInfoViewModel.updateUser(this)
            } //리뷰 삭제 시
            else {
                userInfoViewModel.getBookWorm(token).onJoin
                val data = userInfoViewModel.bwdata.value
                data!!.readCount--
                userInfoViewModel.updateBw(token, data)
                userInfoViewModel.updateUser(this)
            }
        }
    }

    //피드를 불러오는 함수
    fun loadPosts(isRefresh: Boolean = false) {

        nowFeedLoadState.value = LoadState.Loading //로딩 상태로 변경
        viewModelScope.launch {
            //새로고침 또는 첫 로드 시에는 변수 리셋과 쿼리 재장착
            if (isRefresh) {
                dataRepository.reset()
//                loadPagingRepo.setQuery(FireStoreLoadModule.provideQueryLoadPostsOrderByFeedID().whereIn("book.categoryname", listOf("국내도서>컴퓨터/모바일>컴퓨터 공학>자료구조/알고리즘","사회","국내도서>소설/시/희곡>역사소설>한국 역사소설"))) //쿼리 세팅
                dataRepository.setQuery(FireStoreLoadModule.provideQueryLoadPostsOrderByFeedID()) //쿼리 세팅
            }
//            else {
//                loadPagingRepo.reset()
//                loadPagingRepo.setQuery(FireStoreLoadModule.provideQueryLoadPostsOrderByFeedID())
//            }
            val loadedData = dataRepository.loadFireStoreData(FeedDataRepository.DataType.FeedType)
            postsData = if (loadedData != null) {
                (loadedData as MutableList<Feed>).map { feed: Feed ->
                    addFeedData(feed)
                }
            } else null

            nowFeedLoadState.value = LoadState.Done
        }
    }


    fun loadComment(feedId: String, isRefresh: Boolean) {
        nowCommentLoadState.value = LoadState.Loading
        viewModelScope.launch {
            if (isRefresh) {
                dataRepository.reset()
                dataRepository.setQuery(
                    FireStoreLoadModule.provideQueryCommentsInFeedByFeedID(
                        feedId
                    )
                )
            }
            val loadedData =
                dataRepository.loadFireStoreData(FeedDataRepository.DataType.CommentType, 10)
            commentsData = if (loadedData != null) {
                (loadedData as MutableList<Comment>).map { comment: Comment ->
                    return@map addCommentData(comment)
                }
            } else null
            nowCommentLoadState.value = LoadState.Done
        }
    }

    //단일 게시물을 불러올 때
    fun loadPost(liveData: MutableLiveData<Feed>, feedId: String? = null) {
        liveData.value = null
        viewModelScope.launch {
            val loadedData =
                if (feedId == null) dataRepository.loadFireStoreData(
                    FeedDataRepository.DataType.FeedType,
                    pageSize = 1
                )
                else dataRepository.loadOnePost(feedId).toObject(Feed::class.java)
            liveData.value = addFeedData(loadedData as Feed)
        }
    }

    //시간차 구하기 n분 전, n시간 전 등등
    fun getDateDuration(createdTime: String?): String {
        var dateDuration = ""
        val now = System.currentTimeMillis()
        val dateNow = Date(now) //현재시각
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
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
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(duration)
            }
            return dateDuration
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    //넘겨준 정보에 필요한 정보를 담아서 보내주는 converter
    private suspend fun addFeedData(feed: Feed) =
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val tempUserInfo = userInfoViewModel.suspendGetUser(null)?.apply {
                feed.isUserLiked = likedPost.contains(feed.feedID)
            }
            feed.creatorInfo = userInfoViewModel.suspendGetUser(feed.userToken)!!
            feed.isUserPost = (tempUserInfo!!.token == feed.userToken)
            feed.duration = getDateDuration(feed.date)
            if (feed.commentsCount > 0L) {
                feed.comment = FireStoreLoadModule.provideQueryCommentsLately(feed.feedID!!)
                    .get().await()
                    .toObjects(Comment::class.java)[0] //형변환을 자동으로 해줌.
                feed.comment!!.creator =
                    userInfoViewModel.suspendGetUser(feed.comment!!.userToken)!!
                feed.comment!!.duration = getDateDuration(feed.comment!!.madeDate)
            }
            return@withContext feed
        }

    private suspend fun addCommentData(comment: Comment) =
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val tempUserInfo = userInfoViewModel.suspendGetUser(null)
            comment.creator = userInfoViewModel.suspendGetUser(comment.userToken)!!
            comment.isUserComment = (tempUserInfo!!.token == comment.userToken)
            return@withContext comment
        }
}