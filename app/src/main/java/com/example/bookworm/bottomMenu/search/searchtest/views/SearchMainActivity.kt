package com.example.bookworm.bottomMenu.search.searchtest.views

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.bottomMenu.search.searchtest.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.searchtest.pages.BookSearchFragment
import com.example.bookworm.bottomMenu.search.searchtest.adapters.SearchPageAdapter
import com.example.bookworm.databinding.SubactivitySearchMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class SearchMainActivity : AppCompatActivity() {
    private val pageAdapter by lazy {
        SearchPageAdapter(supportFragmentManager, lifecycle)
    }
    private val binding by lazy {
        SubactivitySearchMainBinding.inflate(layoutInflater)
    }
    private val searchViewModel by lazy {
        ViewModelProvider(this,
                SearchViewModel.Factory(this)
        )[SearchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.d("부모",callingActivity!!.className) // startresult인 경우에만 이게 활성화 됨. null여부에 따라 차이를 두면 될듯?
        if (callingActivity != null) //startresult로 액티비티가 열린 경우
        {
            binding.apply {
                supportFragmentManager
                        .beginTransaction()
                        .replace(container.id, BookSearchFragment())
                        .commitAllowingStateLoss()
                //이전에 검색한 데이터가 있는 경우
                if (intent.hasExtra("prevBook")) {
                    edtSearch.setText(intent.getParcelableExtra<Book>("prevBook")!!.title)
                    excuteSearch()
                }
            }

        } else {
            binding.apply {
                viewpager.isVisible = true
                tabLayout.isVisible = true
                container.isVisible = false
                viewpager.adapter = pageAdapter
                val pageList = listOf("도서", "피드", "챌린지", "사용자")
                TabLayoutMediator(tabLayout, viewpager, false, false) { tab, position ->
                    tab.apply {
                        text = pageList[position];
                        tag = position.toString()
                    }
                }.attach()
            }
        }
        //공통적으로 적용되는 것들.
        binding.apply {
            setContentView(root)
            viewpager.isVisible = callingActivity == null
            tabLayout.isVisible = callingActivity == null
            container.isVisible = callingActivity != null
            edtSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    excuteSearch()
                }
                false
            }
            btnBefore.setOnClickListener {
                finish()
            }
            btnTextClear.setOnClickListener {
                edtSearch.text.clear()
                edtSearch.requestFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edtSearch,0)
            }

        }


    }

    private fun excuteSearch() {
        binding.apply {
            searchViewModel.liveKeywordData.value = edtSearch.text.toString()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(edtSearch.windowToken, 0)
            edtSearch.clearFocus()
        }
    }
}