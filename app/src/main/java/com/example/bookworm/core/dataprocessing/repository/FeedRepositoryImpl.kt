package com.example.bookworm.core.dataprocessing.repository

import com.example.bookworm.bottomMenu.feed.items.Feed

class FeedRepositoryImpl : DataRepository.HandleFeed {
    override fun getFeedList(lastVisible: String?) {
        if (lastVisible != null) {

        }
    }

    override fun createFeed(feed: Feed) {
        TODO("Not yet implemented")
    }

    override fun deleteFeed(feedId: String) {
        TODO("Not yet implemented")
    }
}