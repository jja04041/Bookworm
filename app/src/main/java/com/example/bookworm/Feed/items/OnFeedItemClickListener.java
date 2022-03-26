package com.example.bookworm.Feed.items;

import android.view.View;

import com.example.bookworm.Challenge.items.ChallengeAdapter;

public interface OnFeedItemClickListener {
    public void onItemClick(FeedAdapter.ItemViewHolder holder, View view, int position);
}
