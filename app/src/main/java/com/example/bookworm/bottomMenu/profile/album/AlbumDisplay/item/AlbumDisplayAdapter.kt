package com.example.bookworm.bottomMenu.profile.album.AlbumDisplay.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.BottomMenu.Feed.ViewHolders.FeedItemVIewHolder
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.profile.album.AlbumData
import com.example.bookworm.bottomMenu.profile.album.AlbumDisplay.view.AlbumDisplayViewHolder

class AlbumDisplayAdapter(val context: Context) :
    ListAdapter<AlbumData, RecyclerView.ViewHolder>(MyDiffCallback) {
    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        when (viewType) {
            1 -> {
                view = inflater.inflate(R.layout.fragment_album_item, parent, false)
                return FeedItemVIewHolder(view, context)
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
        val safePosition = holder.adapterPosition
        if (holder is AlbumDisplayViewHolder) {
            val item = currentList[safePosition]
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
        return if (currentList[pos].albumId != null) 1 else 2
    }

    //값 업데이트를 위한 비교 콜백
    object MyDiffCallback : DiffUtil.ItemCallback<AlbumData>() {


        override fun areItemsTheSame(oldItem: AlbumData, newItem: AlbumData): Boolean {
            return oldItem.albumId == newItem.albumId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: AlbumData, newItem: AlbumData): Boolean {
            return oldItem == newItem
        }

    }
}