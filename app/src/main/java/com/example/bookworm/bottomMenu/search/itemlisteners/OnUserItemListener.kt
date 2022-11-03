package com.example.bookworm.bottomMenu.search.itemlisteners

import com.example.bookworm.bottomMenu.search.adapters.UserResultAdapter

interface OnUserItemListener {
    fun onClick(holder: UserResultAdapter.UserViewHolder, position: Int)
}