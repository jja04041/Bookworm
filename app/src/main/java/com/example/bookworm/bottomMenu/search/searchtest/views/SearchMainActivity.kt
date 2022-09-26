package com.example.bookworm.bottomMenu.search.searchtest.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.search.searchtest.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.searchtest.pages.SearchPageAdapter
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
        binding.apply {
            setContentView(root)
            viewpager.adapter = pageAdapter
            val pageList = listOf("도서", "피드", "챌린지", "사용자")
            TabLayoutMediator(tabLayout, viewpager,false,false) { tab, position ->
                tab.apply {
                    text = pageList[position];
                    tag = position.toString()
                }
            }.attach()


            edtSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchViewModel.liveKeywordData.value = edtSearch.text.toString()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(edtSearch.windowToken, 0)
                    edtSearch.clearFocus()
                }
                false
            }
            btnBefore.setOnClickListener {
                finish()
            }
            btnSearch.setOnClickListener {
                searchViewModel.liveKeywordData.value = edtSearch.text.toString()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(edtSearch.windowToken, 0)
                edtSearch.clearFocus()
            }
        }
    }
}