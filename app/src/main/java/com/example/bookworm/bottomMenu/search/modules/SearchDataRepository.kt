package com.example.bookworm.bottomMenu.search.modules

import android.content.Context
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.feed.FireStoreLoadModule
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import kotlin.collections.HashMap


//실제로 서버와 연동하여 데이터를 가져오는 레포지토리 (데이터 송수신을 담당)
class SearchDataRepository(val context: Context) {

    //알라딘 Api와 연동할 수 있는 Retrofit 객체를 미리 생성해둠
    private val aladinApi =
        RetrofitInstance(context.getString(R.string.aladinBaseurl)).getInstance()
            .create(SearchRetrofitInterface::class.java)

    //파이어스토어에 저장된 데이터를 접근하기 위한 DAO
    private val firestoreDb = FirebaseFirestore.getInstance()


    suspend fun loadPopularBookData() =
        aladinApi.getRecom(QueryForRetrofit.getQueryForRecommend(context))


    suspend fun loadRanking() = firestoreDb.collection("users")
        .orderBy("BookWorm.readcount", Query.Direction.DESCENDING).limit(3)
        .get().await()

    //검색한 도서를 가져오는 메소드 -> ViewModelScope 내에서 접근해야함 -> 비동기 처리
    suspend fun loadSearchedBooks(keyword: String, page: Int) =
        aladinApi.getResult(QueryForRetrofit.getQueryForSearchBooks(context, keyword, page))


    //도서의 상세정보를 가져오는 메소드 -> ViewModelScope 내에서 접근해야함 -> 비동기 처리
    suspend fun loadBookDetail(itemId: String) = aladinApi.getItem(
        QueryForRetrofit.getQueryForSearchBookDetail(
            context = context,
            itemId = itemId
        )
    )

    //어플 사용자의 리뷰를 가져오는 메소드 -> ViewModelScope 내에서 접근해야함 -> 비동기 처리
    suspend fun loadUserBookReview(
        itemId: String,
        page: Int = 5,
        lastVisible: String? = null
    ): QuerySnapshot? {
        var query = FireStoreLoadModule.provideQueryPathToFeedCollection()
            .whereEqualTo("book.itemId", itemId)
            .orderBy("feedID", Query.Direction.ASCENDING)
        if (lastVisible != null) query = query.startAfter(lastVisible)
        return query.limit(page.toLong()).get().await()
    }

    //검색한 사용자 정보를 가져오기 위한 메소드 ->  ViewModelScope 내에서 접근해야함 -> 비동기 처리
    suspend fun loadUserData(keyword: String, page: Int, lastVisible: String?): QuerySnapshot? {
        var query: Query = FireStoreLoadModule.provideQueryPathToUserCollection()
            .whereGreaterThanOrEqualTo("username", keyword)
            .whereLessThanOrEqualTo("username",  keyword + "\uf8ff")

            .orderBy("username")
        if (lastVisible != null) query = query.startAfter(lastVisible)
        return query.limit(page.toLong()).get().await()
    }

    //Retrofit 인스턴스
    inner class RetrofitInstance(baseUrl: String) {
        private val client: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        fun getInstance(): Retrofit {
            return client
        }
    }


    //쿼리 작성 -> 레프로핏작업을 위한
    private object QueryForRetrofit {

        //추천 도서를 가져오기 위한 쿼리
        fun getQueryForRecommend(context: Context): Map<String, String> {
            val query = setDefault(context)
            query["QueryType"] = "Bestseller"
            return query
        }

        //검색한 도서의 결과를 가져오기 위한 쿼리
        fun getQueryForSearchBooks(
            context: Context,
            keyword: String,
            page: Int
        ): Map<String, String> {
            val query = setDefault(context)
            query["QueryType"] = "Keyword"
            query["Query"] = keyword
            query["Start"] = page.toString()
            return query
        }

        //도서 상세정보를 가져오기 위한 쿼리
        fun getQueryForSearchBookDetail(context: Context, itemId: String): Map<String, String> {
            val query = setDefault(context)
            query["ItemId"] = itemId
            query["ItemIdType"] = "ItemId"
            return query
        }

        //기본 세팅
        private fun setDefault(context: Context): HashMap<String, String> {
            val query = HashMap<String, String>()
            query["ttbkey"] = context.getString(R.string.ttbKey)
            query["MaxResults"] = "10" //최대 길이
            query["output"] = "js"
            query["SearchTarget"] = "Book"
            query["Version"] = "20131101"
            return query
        }
    }

    //
}