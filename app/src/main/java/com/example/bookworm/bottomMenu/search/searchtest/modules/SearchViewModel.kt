package com.example.bookworm.bottomMenu.search.searchtest.modules

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.feed.FeedViewModel
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchDetailActivity
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

//전반적인 검색에 사용될 뷰모델 -> 데이터 가공, 처리 담당
class SearchViewModel(context: Context) : ViewModel() {
    //상태 추적을 위한 변수
    enum class State {
        Loading, Done, Error
    }

    //context Object
    @SuppressLint("StaticFieldLeak")
    // 다양한 곳에서 뷰모델을 사용하므로 그에 맞게 context를 변환해주어야 함.
    val ct = when (context) {
        is MainActivity -> context
        is SearchMainActivity -> context
        is SearchDetailActivity -> context
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
    private var loadDataState: MutableLiveData<State>? = null
    val liveKeywordData: MutableLiveData<String> = MutableLiveData()

    //알라딘의 인기도서 가져오는 메소드
    fun loadPopularBook(stateLiveData: MutableLiveData<State>, resultBookList: ArrayList<Book>) {
        loadDataState = stateLiveData
        loadDataState!!.value = State.Loading
        viewModelScope.launch {
            val result = searchDataRepository.loadPopularBookData()
            when (result.isSuccessful) {
                true -> {
                    val data = result.body()?.let { JSONObject(it)["item"] as JSONArray }
                    for (i in 0 until data!!.length()) {
                        val item = data.getJSONObject(i)
                        resultBookList.add(convertToBook(item, true))
                    }
                    loadDataState!!.value = State.Done
                }
                else -> {
                    loadDataState!!.value = State.Error
                }
            }
        }
    }

    //알라딘의 추천도서 가져오는 메소드


    /**
     * Json데이터를 Book 객체로 변환 */

    private fun convertToBook(data: JSONObject, isRc: Boolean) = Book().apply {
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
        }
    }

    fun loadBooks(keyword: String, stateLiveData: MutableLiveData<State>, resultBookList: ArrayList<Book>, page: Int) {
        loadDataState = stateLiveData
        loadDataState!!.value = State.Loading
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
                        loadDataState!!.value = State.Done
                    }catch (e:Exception){
                        loadDataState!!.value = State.Error
                    }

                }
                else -> {
                    loadDataState!!.value = State.Error
                }
            }
        }
    }

    fun loadBookDetail(itemId: String, stateLiveData: MutableLiveData<State>) {

    }

    private fun replaceLtgt(text: String): String { //가끔 &lt &gt로 표시되는 경우가 발생  => 이를 해결
        var text = text
        text = text.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&lt", "<")
                .replace("&gt", ">")
        return text
    }
//보류
//    fun loadRanking(stateLiveData: MutableLiveData<State>, bwList: ArrayList<BookWorm>, userList: ArrayList<UserInfo>) {
//        loadDataState = stateLiveData
//        loadDataState!!.value = State.Loading
//        viewModelScope.launch {
//            try {
//                val result = searchDataRepository.loadRanking()
//                if (result.documents.isNotEmpty()) {
//                    for (i: DocumentSnapshot in result.documents) {
//                        val bwData = i["BookWorm"] as Map<String>
//                    }
//                    loadDataState!!.value = State.Done
//                }
//                loadDataState!!.value = State.Error
//            } catch (e: NullPointerException) {
//                loadDataState!!.value = State.Error
//            }
//        }
//
//    }


}