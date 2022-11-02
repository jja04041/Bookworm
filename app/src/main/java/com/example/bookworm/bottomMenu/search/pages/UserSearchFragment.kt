package com.example.bookworm.bottomMenu.search.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.bottomMenu.search.adapters.UserResultAdapter
import com.example.bookworm.bottomMenu.search.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.views.SearchMainActivity
import com.example.bookworm.databinding.FragmentSearchPageUserBinding

class UserSearchFragment : Fragment() {
    private val searchViewModel by lazy {
        ViewModelProvider(
            activity as SearchMainActivity,
            SearchViewModel.Factory(context as SearchMainActivity)
        )[SearchViewModel::class.java]
    }
    private val binding by lazy {
        FragmentSearchPageUserBinding.inflate(layoutInflater)
    }
    private var page = 1
    private var isEnd = false
    private var keyword = ""
    private val userAdapter = UserResultAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        setAdapter()
        setUI()
        return binding.root
    }

    private fun setUI() {
        //검색어를 전달받은 후 처리하는 루틴을 작성한다.
        searchViewModel.liveKeywordData.observe(context as SearchMainActivity) {
        }
    }

    /** 어댑터 설정
     * */
    private fun setAdapter() {
        binding.mRecyclerView.apply {
            adapter = userAdapter
            with(this) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        val lastVisibleItemPosition =
                            layoutManager!!.findLastCompletelyVisibleItemPosition()
                        if ((lastVisibleItemPosition
                                    == userAdapter.currentList.lastIndex) && lastVisibleItemPosition > 0
                        )
//                            loadData(false)
                            TODO()
                    }
                })
            }
        }
    }
}