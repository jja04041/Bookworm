package com.example.bookworm.bottomMenu.feed

import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Transaction
import javax.inject.Singleton

// FireStore에서 검색하는 쿼리를 담은 Object
//싱글톤패턴으로 디자인됨
@Module
object FireStoreLoadModule {
    @Provides
    @Singleton
    //유저 콜렉션
    fun provideQueryPathToUserCollection() = provideFirebaseInstance()
            .collection("users")

    //피드 콜렉션
    fun provideQueryPathToFeedCollection() = provideFirebaseInstance()
            .collection("feed")

    //책볼레 콜렉션
    fun provideQueryPathToBwCollection() =
            provideFirebaseInstance()
                    .collection("BookWorm")

    //챌린지 콜렉션
    fun provideQueryPathToChallengeCollection() =
            provideFirebaseInstance()
                    .collection("challenge")

    //피드 내용 불러오는 쿼리
    fun provideFirebaseInstance() =
            FirebaseFirestore.getInstance()

    fun provideQueryLoadPostsOrderByFeedID(pageSize: Int = 5) =
            provideQueryPathToFeedCollection()
                    .orderBy("FeedID", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())

    fun provideQueryLoadPostsOrderByFeedID() =
            provideQueryPathToFeedCollection()
                    .orderBy("FeedID", Query.Direction.DESCENDING)

    //피드 내용 불러오는 쿼리
    fun provideUserByUserToken(userToken: String) =
            provideQueryPathToUserCollection()
                    .document(userToken)

    fun provideQueryCommentsLately(id: String) =
            provideQueryPathToFeedCollection()
                    .document(id)
                    .collection("comments")
                    .orderBy("commentID", Query.Direction.DESCENDING)
                    .limit(1)

    //피드 업로드에 사용하는 쿼리
    fun provideQueryUploadPost(item: Feed) =
            provideQueryPathToFeedCollection()
                    .add(item)

    //FeedId로 피드 문서 검색하는 쿼리
    fun provideQueryPostByFeedID(feedId: String) =
            provideQueryPathToFeedCollection()
                    .document(feedId)

    fun provideQueryCommentsInFeedByFeedID(feedId: String) =
            provideQueryPathToFeedCollection()
                    .document(feedId)
                    .collection("comments")
                    .orderBy("commentID", Query.Direction.DESCENDING)


}