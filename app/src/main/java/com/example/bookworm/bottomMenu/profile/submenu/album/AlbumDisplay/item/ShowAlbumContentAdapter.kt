package com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.item

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.bottomMenu.feed.Feed
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumCreate.view.AddAlbumPostViewholder
import com.example.bookworm.bottomMenu.profile.submenu.album.AlbumDisplay.view.ShowAlbumContentItemViewHolder
import com.example.bookworm.databinding.SubactivityCreatealbumcontentAddalbumpostLayoutBinding
import com.example.bookworm.databinding.SubactivityShowalbumcontentItemBinding

class ShowAlbumContentAdapter(val context: Context) :
    ListAdapter<Feed, RecyclerView.ViewHolder>(MyDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when(viewType){
            0->{
                var binding =  SubactivityShowalbumcontentItemBinding.inflate(inflater)
                return ShowAlbumContentItemViewHolder(binding, parent.context)
            }
            else->{
                var binding = SubactivityCreatealbumcontentAddalbumpostLayoutBinding.inflate(inflater)
                return AddAlbumPostViewholder(binding,parent.context)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ShowAlbumContentItemViewHolder) holder.setItem(currentList[position])
        else (holder as AddAlbumPostViewholder).setItem()
    }

    //데이터가 빈 경우 추가 할 수 있는 버튼으로 변경한다,
    override fun getItemViewType(pos: Int): Int {
        return if (currentList[pos]!=null) 0 else 1
    }
    //값 업데이트를 위한 비교 콜백
    object MyDiffCallback : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(
            oldItem: Feed,
            newItem: Feed
        ): Boolean {
            return oldItem.FeedID == newItem.FeedID
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