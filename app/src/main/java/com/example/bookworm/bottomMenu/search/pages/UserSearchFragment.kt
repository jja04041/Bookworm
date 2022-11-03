package com.example.bookworm.bottomMenu.search.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.LoadState
import com.example.bookworm.bottomMenu.profile.views.ProfileInfoActivity
import com.example.bookworm.bottomMenu.search.adapters.UserResultAdapter
import com.example.bookworm.bottomMenu.search.itemlisteners.OnUserItemListener
import com.example.bookworm.bottomMenu.search.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.views.SearchMainActivity
import com.example.bookworm.core.userdata.UserInfo
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
    private var isEnd = false
    private var keyword = ""
    private val PAGE_SIZE = 5
    private val userAdapter = UserResultAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.llEmptySearchUser.isVisible = false
        setAdapter()
        loadData()
        return binding.root
    }


    /** 어댑터 설정
     * */
    private fun setAdapter() {
        binding.mRecyclerView.apply {
            adapter = userAdapter
            userAdapter.addListener(object : OnUserItemListener {
                override fun onClick(holder: UserResultAdapter.UserViewHolder, position: Int) {
                    val intent = Intent(context, ProfileInfoActivity::class.java)
                    intent.putExtra("userID", userAdapter.currentList[position].token)
                    startActivity(intent)
                }
            })
            with(this) {
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        val lastVisibleItemPosition =
                            layoutManager!!.findLastCompletelyVisibleItemPosition()
                        if ((lastVisibleItemPosition
                                    == userAdapter.currentList.lastIndex) && lastVisibleItemPosition > 0 && !isEnd
                        )
                            loadData()
                    }
                })
            }
        }
    }

    private fun loadData() {
        val stateLiveData = MutableLiveData<LoadState>()
        val resultList = emptyList<UserInfo>().toMutableList()
        if (!isEnd && keyword != "") {
            searchViewModel.loadSearchedUser(
                stateLiveData = stateLiveData,
                keyword = keyword,
                userList = resultList,
                page = PAGE_SIZE
            )
        }
        searchViewModel.liveKeywordData.observe(viewLifecycleOwner) { keyword ->
            if (this.keyword != keyword && keyword != "") {
                init(resultList = resultList)
                searchViewModel.loadSearchedUser(keyword, stateLiveData, resultList, PAGE_SIZE)
            }
        }
        stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                LoadState.Done -> {
                    val current = userAdapter.currentList.toMutableList()

                    //검색결과가 없는 경우
                    if (current.isEmpty() && resultList.isEmpty()) {
                        binding.llEmptySearchUser.isVisible = true
                        isEnd = true
                    } else if (current.isNotEmpty() && current.last().token == "") current.removeLast()

                    if (current.size < PAGE_SIZE) isEnd = true
                    if (!current.containsAll(resultList)) {
                        binding.llEmptySearchUser.isVisible = false
                        current.addAll(resultList)
                        if (!isEnd) current.add(UserInfo())
                    } else isEnd = true

                    //변경된 결과값을 어댑터에 반영
                    userAdapter.submitList(current)
                }
                LoadState.Error -> {
                    binding.llEmptySearchUser.isVisible = true
                }
                else -> {}

            }
        }

    }

    private fun init(keyword: String = "", resultList: MutableList<UserInfo>) {
        isEnd = false
        this.keyword = keyword
        userAdapter.submitList(emptyList())
        resultList.clear()
    }
}