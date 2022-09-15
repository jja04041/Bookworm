package com.example.bookworm.bottomMenu.profile.submenu.posts

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment
import com.example.bookworm.databinding.FragmentProfilePostItemBinding

class PostDisplayViewHolder(val binding: FragmentProfilePostItemBinding, val context: Context) :
    RecyclerView.ViewHolder(binding.root) {

    fun setItem(item: Feed) {
        binding.tvPostText.text = item.feedText
        Glide.with(binding.root).load(item.book!!.img_url).into(binding.ivThumb)
        binding.postContainer.setOnClickListener {
            val intent = Intent(context, SubActivityComment::class.java)
            intent.putExtra("item", item)
            intent.putExtra("position", absoluteAdapterPosition)
            context!!.startActivity(intent)
        }
    }

}