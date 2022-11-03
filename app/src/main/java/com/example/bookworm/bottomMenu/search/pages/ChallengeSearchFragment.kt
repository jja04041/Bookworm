package com.example.bookworm.bottomMenu.search.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.search.modules.SearchViewModel
import com.example.bookworm.bottomMenu.search.views.SearchMainActivity
import com.example.bookworm.databinding.FragmentSearchPageChallengeBinding

class ChallengeSearchFragment : Fragment() {
    private val binding by lazy {
        FragmentSearchPageChallengeBinding.inflate(layoutInflater)
    }
    private val searchViewModel by lazy {
        ViewModelProvider(activity as SearchMainActivity,
                SearchViewModel.Factory(context as SearchMainActivity)
        )[SearchViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        searchViewModel.liveKeywordData.observe(context as SearchMainActivity) {
        }
        return binding.root
    }
}