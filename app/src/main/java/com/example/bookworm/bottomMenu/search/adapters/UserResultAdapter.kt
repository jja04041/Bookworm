package com.example.bookworm.bottomMenu.search.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.core.userdata.UserInfo

class UserResultAdapter : ListAdapter<UserInfo, RecyclerView.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<UserInfo>() {
        override fun areItemsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem.token == newItem.token

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}