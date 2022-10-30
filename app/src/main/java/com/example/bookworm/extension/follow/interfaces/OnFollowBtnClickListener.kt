package com.example.bookworm.extension.follow.interfaces

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface OnFollowBtnClickListener {
    fun onItemClick(holder: RecyclerView.ViewHolder, view: View, position: Int)
}