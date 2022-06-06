package com.example.bookworm.extension.follow.modules

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.extension.follow.view.FollowerViewHolder
import com.example.bookworm.R
import com.example.bookworm.core.userdata.UserInfo

//data: ArrayList<UserInfo>?,
class FollowItemAdapter(val context: Context, val nowUserInfo: UserInfo, val isFollower:Int
//                    , val pager: PagerInterface.PageAdapter
                        )
    : ListAdapter<UserInfo, RecyclerView.ViewHolder>(MyDiffCallback) {
    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var inflater: LayoutInflater = LayoutInflater.from(context)
        var view: View
        when (viewType) {
            1 -> {
                view = inflater.inflate(R.layout.layout_user_item, parent, false)
                return FollowerViewHolder(view, context,nowUserInfo,isFollower
//                    ,pager
                )
            }
            else -> {
                view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                return LoadingViewHolder(view)
            }
        }
    }


    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            if (holder is FollowerViewHolder) {
                var userInfo = currentList!!.get(position)
                holder.setItem(userInfo)
            } else if (holder is LoadingViewHolder) {
                showLoadingView(holder, position)
            }
        }
    }


    //뷰타입 확인
    override fun getItemViewType(pos: Int): Int {
        return if (currentList.get(pos).token != null) 1 else 2
    }


    private class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //
    }

    object MyDiffCallback : DiffUtil.ItemCallback<UserInfo>() {
        override fun areItemsTheSame(
            oldItem: UserInfo,
            newItem: UserInfo
        ): Boolean {

            return oldItem.token == newItem.token
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: UserInfo,
            newItem: UserInfo
        ): Boolean {
            return oldItem == newItem
        }

    }

}
