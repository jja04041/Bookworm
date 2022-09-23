package com.example.bookworm.bottomMenu.search.searchtest.modules

import android.content.Context
import com.example.bookworm.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


//실제로 서버와 연동하여 데이터를 가져오는 레포지토리 (데이터 송수신을 담당)
class SearchDataRepository(val context: Context) {
    private val aladinApi = RetrofitInstance(context.getString(R.string.aladinBaseurl)).getInstance()
            .create(SearchRetrofitInterface::class.java)
    private val firestoreDb = FirebaseFirestore.getInstance()


    suspend fun loadPopularBookData() =
            aladinApi.getRecom(QueryForRetrofit.getQueryForRecommend(context))


    suspend fun loadRanking() = firestoreDb.collection("users")
            .orderBy("BookWorm.readcount", Query.Direction.DESCENDING).limit(3)
            .get().await()

    suspend fun loadSearchedBooks(keyword: String, page: Int) = aladinApi.getResult(QueryForRetrofit.getQueryForSearchBooks(context, keyword, page))

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

    //쿼리 작성
    private object QueryForRetrofit {

        fun getQueryForRecommend(context: Context): Map<String, String> {
            val query = setDefault(context)
            query["QueryType"] = "Bestseller"
            return query
        }

        fun getQueryForSearchBooks(context: Context, keyword: String, page: Int): Map<String, String> {
            val query = setDefault(context)
            query["QueryType"] = "Keyword"
            query["Query"] = keyword
            query["Start"] = page.toString()
            return query
        }

        fun getQueryForSearchBookDetail(context: Context, itemId: String): Map<String, String> {
            val query = setDefault(context)
            query["ItemId"] = itemId
            query["ItemIdType"] = "ItemId"
            return query
        }

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
}