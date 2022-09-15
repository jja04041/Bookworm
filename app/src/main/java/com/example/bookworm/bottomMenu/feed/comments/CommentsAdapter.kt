package com.example.bookworm.bottomMenu.feed.comments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.databinding.LayoutCommentItemBinding
import com.example.bookworm.databinding.LayoutCommentSummaryBinding

//댓글 불러오는 어댑터
class CommentsAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(Companion) {

    private val vType = mapOf("Loading" to 0, "SummaryFeed" to 1, "Comments" to 2)

    companion object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is Feed && newItem is Feed)
                oldItem.FeedID == newItem.FeedID
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
                newSummaryItemViewHolder(view)
            }
            vType["Loading"] -> {
                view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.layout_comment_item, parent, false)
                newCommentItemViewHolder(view)
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
    inner class newCommentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCommentItemBinding.bind(itemView)
        fun bind(item: Comment) {
            binding.lifecycleOwner = itemView.context as SubActivityComment
            binding.comment=item
//            binding.apply {
//                //댓글 작성자 프로필 이미지 로드
//                Glide.with(itemView.context)
//                        .load(item.creator!!.profileimg)
//                        .into(imgProfile)
//                //댓글 작성자 닉네임 세팅
//                tvNickname.text = item.creator!!.username
//                //댓글 내용 세팅
//                tvCommentContent.text = item.contents
//                //댓글 작성일자 세팅
//                tvDate.text = item.duration
//            }
        }
    }

    //게시물 요약 내용
    inner class newSummaryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCommentSummaryBinding.bind(itemView)

        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        fun bind(item: Feed) {
            binding.apply {
                //작성자 관련
                item.Creator!!.apply {
                    tvNickname.text = username
                    Glide.with(itemView.context)
                            .load(profileimg)
                            .into(ivProfileImage)
                }

                //내용
                //책
                item.book!!.apply {
                    feedBookAuthor.text = author
                    feedBookTitle.text = title
                    Glide.with(itemView.context).load(img_url).into(feedBookThumb)
                }
                //피드 내용
                tvFeedText.text = item.feedText
                if (item.imgurl != "") Glide.with(itemView.context).load(item.imgurl).into(ivFeedImage)
                ivFeedImage.isVisible = (item.imgurl != "")
                tvCommentCount.text = "댓글 ${item.commentsCount}개"
                tvDate.text = item.duration
                //라벨 세팅
                llLabel.removeAllViews() //기존에 설정된 값을 초기화 시켜줌.
                val idx = item.label!!.indexOf("")
                for (i in 0 until idx) {
                    //뷰 생성
                    val tv = TextView(itemView.context)
                    tv.text = item.label[i] //라벨에 텍스트 삽입
                    tv.background = itemView.context.getDrawable(R.drawable.label_design) //디자인 적용
                    tv.setBackgroundColor((itemView.context as MainActivity).getColor(R.color.subcolor_2)) //배경색 적용
                    val params = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ) //사이즈 설정
                    params.setMargins(10, 0, 10, 0) //마진 설정
                    tv.layoutParams = params //설정값 뷰에 저장
                    llLabel.addView(tv) //레이아웃에 뷰 세팅
                }
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val safePosition = holder.bindingAdapterPosition
        var item = getItem(safePosition)
        when (item) {
            is Feed -> {
                (holder as newSummaryItemViewHolder).bind(item)
            }
            is Comment -> {
                if (item.commentID != "") (holder as newCommentItemViewHolder).bind(item)
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
}