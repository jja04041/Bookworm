package com.example.bookworm.extension.follow.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.LoadState
import com.example.bookworm.bottomMenu.profile.views.ProfileInfoActivity
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.databinding.LayoutUserItemBinding

//isFollower 0=팔로잉 탭, 1=팔로워 탭
class FollowerViewHolder(
    val itemView: View, context: Context?, val nowUserInfo: UserInfo,
) :
    RecyclerView.ViewHolder(itemView) {
    var context = context//전달된 context
    var binding = LayoutUserItemBinding.bind(itemView)
    var isFollowed = false
    val fv =
        ViewModelProvider(context as FollowerActivity, FollowViewModel.Factory(context)).get(
            FollowViewModel::class.java
        )

    fun setItem(item: UserInfo?) {
        Glide.with(context!!).load(item!!.profileimg).circleCrop().into(binding.ivProfileImg)
        binding.tvProfileID.setText(item.username)
        //사용자 본인과 다른 프로필인 경우에만 팔로우, 팔로잉이 가능 => 본인이 본인을 팔로우하는 건 기본이기 때문
        if (!item.token.equals(nowUserInfo.token)) {
            if (item.isFollowed) following()
            else unfollowing()
            binding.btnFollow.setOnClickListener {
                AlertDialog.Builder(context)
                    .setMessage(if (item.isFollowed) "팔로우를 취소하시겠습니까?" else "팔로우 하시겠습니까?")
                    .setPositiveButton(
                        "네"
                    ) { dialog, which ->
                        if (isFollowed) {
                            val unFollowStateLiveData = MutableLiveData<LoadState>()
                            fv.follow(item, false, unFollowStateLiveData, item)
                            unFollowStateLiveData.observe(context as FollowerActivity) {
                                if (it == LoadState.Done)
                                    unfollowing()
                            }
                        } else {
                            val followStateLiveData = MutableLiveData<LoadState>()
                            fv.follow(item, true, followStateLiveData, item)
                            followStateLiveData.observe(context as FollowerActivity) {
                                if (it == LoadState.Done)
                                    following()

                            }
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        "아니요"
                    ) { dialog, which -> dialog.dismiss() }.show()
            }
        } else binding.btnFollow.visibility = View.INVISIBLE;


        itemView.setOnClickListener {
            val intent = Intent(context, ProfileInfoActivity::class.java)
            intent.putExtra("userID", item!!.token)
            intent.putExtra("pos", absoluteAdapterPosition)
            (context as FollowerActivity).startActivity(intent)
        }
    }

    fun following() {
        binding.btnFollow.isSelected = true
        binding.btnFollow.text = "팔로잉"
        isFollowed = true
    }

    fun unfollowing() {
        binding.btnFollow.isSelected = false
        binding.btnFollow.text = "팔로우"
        isFollowed = false

    }


}