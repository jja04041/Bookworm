package com.example.bookworm.bottomMenu.feed.comments

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.LoadState
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.challenge.board.subactivity_challenge_board.context
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.feed.FeedViewModel
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.LayoutCommentItemBinding
import com.example.bookworm.databinding.SubactivityCommentBinding
import com.example.bookworm.notification.MyFCMService
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

//메커니즘 순서
//1. 이전 화면(fragmentFeed)에서 넘어온 Feed데이터를 가지고 화면애 세팅해준다.
//2. 댓글을 불러와야 하는데, Paging3를 이용하여 데이터를 가지고 온다.
//3. 가지고 온 댓글을 리사이클러뷰에 뿌려준다.
//4. 사용자가 댓글을 단 경우, 파이어스토어 서버에 해당 댓글을 저장하고, (1) 화면을 새로고침한다. or (2) 맨 위에 아이템을 세팅한다.


//댓글 삭제 및 수정 부분을 ItemTouchHelper를 이용하여 작업한다.

//댓글, 상세 화면
class SubActivityComment : AppCompatActivity() {
    private val binding by lazy {
        SubactivityCommentBinding.inflate(layoutInflater)
    }

    private val userInfoViewModel by lazy {
        ViewModelProvider(this, UserInfoViewModel.Factory(this))[UserInfoViewModel::class.java]
    }
    private val feedViewModel by lazy {
        ViewModelProvider(this, FeedViewModel.Factory(this))[FeedViewModel::class.java]
    }
    private var isDataEnd = false
    private val myFCMService = MyFCMService.getInstance()
    private val feedItem by lazy<Feed> {
        intent.getParcelableExtra("Feed")!!
    }
    val nowUser by lazy {
        intent.getParcelableExtra<UserInfo>("NowUser")
    }
    private val commentAdapter = CommentsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            setContentView(root)
            tvTopName.text = "${feedItem.creatorInfo!!.username}님의 게시물"
            mRecyclerView.isNestedScrollingEnabled = false
            btnWriteComment.setOnClickListener {
                //댓글 추가
                addComment()
            }
            btnBefore.setOnClickListener { finish() }
            setRecyclerView()
            loadCommentData(true)
        }

    }

    //피드를 불러오는 어댑터 장착


    //댓글 추가 메소드 구현
    private fun addComment() {
        val commentText = binding.edtComment.text.toString() //댓글 내용
        binding.apply {
            if (commentText != "") {//내용이 비어있지 않을 때만 이 메소드가 작동하도록 함
                edtComment.apply {

                    val madeDate = LocalDateTime.now()
                        .format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
                                .withLocale(Locale.KOREA)
                                .withZone(ZoneId.of("Asia/Seoul"))
                        )
                    val comment = Comment(
                        commentID = "${madeDate}_${nowUser!!.token}",
                        contents = commentText,
                        userToken = nowUser!!.token,
                        madeDate = madeDate
                    )
                    feedViewModel.manageComment(comment, feedItem.feedID!!, true) //서버에 댓글 추가
                    //게시물 작성자에게 댓글이 달렸다는 알림을 보냄
                    myFCMService.sendPostToFCM(
                            this@SubActivityComment, feedItem.creatorInfo!!.fCMtoken,
                            "${nowUser!!.username}님이 댓글을 남겼습니다. \"${text}\" "
                    )

                    //키보드 내리기
                    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(windowToken, 0)
                    clearFocus()
                    text = null

                    feedViewModel.nowCommentLoadState.observe(context as SubActivityComment) { state ->
                        if (state == LoadState.Done) {
                            comment.duration = feedViewModel.getDateDuration(comment!!.madeDate)
                            comment.creator = nowUser!!
                            commentAdapter.currentList.toMutableList().apply {
                                add(1, comment)
                                commentAdapter.submitList(this)
                            }
                        }

                        binding.mRecyclerView.apply {
                            smoothScrollToPosition(0) //맨 위 아이템으로 포커스를 이동 (본인 댓글 확인을 위해)
                        }
                    }
                }
            }
        }
    }

    //페이지 새로고침 시 사용하는 메소드
    fun pageRefresh() {
        isDataEnd = false
        loadCommentData(true)
        commentAdapter.submitList(emptyList())
    }


    //데이터를 가져오는 메소드
    fun loadCommentData(isRefreshing: Boolean) {
        if (!isDataEnd) {
            feedViewModel.loadComment(feedItem.feedID!!, isRefreshing)
            feedViewModel.nowCommentLoadState.observe(this) { nowState ->
                //데이터 로딩이 다 되었다면
                if (nowState == LoadState.Done) {
                    val current = commentAdapter.currentList.toMutableList() //기존에 가지고 있던 아이템 목록
                    if (isRefreshing) {
                        current.clear()
                        binding.swiperefresh.isRefreshing = false //새로고침인 경우, 데이터가 다 로딩 된 후 새로고침 표시 없애기
                        current.add(0, feedItem) //피드 데이터를 가져온다.
                    }
                    //만약 현재 목록이 비어있지 않고, 마지막 아이템이 로딩 아이템 이라면 마지막 아이템을 제거
                    if (current.size > 1 && (current.last() as Comment).commentID == "")
                        current.removeLast()
                    //데이터의 끝에 다다르지 않았다면, 현재 목록에 불러온 아이템을 추가한다.
                    val loadedData = feedViewModel.commentsData
                    if (loadedData != null && !current.containsAll(loadedData)) {
                        current.addAll(loadedData)
                        if (loadedData.size == 10)
                            current.add(Comment())
                        else
                            isDataEnd =true
                    }
                    //변경된 리스트를 어댑터에 반영
                    commentAdapter.submitList(current)
                }
            }
        }
    }

    //리사이클러뷰를 위한 재료 세팅
    private fun setRecyclerView() {
        binding.apply {
            mRecyclerView.adapter = commentAdapter //어댑터 세팅
            mRecyclerView.itemAnimator = null //리사이클러뷰 애니메이션 제거
            mRecyclerView.isNestedScrollingEnabled = false
            mRecyclerView.setHasFixedSize(true)
            swiperefresh.setOnRefreshListener {
                pageRefresh()
            } //스와이프하여 새로고침
            with(mRecyclerView) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        val lastVisibleItemPosition =
                                layoutManager!!.findLastCompletelyVisibleItemPosition()
                        if ((lastVisibleItemPosition
                                        == commentAdapter.currentList.lastIndex) && lastVisibleItemPosition > 0)
                            loadCommentData(false)
                    }
                })
            }
        }
    }
}