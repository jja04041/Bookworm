package com.example.bookworm.bottomMenu.search.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.bottomMenu.profile.views.ProfileInfoActivity
import com.example.bookworm.bottomMenu.search.adapters.FeedResultAdapter
import com.example.bookworm.bottomMenu.search.adapters.UserResultAdapter
import com.example.bookworm.bottomMenu.search.itemlisteners.OnFeedItemListener
import com.example.bookworm.bottomMenu.search.itemlisteners.OnUserItemListener
import com.example.bookworm.bottomMenu.search.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.views.SearchMainActivity
import com.example.bookworm.databinding.FragmentSearchPageFeedBinding

class FeedSearchFragment : Fragment() {
    private val binding by lazy {
        FragmentSearchPageFeedBinding.inflate(layoutInflater)
    }
    private val searchViewModel by lazy {
        ViewModelProvider(
            activity as SearchMainActivity,
            SearchViewModel.Factory(context as SearchMainActivity)
        )[SearchViewModel::class.java]
    }
    private var isEnd = false
    private var keyword = ""
    private val PAGE_SIZE = 5
    private val feedResultAdapter = FeedResultAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel.liveKeywordData.observe(context as SearchMainActivity) {
        }
        return binding.root
    }

    private fun setAdapter() {
        binding.mRecyclerView.apply {
            adapter = feedResultAdapter
            feedResultAdapter.addListener(object : OnFeedItemListener {
                override fun onClick(holder: FeedResultAdapter.FeedViewHolder, position: Int) {
//                    val intent = Intent(context, ProfileInfoActivity::class.java)
//                    intent.putExtra("fee", feedResultAdapter.currentList[position].feedID)
//                    startActivity(intent)
                }
            })
            with(this) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        val lastVisibleItemPosition =
                            layoutManager!!.findLastCompletelyVisibleItemPosition()
//                        if ((lastVisibleItemPosition
//                                    == feedResultAdapter.currentList.lastIndex) && lastVisibleItemPosition > 0 && !isEnd
//                        )
//                            loadData()
                    }
                })
            }
        }
    }
}