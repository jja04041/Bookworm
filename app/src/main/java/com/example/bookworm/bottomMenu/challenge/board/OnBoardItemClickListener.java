package com.example.bookworm.bottomMenu.challenge.board;

import android.view.View;

import com.example.bookworm.bottomMenu.challenge.items.ChallengeAdapter;
import com.example.bookworm.bottomMenu.challenge.board.BoardAdapter;

public interface OnBoardItemClickListener {
    void onItemClick(BoardAdapter.ItemViewHolder holder, View view, int position);
}
