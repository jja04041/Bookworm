package com.example.bookworm.bottomMenu.search.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.search.itemlisteners.OnUserItemListener
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.LayoutUserItemBinding

class UserResultAdapter : ListAdapter<UserInfo, RecyclerView.ViewHolder>(Companion) {
    var listener: OnUserItemListener? = null

    companion object : DiffUtil.ItemCallback<UserInfo>() {
        override fun areItemsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UserInfo, newItem: UserInfo): Boolean {
            return oldItem.token == newItem.token

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        return when (viewType) {
            //사용자 아이템
            1 -> {
                view = inflater.inflate(R.layout.layout_user_item, parent, false)
                UserViewHolder(view)
            }
            //로딩뷰 아이템
            else -> {
                view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    fun addListener(listener: OnUserItemListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            if (this is UserViewHolder)
                setItem(currentList[position])
            else
                showLoadingView(this as LoadingViewHolder, position)
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutUserItemBinding.bind(itemView)
        fun setItem(user: UserInfo) {
            binding.btnFollow.isVisible = false
            Glide.with(itemView).load(user.profileimg).circleCrop().into(binding.ivProfileImg)
            binding.tvProfileID.text = user.username
            binding.root.setOnClickListener {
                listener!!.onClick(this,bindingAdapterPosition)
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {}

    //뷰타입 확인
    override fun getItemViewType(pos: Int): Int {
        return if (currentList[pos].token != "") 1 else 2
    }
}