package com.example.bookworm.bottomMenu.feed.comments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.bookworm.R
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.feed.customMenuPopup
import com.example.bookworm.bottomMenu.profile.views.ProfileInfoActivity
import com.example.bookworm.bottomMenu.search.searchtest.views.BookDetailActivity
import com.example.bookworm.databinding.LayoutCommentItemBinding
import com.example.bookworm.databinding.LayoutCommentSummaryBinding
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

//댓글 불러오는 어댑터
class CommentsAdapter(val context: Context) : ListAdapter<Any, RecyclerView.ViewHolder>(Companion) {


    private val vType = mapOf("Loading" to 0, "SummaryFeed" to 1, "Comments" to 2)

    companion object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is Feed && newItem is Feed)
                oldItem.feedID == newItem.feedID
            else
                (oldItem as Comment).commentID == (newItem as Comment).commentID
        }
    }

    //각 아이템에 고유값을 부여하여, 리스트가 갱신될때, 이미 있는 아이템이라면 갱신하지 않음.
    override fun getItemId(position: Int): Long {
        return currentList[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View

        return when (viewType) {
            vType["SummaryFeed"] -> {
                view = inflater.inflate(R.layout.layout_comment_summary, parent, false)
                SummaryItemViewHolder(view)
            }
            vType["Loading"] -> {
                view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.layout_comment_item, parent, false)
                CommentItemViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    //로딩바 클래스
    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //
    }

    //댓글 뷰홀더
    inner class CommentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCommentItemBinding.bind(itemView)
        fun bind(item: Comment) {
            binding.apply {
                //DataBinding을 통한 뷰 세팅
                lifecycleOwner = itemView.context as SubActivityComment
                comment = item
                tvDate.text = getDateDuration(item.madeDate)

                //프로필 클릭 시 해당 사용자의 프로필 정보 화면으로 이동하게
                llProfile.setOnClickListener {
                    val intent = Intent(itemView.context, ProfileInfoActivity::class.java)
                    intent.putExtra("userID", item.userToken)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    //게시물 요약 내용
    inner class SummaryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCommentSummaryBinding.bind(itemView)

        private fun deleteThisFeed(feed: Feed) {

        }

        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        fun bind(item: Feed) {
            binding.apply {
                //작성자 관련
                item.creatorInfo.apply {
                    tvNickname.text = username
                    Glide.with(itemView.context)
                            .load(profileimg).circleCrop()
                            .into(ivProfileImage)
                }
                btnFeedMenu.setOnClickListener { v ->
                    val popupMenu = customMenuPopup(context, v)
                    if (item.isUserPost) {
                        popupMenu.setItem(item)
                        popupMenu.liveState.observe(context as MainActivity) {
                            if (it == popupMenu.FEED_DELETE) {
                                //FragmentFeed의 FeedAdapter에서 이 데이터를 삭제한다.
                                val intent = context.intent
                                intent.putExtra("deleteTarget", item) //삭제 대상 게시물 아이템을 인텐트에 담는다.
                                //이 게시물은 삭제할 것이라고 액티비티에 알려줌
                                (context as SubActivityComment).setResult(SubActivityComment.FEED_DELETE, intent)
                                (context as SubActivityComment).finish()
                            }
                        }
                    }
                }
                //내용
                //책
                item.book.apply {
                    feedBookAuthor.text = author
                    feedBookTitle.text = title
                    Glide.with(itemView.context).load(imgUrl).into(feedBookThumb)
                    llbook.setOnClickListener {
                        val intent = Intent(itemView.context, BookDetailActivity::class.java)
                        intent.putExtra("BookID", item.book.itemId)
                        itemView.context.startActivity(intent)
                    }
                }
                //피드 내용
                tvFeedText.text = item.feedText
                if (item.imgurl != "") Glide.with(itemView.context).load(item.imgurl).signature(ObjectKey(System.currentTimeMillis().toString())).into(ivFeedImage)
                ivFeedImage.isVisible = (item.imgurl != "")
                tvCommentCount.text = "댓글 ${item.commentsCount}개"
                tvDate.text = item.duration
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val safePosition = holder.bindingAdapterPosition
        var item = getItem(safePosition)
        when (item) {
            is Feed -> {
                (holder as SummaryItemViewHolder).bind(item)
            }
            is Comment -> {
                if (item.commentID != "") (holder as CommentItemViewHolder).bind(item)
                else showLoadingView(holder as LoadingViewHolder, safePosition)
            }
            else -> {}
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] is Feed) vType["SummaryFeed"]!!
        else if ((currentList[position] as Comment).commentID == "") vType["Loading"]!!
        else vType["Comments"]!!
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
}