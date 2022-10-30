package com.example.bookworm.bottomMenu.challenge.board;

import android.view.View;


public interface OnBoardItemClickListener {
    void onItemClick(BoardAdapter.ItemViewHolder holder, View view, int position);
}
