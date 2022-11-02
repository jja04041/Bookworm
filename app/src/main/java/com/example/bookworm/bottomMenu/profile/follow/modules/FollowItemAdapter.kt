package com.example.bookworm.bottomMenu.profile.follow.modules

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.bottomMenu.profile.follow.view.FollowerViewHolder
import com.example.bookworm.R
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.bottomMenu.profile.follow.interfaces.OnFollowBtnClickListener

//data: ArrayList<UserInfo>?,
class FollowItemAdapter(
    val context: Context, private val nowUserInfo: UserInfo
) : ListAdapter<UserInfo, RecyclerView.ViewHolder>(MyDiffCallback) {
    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.
    var listener: OnFollowBtnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View
        return when (viewType) {
            1 -> {
                view = inflater.inflate(R.layout.layout_user_item, parent, false)
                FollowerViewHolder(
                    view, context, nowUserInfo, listener!!
                )
            }
            else -> {
                view = inflater.inflate(R.layout.layout_item_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    //팔로우/ 팔로잉 버튼 눌렀을 때 숫자 변화를 보내기 위함.
    fun addListener(listener: OnFollowBtnClickListener) {
        this.listener = listener
    }

    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.apply {
            if (holder is FollowerViewHolder) {
                val userInfo = currentList[position]
                holder.setItem(userInfo)
            } else if (holder is LoadingViewHolder) {
                showLoadingView(holder, position)
            }
        }
    }


    //뷰타입 확인
    override fun getItemViewType(pos: Int): Int {
        return if (currentList[pos].token != "") 1 else 2
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
