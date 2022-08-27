package com.example.bookworm.bottomMenu.feed.temp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.bookworm.databinding.TmpActivityFeedBinding
import com.example.bookworm.databinding.TmpcanactivityBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FeedActivity : AppCompatActivity() {
    private lateinit var dataBinding: TmpcanactivityBinding

    //viewModels<뷰모델이름>() 사용 시, ViewModelProvider 없이 지연 사용 가능


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = TmpcanactivityBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
            supportFragmentManager.beginTransaction().replace(dataBinding.container.id,fragmentFeed()).commitAllowingStateLoss()
    }

}