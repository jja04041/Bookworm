package com.example.bookworm.bottomMenu.search.searchtest.bookitems

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.databinding.LayoutItemLoadingBinding
import com.example.bookworm.databinding.SearchFavoriteitemBinding
import com.example.bookworm.databinding.SearchRecyclerviewItemBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.ThreeBounce

class BookAdapter(val context: Context) : ListAdapter<Book, RecyclerView.ViewHolder>(Companion) {
    var listener: OnBookItemClickListener? = null

    companion object : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.itemId == newItem.itemId
        }
    }

    @JvmName("setListener1")
    fun setListener(listener: OnBookItemClickListener) {
        this.listener = listener
    }

    override fun getItemId(position: Int): Long {
        return position.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            0 -> {
                val view = inflater.inflate(R.layout.search_favoriteitem, parent, false)
                RecomBookViewHolder(view)
            }
            1 -> {
                val view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                LoadingBookViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.search_recyclerview_item, parent, false)
                BookViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val safePos = holder.bindingAdapterPosition
        when (holder) {
            is RecomBookViewHolder ->
                holder.bindItem(currentList[safePos])
            is BookViewHolder ->
                holder.bindItem(currentList[safePos])
            else -> {
                showLoadingView(holder as LoadingBookViewHolder, safePos)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].isRecommend) 0 //인기도서
        else if (currentList[position].itemId == "") 1 //로딩바
        else 2 //일반 책
    }

    inner class RecomBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by lazy {
            SearchFavoriteitemBinding.bind(itemView)
        }

        fun bindItem(item: Book) {
            binding.apply {
                tvBookTitle.text = item.title
                val circularProgressDrawable = CircularProgressDrawable(itemView.context)
                circularProgressDrawable.apply {
                    strokeWidth = 5f
                    centerRadius = 30f
                    start()
                    Glide.with(itemView.context)
                            .load(item.imgUrl)
                            .placeholder(this)
                            .into(ivBook)
                }

                root.setOnClickListener { view ->
                    val pos = bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION)
                        listener!!.onItemClick(this@RecomBookViewHolder, view, pos)
                }
            }
        }
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding by lazy {
            SearchRecyclerviewItemBinding.bind(itemView)
        }

        fun bindItem(item: Book) {
            binding.apply {
                tvAuthor.text = item.author
                tvDescription.text = item.content
                tvPublisher.text = item.publisher
                tvTitle.text = item.title
                val circularProgressDrawable = CircularProgressDrawable(itemView.context)
                circularProgressDrawable.apply {
                    strokeWidth = 5f
                    centerRadius = 30f
                    start()
                    Glide.with(itemView.context)
                            .load(item.imgUrl)
                            .placeholder(this)
                            .into(ivThumb)
                }
                root.setOnClickListener { v ->
                    val pos = bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION)
                        listener!!.onItemClick(this@BookViewHolder, v, pos)
                }
            }

        }
    }

    inner class LoadingBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    private fun showLoadingView(viewHolder: BookAdapter.LoadingBookViewHolder, position: Int) {
        val binding: LayoutItemLoadingBinding = LayoutItemLoadingBinding.bind(viewHolder.itemView)
        val Circle: Sprite = ThreeBounce()
        Circle.animationDelay = 0
        binding.progressBar.setIndeterminateDrawable(Circle)
    }
}