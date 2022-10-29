package com.example.bookworm.bottomMenu.search.searchtest.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.bottomMenu.search.searchtest.adapters.UserDataAdapter
import com.example.bookworm.bottomMenu.search.searchtest.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
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
    private val userAdapter = UserDataAdapter()
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