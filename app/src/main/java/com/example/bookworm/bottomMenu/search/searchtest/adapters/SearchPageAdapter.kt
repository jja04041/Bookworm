package com.example.bookworm.bottomMenu.search.searchtest.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookworm.bottomMenu.search.searchtest.pages.BookSearchFragment
import com.example.bookworm.bottomMenu.search.searchtest.pages.ChallengeSearchFragment
import com.example.bookworm.bottomMenu.search.searchtest.pages.PostSearchFragment
import com.example.bookworm.bottomMenu.search.searchtest.pages.UserSearchFragment

class SearchPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
    private val pageList = listOf(
            BookSearchFragment(),
            PostSearchFragment(),
            ChallengeSearchFragment(),
            UserSearchFragment()
    )

    override fun getItemCount() = pageList.size
    fun getPage(position: Int) = pageList[position]

    override fun createFragment(position: Int) = pageList[position]
}