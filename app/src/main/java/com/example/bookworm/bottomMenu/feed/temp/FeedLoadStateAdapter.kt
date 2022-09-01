package com.example.bookworm.bottomMenu.feed.temp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.databinding.LayoutItemLoadingBinding

class FeedLoadStateAdapter(private val retry:() -> Unit) : LoadStateAdapter<FeedLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_loading, parent, false)
    ) {
        private val binding = LayoutItemLoadingBinding.bind(itemView)
        fun bind(loadState: LoadState) {
            binding.progressBar.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) = holder.bind(loadState)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState)
            : LoadStateViewHolder = LoadStateViewHolder(parent)

}