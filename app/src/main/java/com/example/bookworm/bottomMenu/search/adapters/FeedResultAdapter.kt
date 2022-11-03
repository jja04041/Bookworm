package com.example.bookworm.bottomMenu.search.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.search.itemlisteners.OnFeedItemListener
import com.example.bookworm.bottomMenu.search.itemlisteners.OnUserItemListener
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.LayoutUserItemBinding

class FeedResultAdapter : ListAdapter<Feed, RecyclerView.ViewHolder>(Companion) {
    var listener: OnFeedItemListener? = null

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
            //사용자 아이템
            1 -> {
                view = inflater.inflate(R.layout.layout_user_item, parent, false)
                FeedViewHolder(view)
            }
            //로딩뷰 아이템
            else -> {
                view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    fun addListener(listener: OnFeedItemListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            if (this is FeedViewHolder)
                setItem(currentList[position])
            else
                showLoadingView(this as LoadingViewHolder, position)
        }
    }

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutUserItemBinding.bind(itemView)
        fun setItem(feed: Feed) {
            binding.apply {

                root.setOnClickListener {
                    listener!!.onClick(this@FeedViewHolder, bindingAdapterPosition)
                }
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {}

    //뷰타입 확인
    override fun getItemViewType(pos: Int): Int {
        return if (currentList[pos].feedID != "") 1 else 2
    }
}