package com.example.bookworm.bottomMenu.feed

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
    fun provideFirebaseInstance() =
            FirebaseFirestore.getInstance()

    fun provideQueryLoadPostsOrderByFeedID(pageSize: Int = 5) =
            provideFirebaseInstance()
                    .collection("feed")
                    .orderBy("FeedID", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())

    fun provideQueryLoadPostsOrderByFeedID() =
            provideFirebaseInstance()
                    .collection("feed")
                    .orderBy("FeedID", Query.Direction.DESCENDING)

    //피드 내용 불러오는 쿼리
    fun provideUserByUserToken(userToken: String) =
            provideFirebaseInstance()
                    .collection("users")
                    .document(userToken)

    fun provideQueryCommentsLately(id: String) =
            FirebaseFirestore
                    .getInstance()
                    .collection("feed")
                    .document(id)
                    .collection("comments")
                    .orderBy("commentID", Query.Direction.DESCENDING)
                    .limit(1)

    //피드 업로드에 사용하는 쿼리
    fun provideQueryUploadPost(item: Feed) = FirebaseFirestore
            .getInstance()
            .collection("feed")
            .add(item)

    //FeedId로 피드 문서 검색하는 쿼리
    fun provideQueryPostByFeedID(feedId: String) =
            FirebaseFirestore
                    .getInstance()
                    .collection("feed")
                    .document(feedId)

    fun provideQueryCommentsInFeedByFeedID(feedId: String) =
            FirebaseFirestore
                    .getInstance()
                    .collection("feed")
                    .document(feedId)
                    .collection("comments")
                    .orderBy("commentID", Query.Direction.DESCENDING)

}