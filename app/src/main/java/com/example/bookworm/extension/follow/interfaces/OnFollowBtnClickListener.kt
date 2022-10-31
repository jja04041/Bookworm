package com.example.bookworm.extension.follow.interfaces

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.bookworm.extension.follow.view.FollowerViewHolder


//버튼이 눌린 후 진행되는 것들을 메인 액티비티에 적용시키기 위한 리스너.
interface OnFollowBtnClickListener {
    fun onItemClick(holder: FollowerViewHolder, v: View)
}