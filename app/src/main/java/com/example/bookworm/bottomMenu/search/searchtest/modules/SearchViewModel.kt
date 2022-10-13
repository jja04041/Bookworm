package com.example.bookworm.bottomMenu.search.searchtest.modules

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.bookworm.LoadState
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.feed.FeedViewModel
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.bottomMenu.search.searchtest.views.BookDetailActivity
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

//전반적인 검색에 사용될 뷰모델 -> 데이터 가공, 처리 담당
class SearchViewModel(context: Context) : ViewModel() {


    //context Object
    @SuppressLint("StaticFieldLeak")
    // 다양한 곳에서 뷰모델을 사용하므로 그에 맞게 context를 변환해주어야 함.
    val ct = when (context) {
        is MainActivity -> context
        is SearchMainActivity -> context
        is BookDetailActivity -> context
        else -> context as SubActivityComment
    }

    class Factory(val context: Context) :
            ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return SearchViewModel(context) as T
        }
    }


    private val searchDataRepository = SearchDataRepository(ct) //검색 결과를 가져오는 레포지토리
    private val userInfoViewModel = ViewModelProvider(ct, //사용자 데이터 처리시 사용
            UserInfoViewModel.Factory(ct))[UserInfoViewModel::class.java]
    private val feedViewModel = ViewModelProvider(ct, //사용자 데이터 처리시 사용
            FeedViewModel.Factory(ct))[FeedViewModel::class.java]
    private var loadDataState: MutableLiveData<LoadState>? = null
    val liveKeywordData: MutableLiveData<String> = MutableLiveData()

    //알라딘의 인기도서 가져오는 메소드
    fun loadPopularBook(stateLiveData: MutableLiveData<LoadState>, resultBookList: ArrayList<Book>) {
        loadDataState = stateLiveData
        loadDataState!!.value = LoadState.Loading
        viewModelScope.launch {
            val result = searchDataRepository.loadPopularBookData()
            when (result.isSuccessful) {
                true -> {
                    Log.d("책 데이터", result.body().toString())
                    val data = result.body()?.let { JSONObject(it)["item"] as JSONArray }
                    for (i in 0 until data!!.length()) {
                        val item = data.getJSONObject(i)
                        resultBookList.add(convertToBook(item, true))
                    }
                    loadDataState!!.value = LoadState.Done
                }
                else -> {
                    loadDataState!!.value = LoadState.Error
                }
            }
        }
    }

    //알라딘의 추천도서 가져오는 메소드


    /**
     * Json데이터를 Book 객체로 변환 */

    private fun convertToBook(data: JSONObject, isRc: Boolean, isDetail: Boolean = false) = Book().apply {
        data.apply {
            title = getString("title")
            imgUrl = getString("cover").replace("coversum", "cover500");
            itemId = getString("itemId")
            author = getString("author")
            categoryName = getString("categoryName")
            content = replaceLtgt(getString("description"))
            publisher = getString("publisher")
            isbn = getString("isbn13")
            isRecommend = isRc
            //상세정보에서만 필요한 데이터들
            if (isDetail) {
                purchaseLink = getString("link") //구매 링크
                originPrice = getInt("priceStandard").toString()//정상가
                salePrice = getInt("priceSales").toString()//판매가
                userRate = getInt("customerReviewRank") / 2F//사용자 평점
            }
        }
    }

    fun loadBooks(keyword: String, stateLiveData: MutableLiveData<LoadState>, resultBookList: ArrayList<Book>, page: Int) {
        loadDataState = stateLiveData
        loadDataState!!.value = LoadState.Loading
        viewModelScope.launch {
            val result = searchDataRepository.loadSearchedBooks(keyword, page)
            when (result.isSuccessful) {
                true -> {
                    try {
                        val data = result.body()?.let { JSONObject(it)["item"] as JSONArray }
                        for (i in 0 until data!!.length()) {
                            val item = data.getJSONObject(i)
                            resultBookList.add(convertToBook(item, false))
                        }
                        loadDataState!!.value = LoadState.Done
                    } catch (e: Exception) {
                        loadDataState!!.value = LoadState.Error
                    }

                }
                else -> {
                    loadDataState!!.value = LoadState.Error
                }
            }
        }
    }

    fun loadBookDetail(itemId: String, stateLiveData: MutableLiveData<Book>, reviewList: ArrayList<Feed>) {
        //itemId로 검색하고 데이터를 받아옴
        //책 검색과 동시에 책 리뷰를 받아와야 함.
        viewModelScope.launch {
            val result = searchDataRepository.loadBookDetail(itemId)
            if (result.isSuccessful) {
                val book = result.body()?.let {
                    convertToBook(data = (JSONObject(it)["item"] as JSONArray).getJSONObject(0), isRc = false,true)
                }
                stateLiveData.value = book //검색된 책 데이터
            } else stateLiveData.value = Book()
        }
    }

    /** 가끔 &lt &gt로 표시되는 경우가 발생
     *  => 이를 해결
     * */
    private fun replaceLtgt(text: String): String = text.replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&lt", "<")
            .replace("&gt", ">")


    //리뷰 정보를 가져오는 함수
    fun getReviewData(stateLiveData: MutableLiveData<LoadState>? = null, reviewList: ArrayList<Feed>, itemId: String) {

    }
//보류
//    fun loadRanking(stateLiveData: MutableLiveData<LoadState>, bwList: ArrayList<BookWorm>, userList: ArrayList<UserInfo>) {
//        loadDataState = stateLiveData
//        loadDataState!!.value = LoadState.Loading
//        viewModelScope.launch {
//            try {
//                val result = searchDataRepository.loadRanking()
//                if (result.documents.isNotEmpty()) {
//                    for (i: DocumentSnapshot in result.documents) {
//                        val bwData = i["BookWorm"] as Map<String>
//                    }
//                    loadDataState!!.value = LoadState.Done
//                }
//                loadDataState!!.value = LoadState.Error
//            } catch (e: NullPointerException) {
//                loadDataState!!.value = LoadState.Error
//            }
//        }
//
//    }


}