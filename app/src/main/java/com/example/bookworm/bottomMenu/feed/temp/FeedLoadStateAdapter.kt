package com.example.bookworm.bottomMenu.feed.temp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.databinding.LayoutItemLoadingBinding
import com.github.ybq.android.spinkit.style.ThreeBounce

class FeedLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<FeedLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_loading, parent, false)
    ) {
        private val binding = LayoutItemLoadingBinding.bind(itemView)
        fun bind(loadState: LoadState) {
            binding.apply {
                val Circle = ThreeBounce()
                Circle.animationDelay = 0
                progressBar.setIndeterminateDrawable(Circle)
                progressBar.isVisible = loadState is LoadState.Loading
            }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState)
    = holder.bind(loadState)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState)
            : LoadStateViewHolder = LoadStateViewHolder(parent)

}