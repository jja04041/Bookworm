package com.example.bookworm.Extension.Follow.View

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.Core.UserData.UserInfo
import com.example.bookworm.Extension.Follow.Interfaces.PagerInterface
import com.example.bookworm.Extension.Follow.Modules.followCounter
import com.example.bookworm.BottomMenu.Profile.View.ProfileInfoActivity
import com.example.bookworm.databinding.LayoutUserItemBinding
//isFollower 0=팔로잉 탭, 1=팔로워 탭
class FollowerViewHolder(val itemView: View, context: Context?, val nowUserInfo: UserInfo,val isFollower:Int,val pager: PagerInterface.PageAdapter?) :
    RecyclerView.ViewHolder(itemView),PagerInterface.Page {
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
                AlertDialog.Builder(context)
                    .setMessage("팔로우를 취소하시겠습니까?")
                    .setPositiveButton(
                        "네"
                    ) { dialog, which ->
                        if (v) {
                            followCounter(pager,isFollower)
                                .unfollow(item, nowUserInfo, context)
                            unfollowing()
                        } else {
                            followCounter(pager,isFollower)
                                .follow(item, nowUserInfo, context)
                            following()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        "아니요"
                    ) { dialog, which -> dialog.dismiss() }.show()
            })
    }else binding.btnFollow.visibility=View.INVISIBLE;
    itemView.setOnClickListener(
    {
        var intent = Intent(context, ProfileInfoActivity::class.java)
        intent.putExtra("userID", item.token)
        intent.putExtra("pos", adapterPosition)
        (context as FollowerActivity).startActivity(intent)
    })
}

fun following() {
    binding.btnFollow.setBackgroundColor(Color.BLUE) //버튼의 색상을 파란색으로 수정
    binding.btnFollow.setText("팔로잉")
    v = true
}

fun unfollowing() {
    binding.btnFollow.setBackgroundColor(Color.RED)
    binding.btnFollow.setText("팔로우")
    v = false
}


}