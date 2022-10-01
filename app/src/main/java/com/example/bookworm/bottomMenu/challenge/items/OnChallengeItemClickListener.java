package com.example.bookworm.bottomMenu.challenge.items;

import android.view.View;

import com.example.bookworm.bottomMenu.challenge.ChallengeAdapter;

public interface OnChallengeItemClickListener {
    void onItemClick(ChallengeAdapter.ChallengeItemViewHolder holder, View view, int position);
}
