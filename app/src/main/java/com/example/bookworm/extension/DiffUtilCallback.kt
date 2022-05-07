package com.example.bookworm.extension

import androidx.recyclerview.widget.DiffUtil
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.bottomMenu.Feed.comments.Comment
import com.example.bookworm.bottomMenu.Feed.items.Feed
import java.util.ArrayList

class DiffUtilCallback(private val oldList: ArrayList<*>, private val newList: ArrayList<*>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is Comment && newItem is Comment) {
            return oldItem.commentID === newItem.commentID
        } else if (oldItem is Feed && newItem is Feed) {
            return oldItem.feedID === newItem.feedID
        } else if (oldItem is UserInfo && newItem is UserInfo) {
            return oldItem.token === newItem.token
        }
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] ==newList[newItemPosition]
    }
}