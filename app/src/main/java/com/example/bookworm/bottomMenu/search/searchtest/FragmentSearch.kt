package com.example.bookworm.bottomMenu.search.searchtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.bookworm.BookWorm
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.BookAdapter
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.OnBookItemClickListener
import com.example.bookworm.bottomMenu.search.searchtest.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchDetailActivity
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.FragmentSearchBinding

class FragmentSearch : Fragment() {
    private val binding by lazy {
        FragmentSearchBinding.inflate(layoutInflater)
    }

    //뷰모델에 데이터 가공과 처리를 위임한다.
    // -> 프레그먼트에서는 단순히 UI 세팅만 담당하도록 하여 부담을 줄인다.
    private val searchViewModel by lazy {
        ViewModelProvider(activity as MainActivity,
                SearchViewModel.Factory(context as MainActivity)
        )[SearchViewModel::class.java]
    }
    private val bookAdapter by lazy {
        BookAdapter(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding.apply {
            edtSearchBtn.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val intent = Intent(activity, SearchMainActivity::class.java)
                    startActivity(intent)
                    binding.edtSearchBtn.clearFocus()
                }
            }
            favRecyclerView.adapter = bookAdapter
            bookAdapter.setListener(object : OnBookItemClickListener {
                override fun onItemClick(holder: RecyclerView.ViewHolder, view: View, position: Int) {
                    if (holder is BookAdapter.BookViewHolder) {
                        val intent = Intent(context, SearchDetailActivity::class.java)
                        intent.putExtra("Book", bookAdapter.currentList[position])
                        startActivity(intent)
                    }
                }
            })

            tv1stNickname.setText("성근")
            tv2ndNickname.setText("이규연")
            tv3rdNickname.setText("조세현")
            tv1stReadCount.setText("15권")
            tv2ndReadCount.setText("11권")
            tv3rdReadCount.setText("5권")
            Glide.with(requireContext()).load("https://k.kakaocdn.net/dn/b8DtBK/btqRorUTUCy/w10D6Zn5IMsop8v2BJY5VK/img_640x640.jpg").circleCrop().into(img1stProfile)
            Glide.with(requireContext()).load("https://lh3.googleusercontent.com/a-/ACNPEu_H7Agfvs-q7d7eQU3IX8dbha5l4Awq9vgm5LoFXSI=s96-c").circleCrop().into(img2ndProfile)
            Glide.with(requireContext()).load("https://k.kakaocdn.net/dn/qYA2k/btrJuRyOoVk/KsChdA2fMXS2xrbeArhBtk/img_640x640.jpg").circleCrop().into(img3rdProfile)
        }
        loadRecommendBooks();

        return binding.root
    }

    fun setRanking() {
        var userList = ArrayList<UserInfo>()
        var bwList = ArrayList<BookWorm>()

    }

    private fun loadRecommendBooks() {
        val liveData = MutableLiveData<SearchViewModel.State>()
        var resultList = ArrayList<Book>()
        searchViewModel.loadPopularBook(liveData, resultList)
        showShimmer(true)
        liveData.observe(context as MainActivity) { state ->
            if (state == SearchViewModel.State.Done) {
                bookAdapter.submitList(resultList)
                showShimmer(false)
            }

        }
    }

    //shimmer을 켜고 끄고 하는 메소드
    private fun showShimmer(bool: Boolean) {
        binding.apply {
            if (bool) SFLSearchbook.showShimmer(bool)
            llSearchbook.isVisible = !bool
            SFLSearchbook.isVisible = bool
        }
    }
}