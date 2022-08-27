package com.example.bookworm.bottomMenu.feed.temp

import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Singleton

// FireStore에서 검색하는 쿼리를 담은 Object
@Module
object FireStoreLoadModule {
    @Provides
    @Singleton

    //피드 내용 불러오는 쿼리
    fun provideQueryPostsByUserID(pageSize: Int) =
            FirebaseFirestore
                    .getInstance()
                    .collection("feed")
                    .orderBy("FeedID", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())

    fun provideQueryCommentsLately( id: String) =
            FirebaseFirestore
                    .getInstance()
                    .collection("feed")
                    .document(id)
                    .collection("comments")
                    .orderBy("commentID", Query.Direction.DESCENDING)
                    .limit(1)
}