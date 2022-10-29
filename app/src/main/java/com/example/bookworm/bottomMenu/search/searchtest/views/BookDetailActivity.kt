package com.example.bookworm.bottomMenu.search.searchtest.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.LoadState
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.feed.SubActivityCreatePost
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.bottomMenu.search.searchtest.adapters.UserReviewAdapter
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.bottomMenu.search.searchtest.modules.SearchViewModel
import com.example.bookworm.core.userdata.UserInfo
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
    private val userInfoViewModel by lazy {
        ViewModelProvider(this,
                UserInfoViewModel.Factory(this)
        )[UserInfoViewModel::class.java]
    }
    val reviewList = ArrayList<Any>()
    private var isReviewEnd = false
    private val RVPAGESIZE = 5
    //유저 정보를 가져오는 뷰모델


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setAdapter()
        setUI()
    }

    private fun setUI() {
        val stateLiveData = MutableLiveData<Book>()
        val tmpList = ArrayList<Feed>()
        searchViewModel.loadBookDetail(bookId, stateLiveData, tmpList, RVPAGESIZE)
        binding.apply {
            //데이터를 가져온 후
            stateLiveData.observe(this@BookDetailActivity) { book ->
                reviewList.add(book)
                if (book == Book()) Toast.makeText(this@BookDetailActivity, "정보를 로드하는데 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                //작성된 리뷰가 없거나, 최대 로드 가능한 수보다 적은 경우
                if (tmpList.isEmpty() || tmpList.size < RVPAGESIZE) {
                    isReviewEnd = true
                    llEmptyReview.isVisible = tmpList.isEmpty()
                }
                //만약 임시저장 리스트에 담긴 데이터가 있다면
                if (tmpList.isNotEmpty()) {
                    reviewList.addAll(tmpList)
                    if (!isReviewEnd) reviewList.add(Feed()) //추가로 더 불러올 수 있는 경우
                }
                userReviewAdapter.submitList(reviewList) //현재 어답터에 정보를 반영
                showShimmer(false)
            }
            btnPurchase.setOnClickListener {
                //구매링크로 연결
                Intent(Intent.ACTION_VIEW).apply {
                    this.data = Uri.parse((reviewList[0] as Book).purchaseLink)
                    this@BookDetailActivity.startActivity(this)
                }
            }
            btnFeedCreate.setOnClickListener {
                //현재 사용자 정보를 가지고 와서 인텐트로 넘긴다.
                MutableLiveData<UserInfo>().apply {
                    userInfoViewModel.getUser(null, this, false)
                    this.observe(this@BookDetailActivity) { userInfo ->
                        Intent(this@BookDetailActivity, SubActivityCreatePost::class.java).apply {
                            this.putExtra("BookData", reviewList[0] as Book)
                            this.putExtra("mainUser", userInfo)
                            this@BookDetailActivity.startActivity(this)
                        }
                    }
                }

            }
            //뒤로가기
            ivBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun loadMoreReviews() {
        val tmpList = ArrayList<Feed>()
        val liveData = MutableLiveData<LoadState>()
        searchViewModel.loadBookReview(bookId, liveData, tmpList, RVPAGESIZE)
        liveData.observe(this@BookDetailActivity) {
            if (it == LoadState.Done) {
                if (tmpList.isEmpty() || tmpList.size < RVPAGESIZE) isReviewEnd = true
                if (tmpList.isNotEmpty()) {
                    reviewList.removeLast()
                    reviewList.addAll(tmpList)
                    if (!isReviewEnd) reviewList.add(Feed())
                    userReviewAdapter.submitList(reviewList.toMutableList())
                }
            }
        }
    }

    fun setAdapter() {
        binding.apply {
            mRecyclerView.adapter = userReviewAdapter
            mRecyclerView.itemAnimator = null //리사이클러뷰 애니메이션 제거
            mRecyclerView.isNestedScrollingEnabled = false
            //스와이프하여 새로고침
            with(mRecyclerView) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        val lastVisibleItemPosition =
                                layoutManager!!.findLastCompletelyVisibleItemPosition()
                        if ((lastVisibleItemPosition
                                        == userReviewAdapter.currentList.lastIndex - 1) && lastVisibleItemPosition > 0 && !isReviewEnd)
                            loadMoreReviews()
                    }
                })
            }
        }
    }

    //shimmer을 켜고 끄고 하는 메소드
    private fun showShimmer(bool: Boolean) {
        binding.llSearch.isVisible = !bool
        if (bool) binding.SFLSearch.startShimmer() else binding.SFLSearch.stopShimmer()
        binding.SFLSearch.isVisible = bool
    }
}