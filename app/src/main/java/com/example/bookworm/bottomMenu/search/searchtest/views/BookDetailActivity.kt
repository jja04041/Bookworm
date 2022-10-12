package com.example.bookworm.bottomMenu.search.searchtest.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.LoadState
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.feed.FeedViewModel
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.bottomMenu.search.searchtest.modules.SearchViewModel
import com.example.bookworm.databinding.LayoutBookSearchDetailBinding

class BookDetailActivity : AppCompatActivity() {
    val binding by lazy {
        LayoutBookSearchDetailBinding.inflate(layoutInflater)
    }
    private val bookId by lazy {
        intent.getStringExtra("BookID")!!
    } //책 정보


    private val userReviewAdapter by lazy {
        UserReviewAdapter(this)
    }
    private val searchViewModel by lazy {
        ViewModelProvider(this,
                SearchViewModel.Factory(this)
        )[SearchViewModel::class.java]
    }
    //책 리뷰 정보를 가져오는 뷰모델

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            setUI()
            lifecycleOwner = this@BookDetailActivity
            tvLink.setOnClickListener {
                //구매링크로 연결
            }
        }
    }

    private fun setUI() {
        val stateLiveData = MutableLiveData<Book>()
        val reviewList = ArrayList<Feed>()
        searchViewModel.loadBookDetail(bookId, stateLiveData, reviewList)
        binding.apply {
            //데이터를 가져온 후
            stateLiveData.observe(this@BookDetailActivity) { book ->
                if (book != Book()) {
                    this.book = book
                    executePendingBindings()
                } else {

                }
            }
        }
    }

    fun setAdapter() {
        binding.apply {
            mRecyclerView.adapter = userReviewAdapter
        }
    }
}