package com.example.bookworm.bottomMenu.search.itemlisteners

import com.example.bookworm.bottomMenu.search.adapters.FeedResultAdapter
import com.example.bookworm.bottomMenu.search.adapters.UserResultAdapter

interface OnFeedItemListener {
    fun onClick(holder: FeedResultAdapter.FeedViewHolder, position: Int)
}