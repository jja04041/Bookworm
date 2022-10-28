package com.example.bookworm.bottomMenu.search.searchtest.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.search.searchtest.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.searchtest.views.SearchMainActivity
import com.example.bookworm.databinding.FragmentSearchPageUserBinding

class UserSearchFragment : Fragment() {
    private val searchViewModel by lazy {
        ViewModelProvider(activity as SearchMainActivity,
                SearchViewModel.Factory(context as SearchMainActivity)
        )[SearchViewModel::class.java]
    }
    private val binding by lazy {
        FragmentSearchPageUserBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        searchViewModel.liveKeywordData.observe(context as SearchMainActivity) {
        }
        return binding.root
    }
}