package com.example.bookworm.bottomMenu.profile.Album.view

import android.content.Context
import android.view.View
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.databinding.FragmentRecordItemBinding

class AlbumViewHolder(itemView: View, val context: Context,val tracker: SelectionTracker<Long>) : RecyclerView.ViewHolder(itemView) {
    var binding: FragmentRecordItemBinding? = FragmentRecordItemBinding.bind(itemView)
    lateinit var feed: Feed
    fun setItem(item: Feed) {
        feed = item
        binding!!.feedBookTitle.setText(item.book.title)
        binding!!.tvFeedtext.setText(item.feedText)
        Glide.with(binding!!.root).load(item.book.img_url).into(binding!!.ivBookThumb)
        binding!!.tvFeedDate.setText(item.date)
        binding!!.frame.isActivated = tracker!!.isSelected(bindingAdapterPosition.toLong())
    }

    fun getItem(): ItemDetailsLookup.ItemDetails<Long> =
        object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = bindingAdapterPosition
            override fun getSelectionKey(): Long = bindingAdapter!!.getItemId(bindingAdapterPosition)
        }
}
