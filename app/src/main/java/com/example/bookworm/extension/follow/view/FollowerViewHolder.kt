package com.example.bookworm.extension.follow.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookworm.bottomMenu.profile.views.ProfileInfoActivity
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.extension.follow.interfaces.PagerInterface
import com.example.bookworm.databinding.LayoutUserItemBinding
import kotlinx.coroutines.launch

//isFollower 0=팔로잉 탭, 1=팔로워 탭
class FollowerViewHolder(
    val itemView: View, context: Context?, val nowUserInfo: UserInfo, val isFollower: Int
//,val pager: PagerInterface.PageAdapter?
) :
    RecyclerView.ViewHolder(itemView), PagerInterface.Page {
    var context = context//전달된 context
    var binding = LayoutUserItemBinding.bind(itemView)
    var v = false
    val fv = ViewModelProvider(context as FollowerActivity, FollowViewModel.Factory(context)).get(FollowViewModel::class.java)
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
                            (context as FollowerActivity).lifecycleScope.launch {
                                fv.follow(item, false)
                                unfollowing()
                            }


                        } else {
                            (context as FollowerActivity).lifecycleScope.launch {
                                fv.follow(item, true)
                                following()
                            }
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(
                        "아니요"
                    ) { dialog, which -> dialog.dismiss() }.show()
            })
        } else binding.btnFollow.visibility = View.INVISIBLE;


        itemView.setOnClickListener(
            {
                var intent = Intent(context, ProfileInfoActivity::class.java)
                intent.putExtra("userID", item!!.token)
                intent.putExtra("pos", absoluteAdapterPosition)
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