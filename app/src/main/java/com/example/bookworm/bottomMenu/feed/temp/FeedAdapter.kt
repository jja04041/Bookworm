package com.example.bookworm.bottomMenu.feed.temp

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.databinding.FeedDataBinding

class FeedAdapter : PagingDataAdapter<Feed, FeedViewHolder>(Companion) {

    private lateinit var dataBinding: FeedDataBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {

        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.feed_data, parent,false)
        dataBinding.lifecycleOwner = parent.context as FeedActivity


        return FeedViewHolder(dataBinding, parent.context)
    }

    override fun getItemViewType(position: Int): Int {
        return position //아이템 꼬임 문제 방지
    }



    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed = getItem(position) ?: return
        holder.bindFeed(feed)
    }

    companion object : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem.FeedID == newItem.FeedID

        }
    }
}