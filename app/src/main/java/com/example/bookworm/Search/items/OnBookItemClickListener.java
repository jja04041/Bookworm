package com.example.bookworm.Search.items;

import android.view.View;

import com.example.bookworm.Search.items.BookAdapter;

public interface OnBookItemClickListener {
    public void onItemClick(BookAdapter.ItemViewHolder holder, View view, int position);
}
