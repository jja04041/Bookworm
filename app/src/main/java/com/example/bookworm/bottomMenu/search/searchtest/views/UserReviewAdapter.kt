package com.example.bookworm.bottomMenu.search.searchtest.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.feed.FeedAdapter
import com.example.bookworm.databinding.LayoutBookSearchDetailReviewBinding
import com.example.bookworm.databinding.LayoutItemLoadingBinding

class UserReviewAdapter(val context: Context) :
        ListAdapter<Feed, RecyclerView.ViewHolder>(Companion) {

    //비교 객체
    companion object : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem.feedID == newItem.feedID
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        return when (viewType) {
            0 -> {
                view = inflater.inflate(R.layout.layout_book_search_detail_review, parent, false)
                UserReviewViewHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val safePosition = holder.bindingAdapterPosition
        if (holder is UserReviewViewHolder) {
            val feed = getItem(position) ?: return
            holder.bindItem(feed)
        } else showLoadingView(holder as LoadingViewHolder, safePosition)
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //
    }

    //각각의 독립된 아이디를 가질 수 있게 함.
    override fun getItemId(position: Int): Long {
        return currentList[position].hashCode().toLong()
    }

    //뷰타입 설정 (0은 리뷰 뷰홀더 , 1은 로딩 뷰홀더)
    override fun getItemViewType(position: Int) = if (currentList[position].feedID != null) 1 else 0

    inner class UserReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding by lazy {
            LayoutBookSearchDetailReviewBinding.bind(itemView)
        }

        fun bindItem(item: Feed) {
            binding.apply {
                tvDate.text = item.date
                tvNickname.text = item.creatorInfo.username
                tvCommentContent.text = item.feedText
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding by lazy {
            LayoutItemLoadingBinding.bind(itemView)
        }
    }
}