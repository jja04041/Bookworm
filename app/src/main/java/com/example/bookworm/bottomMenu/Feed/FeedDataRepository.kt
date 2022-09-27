package com.example.bookworm.bottomMenu.feed

import android.content.Context
import kotlinx.coroutines.tasks.await

class FeedDataRepository(context: Context) {
    suspend fun deleteFeed(feed: Feed) {
        FireStoreLoadModule.provideQueryPathToFeedCollection()
                .document(feed.feedID!!).delete().await()
    }

}