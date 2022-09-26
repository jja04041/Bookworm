package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view.AlbumProcessViewHolder
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view.CreateAlbumActivity
import com.example.bookworm.databinding.FragmentRecordItemBinding
import kotlin.collections.ArrayList

class AlbumProcessAdapter(val context: Context) :
    ListAdapter<Feed, AlbumProcessViewHolder>(MyDiffCallback) {
    val parentActivity = context as CreateAlbumActivity
    var binding: FragmentRecordItemBinding? = null
    var onItemClickListener: ((Feed) -> Unit)? = null
    var selectedFeed: ArrayList<Feed> = ArrayList()

    init {
        setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumProcessViewHolder {
        val inflater = LayoutInflater.from(parentActivity)
        binding = FragmentRecordItemBinding.inflate(inflater)

        return AlbumProcessViewHolder(binding!!, context)
    }

    override fun onBindViewHolder(holderProcess: AlbumProcessViewHolder, position: Int) {
        val safePosition = holderProcess.bindingAdapterPosition
        val item = currentList[safePosition]
        holderProcess.setItem(item)

    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun applySelection(binding: FragmentRecordItemBinding, feed: Feed) {
        if (selectedFeed.contains(feed)) {
            selectedFeed.remove(feed)
            changeBackground(binding, R.color.white)
        } else {
            selectedFeed.add(feed)
            changeBackground(binding, R.color.purple_200)
        }
    }

    private fun changeBackground(binding: FragmentRecordItemBinding, resId: Int) {
        binding.frame.setBackgroundColor(ContextCompat.getColor(binding.root.context, resId))
    }

    fun setItemClickListener(listener: (Feed) -> Unit) {
        onItemClickListener = listener
    }

    //값 업데이트를 위한 비교 콜백
    object MyDiffCallback : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(
            oldItem: Feed,
            newItem: Feed
        ): Boolean {
            return oldItem.FeedID==newItem.FeedID
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