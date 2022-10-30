package com.example.bookworm.bottomMenu.search.searchtest.pages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.LoadState
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.BookAdapter
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.OnBookItemClickListener
import com.example.bookworm.bottomMenu.search.searchtest.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.searchtest.views.BookDetailActivity
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
import com.example.bookworm.databinding.FragmentSearchPageBookBinding
import com.example.bookworm.databinding.SubactivitySearchMainBinding

class BookSearchFragment : Fragment() {
    private val binding by lazy {
        FragmentSearchPageBookBinding.inflate(layoutInflater)
    }
    private val searchViewModel by lazy {
        ViewModelProvider(activity as SearchMainActivity,
                SearchViewModel.Factory(context as SearchMainActivity)
        )[SearchViewModel::class.java]
    }
    private val bookAdapter by lazy {
        BookAdapter(requireContext())
    }
    private var page = 1
    private var isEnd = false
    private var keyword = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setAdapter()
        loadData()
        return binding.root
    }


    fun setAdapter() {
        bookAdapter.addListener(object : OnBookItemClickListener {
            override fun onItemClick(holder: RecyclerView.ViewHolder, view: View, position: Int) {
                if (holder is BookAdapter.BookViewHolder) {
                    if (activity!!.callingActivity != null) {
                        val intent = activity!!.intent
                        intent.putExtra("bookData", bookAdapter.currentList[position])
                        activity!!.setResult(Activity.RESULT_OK, intent)
                        activity!!.finish()
                    } else {
                        val intent = Intent(requireContext(), BookDetailActivity::class.java)
                        intent.putExtra("BookID", bookAdapter.currentList[position].itemId)
                        this@BookSearchFragment.startActivity(intent)
                    }
                }
            }
        })
        binding.recyclerView.apply {
            adapter = bookAdapter
            with(this) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        val lastVisibleItemPosition =
                                layoutManager!!.findLastCompletelyVisibleItemPosition()
                        if ((lastVisibleItemPosition
                                        == bookAdapter.currentList.lastIndex) && lastVisibleItemPosition > 0)
                            loadData(false)
                    }
                })
            }
        }
    }

    fun loadData(isRefreshing: Boolean = true) {
        val stateLiveData = MutableLiveData<LoadState>()
        val resultList = ArrayList<Book>()
        if (!isRefreshing && !isEnd) {
            searchViewModel.loadBooks(stateLiveData = stateLiveData,
                    keyword = keyword,
                    page = page,
                    resultBookList = resultList
            )
        }
        searchViewModel.liveKeywordData.observe(context as SearchMainActivity) { keyword ->
            if (this.keyword != keyword) {
                page = 1
                isEnd = false
                this.keyword = keyword
                bookAdapter.submitList(emptyList())
                resultList.clear()
                searchViewModel.loadBooks(stateLiveData = stateLiveData,
                        keyword = keyword,
                        page = page,
                        resultBookList = resultList
                )
            }
        }
        stateLiveData.observe(context as SearchMainActivity) { state ->
            if (state == LoadState.Done) {
                var current = bookAdapter.currentList.toMutableList() //기존에 가지고 있던 아이템 목록
                //만약 현재 목록이 비어있지 않고, 마지막 아이템이 로딩 아이템 이라면 마지막 아이템을 제거
                if (current.isEmpty() && resultList.isEmpty()) {
                    Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    isEnd = true
                }
                if (current.isNotEmpty() && current.last().title == "") current.removeLast()
                //데이터의 끝에 다다르지 않았다면, 현재 목록에 불러온 아이템을 추가한다.
                if (!current.containsAll(resultList)) {
                    current.addAll(resultList)
                    current.add(Book())
                    page++
                }
                //데이터의 끝에 다다랐다면 끝이라는 것을 변수에 저장
                else isEnd = true

                //변경된 리스트를 어댑터에 반영
                bookAdapter.submitList(current)
            }
        }
    }
}