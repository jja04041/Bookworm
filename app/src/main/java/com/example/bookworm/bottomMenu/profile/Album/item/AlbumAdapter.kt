package com.example.bookworm.bottomMenu.profile.Album.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.Feed.items.Feed
import com.example.bookworm.bottomMenu.profile.Album.view.AlbumViewHolder
import com.example.bookworm.bottomMenu.profile.Album.view.CreateAlbumActivity

class AlbumAdapter(val context: Context) :
    ListAdapter<Feed, AlbumViewHolder>(MyDiffCallback) {
    var tracker: SelectionTracker<Long>? = null
    val parentActivity = context as CreateAlbumActivity

    init {
        setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = LayoutInflater.from(parentActivity)
        val view: View
        view = inflater.inflate(R.layout.fragment_record_item, parent, false)
        return AlbumViewHolder(view, context,tracker!!)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val safePosition = holder.bindingAdapterPosition
        val item = currentList[safePosition]
        holder.setItem(item)
    }

    override fun getItemId(position: Int): Long = position.toLong()


    class ItemsDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as AlbumViewHolder).getItem()
            }
            return null
        }
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