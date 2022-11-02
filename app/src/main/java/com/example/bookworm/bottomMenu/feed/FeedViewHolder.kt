package com.example.bookworm.bottomMenu.feed


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.bookworm.LoadState
import com.example.bookworm.R
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.views.ProfileInfoActivity
import com.example.bookworm.bottomMenu.search.searchtest.views.BookDetailActivity
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FeedDataBinding
import com.example.bookworm.notification.MyFCMService
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class FeedViewHolder(
    private val binding: FeedDataBinding,
    val context: Context,
    val adapter: FeedAdapter
) : RecyclerView.ViewHolder(binding.root) {
    var limit = 0
    var restricted = false
    private var myFCMService: MyFCMService? = null
    val pv = ViewModelProvider(context as MainActivity, UserInfoViewModel.Factory(context)).get(
        UserInfoViewModel::class.java
    )
    private val feedViewModel =
        ViewModelProvider(context as MainActivity, FeedViewModel.Factory(context)).get(
            FeedViewModel::class.java
        )
    private var feedUserFcmtoken: String? = null
    private val mainUserLiveData = MutableLiveData<UserInfo>()

    //생성자를 만든다.
    init {
        binding.lifecycleOwner = context as MainActivity
        pv.getUser(null, mainUserLiveData, false)
        //FCMService 객체를 생성한다 .
        myFCMService = MyFCMService.getInstance()
    }

    //아이템을 세팅하는 메소드
    fun bindFeed(feed: Feed, listener: OnFeedMenuClickListener) {
        //순서
        // 1. 전달받은 데이터를 가지고, 불완전한 데이터들을 먼저 수집( 피드 작성자, 댓글 작성자)
        // 2. 받은 정보를 현재 아이템에 저장
        // 3. 아이템을 캐싱함.
        // 4. 기타 뷰 처리 진행

        //책 제목과 책 저자에 적힌 &lt, &gt 문제 해결
        feed.book!!.title = feed.book!!.title.replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&lt", "<")
            .replace("&gt", ">")
        feed.book!!.author = feed.book!!.author.replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&lt", "<")
            .replace("&gt", ">")

        binding.feedImage.apply {
            isVisible = feed.imgurl != ""
            Glide.with(context).load(feed.imgurl)
                .placeholder(CircularProgressDrawable(context).apply {
                    strokeWidth = 5f
                    centerRadius = 30f
                    start()
                })
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .error(AppCompatResources.getDrawable(context, R.drawable.bookimg_sample))
                .into(this)
        }
        binding.apply {
            //책 정보 확인 시
            llbook.setOnClickListener {
                val intent = Intent(context, BookDetailActivity::class.java)
                intent.putExtra("BookID", feed.book.itemId)
                context.startActivity(intent)
            }


            // 공유하기 버튼 눌렀을 때 (게시물 공유)
            btnShare.setOnClickListener {


            }
            tvIfModified.isVisible = feed.modified
            //좋아요 표시관리
            binding.apply {
                tvLike.apply {
                    text = feed.likeCount.toString()
                }
                btnLike.apply {
                    if(feed.isUserLiked)
                        btnLike.setUIState(LikeButton.UIState.Like(feed.likeCount), isAnim = true)
                    else
                        btnLike.setUIState(LikeButton.UIState.UnLike, isAnim = true)
                }
            }


            btnFeedMenu.setOnClickListener { view ->
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(this@FeedViewHolder, view, pos)
                }
            }
            //프로필을 눌렀을때 그 사용자의 프로필 정보 화면으로 이동
            llProfile.setOnClickListener {
                val intent = Intent(context, ProfileInfoActivity::class.java)
                intent.putExtra("userID", feed.userToken)
                context.startActivity(intent)
            }
        }

        //책 정보 확인 시


        mainUserLiveData.observe(context as MainActivity) { mainUser ->
            binding.apply {
                // 댓글이 있는 경우
                if (feed.commentsCount > 0L) {
                    //피드 내용 클릭시 이벤트
                    flFeedContent.setOnClickListener {
                        if (llLastComment.visibility != View.GONE) {
                            val intent = Intent(context, SubActivityComment::class.java)
                            feed.position = bindingAdapterPosition
                            intent.putExtra("Feed", feed)
                            intent.putExtra("NowUser", mainUser)
                            context.startActivity(intent)
                        }
                    }
                }
                //좋아요 버튼 클릭 시
                lllike.setOnClickListener { controlLike(feed, mainUser) }
                //댓글창 클릭 시
                llComments.setOnClickListener {
                    if (llLastComment.visibility != View.GONE) {
                        val intent = Intent(context, SubActivityComment::class.java)
                        feed.position = bindingAdapterPosition
                        intent.putExtra("Feed", feed)
                        intent.putExtra("NowUser", mainUser)
                        (context.supportFragmentManager.findFragmentByTag("0") as FragmentFeed).startActivityResult.launch(
                            intent
                        )
                    }
                }
                //댓글 작성
                btnWriteComment.setOnClickListener {
                    addComment(binding.feed as Feed, mainUser)
                }
                //댓글 아이콘을 눌렀을 때
                btnComment.setOnClickListener {
                    val intent = Intent(context, SubActivityComment::class.java)
                    feed.position = bindingAdapterPosition
                    intent.putExtra("Feed", feed)
                    intent.putExtra("NowUser", mainUser)
                    (context.supportFragmentManager.findFragmentByTag("0") as FragmentFeed).startActivityResult.launch(
                        intent
                    )
                }
                //뷰에 피드 반영
                this.feed = feed
                executePendingBindings()
            }
        }
    }

    //댓글 추가
    private fun addComment(feed: Feed, nowUser: UserInfo) {
        val commentText = binding.edtComment.text.toString() //댓글 내용
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //댓글 입력 후 키보드 내리기
        binding.edtComment.apply {
            imm.hideSoftInputFromWindow(windowToken, 0)
            clearFocus()
            text = null
        }
        //댓글의 내용이 없는 경우 업로드 하지 않음.
        if (commentText.replace(" ", "") != "") {
            val madeDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val comment = Comment(
                commentID = "${madeDate}_${nowUser.token}",
                contents = commentText,
                userToken = nowUser.token,
                madeDate = madeDate
            )
            feedViewModel.manageComment(comment, feed.feedID!!, true) //서버에 댓글 추가
            feedViewModel.nowCommentLoadState.observe(context as MainActivity) { state ->
                //서버에 업로드 되면 화면에 해당 내용을 반영함.
                if (state == LoadState.Done) {
                    feed.commentsCount += 1L
                    feed.comment = comment
                    feed.comment!!.duration = feedViewModel.getDateDuration(feed.comment!!.madeDate)
                    feed.comment!!.creator = nowUser
                    binding.feed = feed
                    binding.executePendingBindings() // 변경된 값을 뷰에 적용

                    //알림 푸시메시지 전송
                    myFCMService!!.sendPostToFCM(
                        context,
                        feedUserFcmtoken, "${nowUser.username}님이 댓글을 남겼습니다.\"${comment.contents}\""
                    )
                }
            }
        }
    }


    //좋아요를 관리하는 메소드
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun controlLike(feed: Feed, nowUser: UserInfo) {
        if (limit < 5) {
            limit += 1
            val backupLikeState = listOf(feed.likeCount, feed.isUserLiked) //작업 오류시 다시 되돌리기 위함.

            if (!feed.isUserLiked) {
                //현재 좋아요를 누르지 않은 상태
                feed.isUserLiked = true
                feed.likeCount += 1L
                feed.feedID?.let { nowUser.likedPost.add(it) }
            } else {
                //현재 좋아요를 누른 상태
                feed.isUserLiked = false
                feed.likeCount -= 1L
                feed.feedID?.let { nowUser.likedPost.remove(it) }
            }

            feedViewModel.manageLike(feed, nowUser)
            binding.apply {
                tvLike.apply {
                    text = "${feed.likeCount}"
                }
                btnLike.apply {
                        if (feed.isUserLiked)
                            btnLike.setUIState(LikeButton.UIState.Like(feed.likeCount), isAnim = true)
                        else
                            btnLike.setUIState(LikeButton.UIState.UnLike, isAnim = true)
                }
            }
            feedViewModel.nowLikeState.observe(context as MainActivity) { state ->
                when (state) {
                    LoadState.Done -> {
                        myFCMService!!.sendPostToFCM(
                            context,
                            feedUserFcmtoken, "${nowUser.username}님이 좋아요를 표시했습니다."
                        )
                    }
                    LoadState.Error -> {
                        Toast.makeText(
                            context,
                            "게시물의 좋아요를 처리하는 중 오류가 발생하였습니다. 다시 시도해 주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.apply {
                            tvLike.apply {
                                text = backupLikeState[0].toString()
                            }

                            btnLike.apply {
                                    if (backupLikeState[1] as Boolean)
                                        btnLike.setUIState(LikeButton.UIState.Like(feed.likeCount), isAnim = true)
                                    else
                                        btnLike.setUIState(LikeButton.UIState.UnLike, isAnim = true)
                            }
                        }
                    }
                    else -> {}
                }
            }
        } else {
            AlertDialog.Builder(context)
                .setMessage("커뮤니티 활동 보호를 위해 잠시 후에 다시 시도해주세요")
                .setPositiveButton(
                    "네"
                ) { dialog, which -> dialog.dismiss() }.show()
            if (!restricted) {
                restricted = true
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    limit = 0
                    restricted = false
                }, 10000)
            }
        }
    }


    //라벨을 동적으로 생성
    private fun setLabel(label: ArrayList<String>) {
        binding.lllabel.removeAllViews() //기존에 설정된 값을 초기화 시켜줌.
        val idx = label.indexOf("")
        for (i in 0 until idx) {
            //뷰 생성
            val tv = TextView(context)
            tv.text = label[i] //라벨에 텍스트 삽입
            tv.background =
                AppCompatResources.getDrawable(context, R.drawable.label_design) //디자인 적용
            tv.setBackgroundColor((context as MainActivity).getColor(R.color.subcolor_2)) //배경색 적용
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ) //사이즈 설정
            params.setMargins(10, 0, 10, 0) //마진 설정
            tv.layoutParams = params //설정값 뷰에 저장
            binding.lllabel.addView(tv) //레이아웃에 뷰 세팅
        }
    }


    //메달 표시 유무에 따른 세팅
    //댓글창 메달도 있기때문에 여기는 인자를 userInfo, bool 이렇게 2개 받음
    private fun setMedal(userInfo: UserInfo, bool: Boolean?) {
        if (userInfo.medalAppear!!) { //메달을 표시한다면
            if (bool == false) { //피드라면
                binding.ivMedal.visibility = View.VISIBLE
                when (userInfo.tier!!.toInt()) {
//                    1 -> binding.ivMedal.setImageResource(R.drawable.medal_bronze)
                    2 -> binding.ivMedal.setImageResource(R.drawable.medal_silver)
                    3 -> binding.ivMedal.setImageResource(R.drawable.medal_gold)
                    4 -> {}
                    5 -> {}
                    else -> binding.ivMedal.setImageResource(0)
                }
            } else { //댓글이라면
                binding.ivCommentMedal.visibility = View.VISIBLE
                when (userInfo.tier!!.toInt()) {
//                    1 -> binding.ivCommentMedal.setImageResource(R.drawable.medal_bronze)
                    2 -> binding.ivCommentMedal.setImageResource(R.drawable.medal_silver)
                    3 -> binding.ivCommentMedal.setImageResource(R.drawable.medal_gold)
                    4 -> {}
                    5 -> {}
                    else -> binding.ivCommentMedal.setImageResource(0)
                }
            }

        } else { //메달을 표시하지 않을거라면

            if (bool == false) { //피드라면
                binding.ivMedal.visibility = View.GONE
                binding.ivMedal.setImageResource(0)
            } else { //댓글이라면
                binding.ivCommentMedal.visibility = View.GONE
                binding.ivCommentMedal.setImageResource(0)
            }
        }
    }

}