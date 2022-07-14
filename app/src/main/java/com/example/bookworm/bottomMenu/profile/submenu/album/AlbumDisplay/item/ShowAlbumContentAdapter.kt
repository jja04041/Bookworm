package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.view.ShowAlbumContentItemViewHolder
import com.example.bookworm.databinding.FragmentRecordItemBinding
import com.example.bookworm.databinding.SubactivityShowalbumcontentItemBinding

class ShowAlbumContentAdapter(val context: Context) :
    ListAdapter<Feed, ShowAlbumContentItemViewHolder>(MyDiffCallback) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShowAlbumContentItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
//        var binding =  FragmentRecordItemBinding.inflate(inflater)
        var binding =  SubactivityShowalbumcontentItemBinding.inflate(inflater)
        return ShowAlbumContentItemViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ShowAlbumContentItemViewHolder, position: Int) {
        holder.setItem(currentList[position])
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