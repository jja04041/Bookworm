package com.example.bookworm.bottomMenu.profile.submenu.posts

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.databinding.FragmentProfilePostItemBinding


class PostDisplayAdapter :
    ListAdapter<Feed, PostDisplayViewHolder>(PostDiffCallback) {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDisplayViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: FragmentProfilePostItemBinding =
            FragmentProfilePostItemBinding.inflate(inflater)
        return PostDisplayViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: PostDisplayViewHolder, position: Int) {
        val safePosition = holder.bindingAdapterPosition
        val item = currentList[safePosition]
        holder.setItem(item)
    }

    //값 업데이트를 위한 비교 콜백
    object PostDiffCallback : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean =
            (oldItem.feedID == newItem.feedID)

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean =
            (oldItem == newItem)

    }
}