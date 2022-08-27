package com.example.bookworm.bottomMenu.feed.items

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import com.example.bookworm.R
import com.example.bookworm.bottomMenu.feed.views.FeedItemViewHolder

class FeedAdapter(c: Context) :
    ListAdapter<Feed, RecyclerView.ViewHolder>(MyDiffCallback) {
    var context: Context

    init {
        context = c
    }


    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        when (viewType) {
            1 -> {
                view = inflater.inflate(R.layout.fragment_feed_item, parent, false)
                return FeedItemViewHolder(view, context)
            }
            else -> {
                view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                return LoadingViewHolder(view)
            }
        }
    }

    //각 아이템에 고유값을 부여하여, 리스트가 갱신될때, 이미 있는 아이템이라면 갱신하지 않음.
    override fun getItemId(position: Int): Long {
        return currentList[position].hashCode().toLong()
    }

    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val safePosition = holder.bindingAdapterPosition
        if (holder is FeedItemViewHolder) {
            val item = currentList[safePosition]
            holder.setVisibillity(item.imgurl != null)
            holder.setItem(item)
        } else if (holder is LoadingViewHolder) {
            showLoadingView(holder, safePosition)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    //로딩바 클래스
    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //
    }

    override fun getItemViewType(pos: Int): Int {
        return if (currentList[pos].feedID != null) 1 else 2
    }

    //값 업데이트를 위한 비교 콜백
    object MyDiffCallback : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(
            oldItem: Feed,
            newItem: Feed
        ): Boolean {
            return oldItem.feedID == newItem.feedID
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Feed,
            newItem: Feed
        ): Boolean {
            return oldItem == newItem
        }

    }
}


