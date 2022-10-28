package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view.AlbumProcessViewHolder
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view.ShowFeedListForCreateAlbum
import com.example.bookworm.databinding.SubactivityShowalbumcontentItemBinding
import kotlin.collections.ArrayList

class AlbumProcessAdapter(val context: Context) :
        ListAdapter<Feed, AlbumProcessViewHolder>(MyDiffCallback) {
    val parentActivity = context as ShowFeedListForCreateAlbum
    var binding: SubactivityShowalbumcontentItemBinding? = null
    var onItemClickListener: ((Feed) -> Unit)? = null
    val selectedFeed = MutableLiveData<ArrayList<Feed>>()

    init {
        setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumProcessViewHolder {
        val inflater = LayoutInflater.from(parentActivity)
        binding = SubactivityShowalbumcontentItemBinding.inflate(inflater)
        selectedFeed.value = ArrayList()
        return AlbumProcessViewHolder(binding!!, context)
    }

    override fun onBindViewHolder(holderProcess: AlbumProcessViewHolder, position: Int) {
        val safePosition = holderProcess.bindingAdapterPosition
        val item = currentList[safePosition]
        holderProcess.setItem(item)

    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun applySelection(binding: SubactivityShowalbumcontentItemBinding, feed: Feed) {
        if (selectedFeed.value!!.contains(feed)) {
            selectedFeed.value = selectedFeed.value!!.apply {
                remove(feed)
            }
            changeBackground(binding, R.color.white)
        } else {
            selectedFeed.value = selectedFeed.value!!.apply {
                add(feed)
            }
            changeBackground(binding, R.color.purple_200)
        }
    }

    private fun changeBackground(binding: SubactivityShowalbumcontentItemBinding, resId: Int) {
        binding.frame.setBackgroundColor(ContextCompat.getColor(binding.root.context, resId))
    }

    fun setItemClickListener(listener: (Feed) -> Unit) {
        onItemClickListener = listener
    }

    //값 업데이트를 위한 비교 콜백
    object MyDiffCallback : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(
                oldItem: Feed,
                newItem: Feed,
        ): Boolean {
            return oldItem.feedID == newItem.feedID
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
                oldItem: Feed,
                newItem: Feed,
        ): Boolean {
            return oldItem == newItem
        }
    }
}