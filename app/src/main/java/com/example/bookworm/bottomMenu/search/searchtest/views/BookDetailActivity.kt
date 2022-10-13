package com.example.bookworm.bottomMenu.search.searchtest.views

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.LoadState
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.feed.FeedViewModel
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.bottomMenu.search.searchtest.modules.SearchViewModel
import com.example.bookworm.databinding.LayoutBookSearchDetailBinding

class BookDetailActivity : AppCompatActivity() {
    val binding by lazy {
        DataBindingUtil.setContentView<LayoutBookSearchDetailBinding>(this, R.layout.layout_book_search_detail)
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
        binding.lifecycleOwner = this@BookDetailActivity
        setUI()
    }

    private fun setUI() {
        val stateLiveData = MutableLiveData<Book>()
        val reviewList = ArrayList<Feed>()
        searchViewModel.loadBookDetail(bookId, stateLiveData, reviewList)
        binding.apply {
            //데이터를 가져온 후
            stateLiveData.observe(this@BookDetailActivity) { book ->
                this.book = book
                executePendingBindings()

                if (book == Book()) Toast.makeText(this@BookDetailActivity, "정보를 로드하는데 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            }
            tvLink.setOnClickListener {
                //구매링크로 연결
            }
            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    fun setAdapter() {
        binding.apply {
            mRecyclerView.adapter = userReviewAdapter
        }
    }
}