package com.example.bookworm.bottomMenu.search.searchtest.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.feed.FeedViewModel
import com.example.bookworm.bottomMenu.search.searchtest.bookitems.Book
import com.example.bookworm.databinding.LayoutBookSearchDetailBinding

class SearchDetailActivity :AppCompatActivity() {
    val binding by lazy {
        LayoutBookSearchDetailBinding.inflate(layoutInflater)
    }
    val bookData by lazy {
        intent.getParcelableExtra<Book>("Book")
    }
    val feedViewModel by lazy {
        ViewModelProvider(this,
                FeedViewModel.Factory(this)
        )[FeedViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
}