package com.example.bookworm.Follow.View

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.Profile.ProfileInfoActivity
import com.example.bookworm.Core.UserData.UserInfo
import com.example.bookworm.Follow.Modules.followCounter
import com.example.bookworm.databinding.LayoutUserItemBinding

class FollowerViewHolder(val itemView: View, context: Context?, val nowUserInfo: UserInfo):
    RecyclerView.ViewHolder(itemView) {
    var context = context//전달된 context
    var binding = LayoutUserItemBinding.bind(itemView)
    var v = false
    fun setItem(item: UserInfo?) {
        Glide.with(context!!).load(item!!.profileimg).circleCrop().into(binding.ivProfileImg)
        binding.tvProfileID.setText(item.username)
        //사용자 본인과 다른 프로필인 경우에만 팔로우, 팔로잉이 가능 => 본인이 본인을 팔로우하는 건 기본이기 때문
        if (!item.token.equals(nowUserInfo.token)) {
            if (item.isFollowed) following()
            else unfollowing()
            binding.btnFollow.setOnClickListener({
                if (v) {
                    followCounter()
                        .unfollow(item, nowUserInfo, context)
                    unfollowing()
                } else {
                    followCounter()
                        .follow(item, nowUserInfo, context)
                    following()
                }
            })
        }else binding.btnFollow.visibility=View.INVISIBLE;
        itemView.setOnClickListener({
            var intent = Intent(context, ProfileInfoActivity::class.java)
            intent.putExtra("userID",item.token)
            (context as FollowerActivity).startActivity(intent)
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