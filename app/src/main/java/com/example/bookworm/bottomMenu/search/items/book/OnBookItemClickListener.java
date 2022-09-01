package com.example.bookworm.bottomMenu.search.items.book;

import android.view.View;

public interface OnBookItemClickListener {
    public void onItemClick(BookAdapter.ItemViewHolder holder, View view, int position);

    public void onItemClick(RecomBookAdapter.ItemViewHolder holder, View view, int position);
}
