package com.example.bookworm.bottomMenu.feed

import android.view.View


interface OnFeedMenuClickListener {
    fun onItemClick(holder: FeedViewHolder, view: View, position: Int)
}