package com.example.bookworm.Feed.items;

import android.view.View;

import com.example.bookworm.Feed.ViewHolders.ItemNoImgViewHolder;
import com.example.bookworm.Feed.ViewHolders.ItemViewHolder;

public interface OnFeedItemClickListener {
    public void onItemClick(ItemViewHolder holder, View view, int position);
    public void onItemClick(ItemNoImgViewHolder holder, View view, int position);
}
