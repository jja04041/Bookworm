package com.example.bookworm.Follow

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.bookworm.Feed.ViewHolders.ItemViewHolder
import com.example.bookworm.R
import com.example.bookworm.User.UserInfo
import com.example.bookworm.User.followCounter
import com.example.bookworm.databinding.LayoutUserItemBinding

class FollowerAdapter(data: ArrayList<UserInfo>?, val context: Context,val nowUserInfo: UserInfo) : Adapter<RecyclerView.ViewHolder>() {
    var UserList: ArrayList<UserInfo> = ArrayList()
    init{
        if(data!=null) UserList.addAll(data)
    }
    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var inflater: LayoutInflater = LayoutInflater.from(context)
        var view: View
        when (viewType) {
            1 -> {
                view = inflater.inflate(R.layout.layout_user_item, parent, false)
                return ItemViewHolder(view, context,nowUserInfo)
            }
            else -> {
                view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                return LoadingViewHolder(view)
            }
        }


    }

    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var position = holder.adapterPosition
        holder.apply {
            if (holder is ItemViewHolder) {
                var userInfo = UserList!!.get(position)
                holder.setItem(userInfo)
            } else if (holder is LoadingViewHolder) {
                showLoadingView(holder, position)
            }
        }
    }

    fun setData(data: ArrayList<UserInfo>?) {
        UserList.clear()
        UserList.addAll(data!!)
    }

    //뷰타입 확인
    override fun getItemViewType(pos: Int): Int {
        return if (UserList.get(pos).token != null) 1 else 2
    }

    override fun getItemCount(): Int {
        return UserList.size
    }

    private class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //
    }

    class ItemViewHolder(itemView: View, context: Context?,val nowUserInfo: UserInfo) : RecyclerView.ViewHolder(itemView) {
        var context = context//전달된 context
        var binding = LayoutUserItemBinding.bind(itemView)
        var v = false
        fun setItem(item: UserInfo?) {
            Glide.with(context!!).load(item!!.profileimg).into(binding.ivProfileImg)
            binding.tvProfileID.setText(item.username)
            if (item.isFollowed) following()
            else unfollowing()
            binding.btnFollow.setOnClickListener({
                if (v) {
                    followCounter().unfollow(item,nowUserInfo,context)
                    unfollowing()
                }
                else {
                    followCounter().follow(item,nowUserInfo,context)
                    following()
                }
            })
        }
        fun following() {
            binding.btnFollow.setBackgroundColor(Color.BLUE)
            binding.btnFollow.setText("팔로잉")
            v = true
        }
        fun unfollowing() {
            binding.btnFollow.setBackgroundColor(Color.RED)
            binding.btnFollow.setText("팔로우")
            v = false
        }

    }
}