package com.example.bookworm.bottomMenu.feed.temp


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.Feed.CustomPopup
import com.example.bookworm.R
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.comments.Comment
import com.example.bookworm.bottomMenu.feed.comments.CommentsCounter
import com.example.bookworm.bottomMenu.feed.comments.subactivity_comment
import com.example.bookworm.bottomMenu.feed.likeCounter
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.profile.views.ProfileInfoActivity
import com.example.bookworm.bottomMenu.search.subactivity.search_fragment_subActivity_result
import com.example.bookworm.core.internet.FBModule
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FeedDataBinding
import com.example.bookworm.notification.MyFCMService
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


//데이터 바인딩을 위한 어댑터
object bindingAdapter {
    @JvmStatic
    @BindingAdapter("app:imgUrl", "app:placeholder")
    fun setProfileImage(v: ImageView, url: String, placeholder: Drawable) =
            Glide.with(v.context)
                    .load(url)
                    .error(placeholder)
                    .circleCrop()
                    .into(v)

    @SuppressLint("ResourceType")
    @JvmStatic
    @BindingAdapter("app:feedImgUrl")
    fun setFeedImage(v: ImageView, url: String) {
        Glide.with(v.context).load(url).error(Color.TRANSPARENT).into(v)
    }

}

class FeedViewHolder(private val binding: FeedDataBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
    var nowUser: UserInfo = UserInfo()
    var strings: ArrayList<String>? = null
    var limit = 0
    var restricted = false
    var fbModule = FBModule(context)
    private var myFCMService: MyFCMService? = null
    val pv = ViewModelProvider(context as MainActivity, UserInfoViewModel.Factory(context)).get(
            UserInfoViewModel::class.java
    )
    val fv = ViewModelProvider(context as MainActivity).get(
            FeedViewModel::class.java
    )
    private val nowUserInfo: MutableLiveData<UserInfo> = MutableLiveData()
    private val feedUserInfo: MutableLiveData<UserInfo> = MutableLiveData()
    private val commentUserInfo: MutableLiveData<UserInfo> = MutableLiveData()
    private var feedUserFcmtoken: String? = null


    //생성자를 만든다.
    init {
        //현재 사용자의 데이터를 가져온다.
        pv.getUser(null, nowUserInfo)

        nowUserInfo.observe(context as MainActivity) {
            nowUser = it
        }
        //FCMService 객체를 생성한다 .
        myFCMService = MyFCMService.getInstance()
    }


    //아이템을 세팅하는 메소드
    fun bindFeed(feed: Feed) {
        //순서
        // 1. 전달받은 데이터를 가지고, 불완전한 데이터들을 먼저 수집( 피드 작성자, 댓글 작성자)
        // 2. 받은 정보를 현재 아이템에 저장
        // 3. 아이템을 캐싱함.
        // 4. 기타 뷰 처리 진행

        //최상단 댓글 라이브데이터
        var lastCommentLiveData = MutableLiveData<Comment>()

        //책 제목과 책 저자에 적힌 &lt, &gt 문제 해결
        feed.book!!.title = feed.book!!.title.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&lt", "<")
                .replace("&gt", ">")
        feed.book!!.author = feed.book!!.author.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&lt", "<")
                .replace("&gt", ">")


        pv.getUser(feed.UserToken, feedUserInfo) //피드 작성자의 데이터를 가져온다
        //감시
        feedUserInfo.observe(context as MainActivity) { creator ->
            feed.Creator = creator

            //피드 내용 채우기

            //피드 작성 시간
            feed.duration = getDateDuration(feed.date)

            // 댓글이 있는 경우
            if (feed.commentsCount > 0L) {
                fv.getLastComment(feed.FeedID!!, lastCommentLiveData)
                lastCommentLiveData.observe(context) { comment ->
                    binding.feed!!.comment = comment
                    binding.feed!!.comment!!.duration = getDateDuration(feed.comment!!.madeDate)
                    pv.getUser(comment.userToken, commentUserInfo)//최상단 댓글 작성자의 데이터를 가져옴
                    commentUserInfo.observe(context) {
                        feed.comment!!.creator = it
                        binding.feed = feed
                        binding.executePendingBindings()
                    }
                }
                //피드 내용 클릭시 이벤트
                binding.flFeedContent.setOnClickListener{
                    if(binding.llLastComment.visibility!=View.GONE){
                        val intent = Intent(context, subactivity_comment::class.java)
                        intent.putExtra("item",feed)
                        intent.putExtra("position", bindingAdapterPosition)
                        context.startActivity(intent)
                    }
                }

            }

            //사용자가 좋아하는 피드인지 확인하는 값 세팅
            feed.isUserLiked = try {
                nowUser.likedPost!!.contains(feed.FeedID)
            } catch (e: java.lang.NullPointerException) {
                false
            }
            //라벨표시
            if (feed.label!![0] != "") setLabel(feed.label!!)
            else binding.lllabel.visibility = View.GONE
            //리스너 부착
            //책 정보 확인 시
            binding.llbook.setOnClickListener {
                val intent = Intent(context, search_fragment_subActivity_result::class.java)
                intent.putExtra("itemid", feed.book!!.itemId)
                intent.putExtra("data", feed.book!!)
                context.startActivity(intent)
            }
            //댓글창을 클릭했을때
            binding.llComments.setOnClickListener {
                if (binding.llLastComment.visibility != View.GONE) {
                    val intent = Intent(context, subactivity_comment::class.java)
                    feed.position = bindingAdapterPosition
                    intent.putExtra("feed", feed)
                    context.startActivity(intent)
                }
            }
            //댓글 빠르게 달기
            binding.btnWriteComment.setOnClickListener {
                var item = binding.feed
                val userComment = binding.edtComment.text.toString() //항상 Non-Null
                if (userComment != "") {
                    item!!.commentsCount += 1L
                    item!!.comment = addComment(feed.FeedID!!)//현재 입력한 값으로 변경
                    item.comment!!.duration = getDateDuration(item.comment!!.madeDate)
                    item.comment!!.creator = nowUser
                    binding.feed = item
                    binding.executePendingBindings() // 변경된 값을 뷰에 적용
                }
            }
            //댓글 아이콘을 눌렀을 때
            binding.btnComment.setOnClickListener {
                val intent = Intent(context, subactivity_comment::class.java)
                feed.position = bindingAdapterPosition
                intent.putExtra("feed", feed)
                context.startActivity(intent)
            }
            // 공유하기 버튼 눌렀을 때 (게시물 공유)
            binding.btnShare.setOnClickListener {


            }
            //좋아요 표시 관리
            binding.lllike.setOnClickListener { controlLike(feed) }
            //메뉴 선택 시
            binding.btnFeedMenu.setOnClickListener { view ->
                feed.position = bindingAdapterPosition
//                val popup1 = CustomPopup(context, view)
//                popup1.setItems(context, fbModule, feed)
//                popup1.setOnMenuItemClickListener(popup1)
//                popup1.setVisible(nowUser.token == feed.UserToken)
//                popup1.show()
                val popupMenu = customMenuPopup(context,view)
                popupMenu.setItem(feed)
            }
            //프로필을 눌렀을때 그 사용자의 프로필 정보 화면으로 이동
            binding.llProfile.setOnClickListener {
                val intent = Intent(context, ProfileInfoActivity::class.java)
                intent.putExtra("userID", feed.UserToken)
                context.startActivity(intent)
            }

            //뷰에 피드 반영
            binding.feed = feed
            binding.executePendingBindings()
        }
    }

    //댓글 추가
    private fun addComment(FeedID: String): Comment? {

        val data: MutableMap<String?, Any?> = HashMap()
        //유저정보, 댓글내용, 작성시간
        val comment = Comment()
        comment.getData(
                nowUser.token,
                binding.edtComment.text.toString(),
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
                                .withLocale(Locale.KOREA)
                                .withZone(ZoneId.of("Asia/Seoul")))

        )
        data["comment"] = comment

        //입력한 댓글 화면에 표시하기
        CommentsCounter().addCounter(data, context, FeedID)
        //키보드 내리기
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.edtComment.windowToken, 0)
        binding.edtComment.clearFocus()
        binding.edtComment.text = null

        //알림 푸시메시지 전송
        myFCMService!!.sendPostToFCM(
                context,
                feedUserFcmtoken, "${nowUser.username}님이 댓글을 남겼습니다.\"${comment.contents}\""
        )

        return comment
    }


    //좋아요를 관리하는 메소드
    private fun controlLike(feed: Feed) {
        if (limit < 5) {
            limit += 1
            strings = nowUser.likedPost
            val map = HashMap<Any, Any>()
            if (!feed.isUserLiked) {
                //현재 좋아요를 누르지 않은 상태
                feed.likeCount += 1
                feed.isUserLiked = true
                strings!!.add(feed.FeedID!!)
                myFCMService!!.sendPostToFCM(
                        context,
                        feedUserFcmtoken, "${nowUser.username}님이 좋아요를 표시했습니다."
                )
            } else {
                //현재 좋아요를 누른 상태
                feed.likeCount -= 1
                feed.isUserLiked = false
                strings!!.removeAll(Arrays.asList(feed.FeedID!!))
                strings!!.remove(feed.FeedID)
            }
            nowUser.likedPost = strings
            map["nowUser"] = nowUser
            map["liked"] = feed.isUserLiked
            pv.updateUser(nowUser)
            likeCounter().updateCounter(map, feed.FeedID)

            pv.getBookWorm(nowUser.token)
            pv.bwdata.observe(context as MainActivity) {
//                val achievement = Achievement(context, fbModule, nowUser, it)
//                achievement.CompleteAchievement(nowUser, context)
            }

            binding.feed = feed
            binding.executePendingBindings()

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
            tv.background = context.getDrawable(R.drawable.label_design) //디자인 적용
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


    //시간차 구하기 n분 전, n시간 전 등등
    private fun getDateDuration(createdTime: String?): String {
        var dateDuration = ""
        val now = System.currentTimeMillis()
        val dateNow = Date(now) //현재시각
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val dateCreated = dateFormat.parse(createdTime)
            val duration = dateNow.time - dateCreated.time //시간차이 mills
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
                SimpleDateFormat("yyyy-MM-dd").format(duration)
            }
            return dateDuration
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    //메달 표시 유무에 따른 세팅
    //댓글창 메달도 있기때문에 여기는 인자를 userInfo, bool 이렇게 2개 받음
    private fun setMedal(userInfo: UserInfo, bool: Boolean?) {
        if (userInfo.medalAppear!!) { //메달을 표시한다면
            if (bool == false) { //피드라면
                binding.ivMedal.visibility = View.VISIBLE
                when (userInfo.tier!!.toInt()) {
                    1 -> binding.ivMedal.setImageResource(R.drawable.medal_bronze)
                    2 -> binding.ivMedal.setImageResource(R.drawable.medal_silver)
                    3 -> binding.ivMedal.setImageResource(R.drawable.medal_gold)
                    4 -> {}
                    5 -> {}
                    else -> binding.ivMedal.setImageResource(0)
                }
            } else { //댓글이라면
                binding.ivCommentMedal.visibility = View.VISIBLE
                when (userInfo.tier!!.toInt()) {
                    1 -> binding.ivCommentMedal.setImageResource(R.drawable.medal_bronze)
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