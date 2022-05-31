package com.example.bookworm.bottomMenu.search.subactivity;

import android.view.View;

public interface OnSearchResultItemClickListener {
    void onItemClick(SearchResultAdapter.ItemViewHolder holder, View view, int position);
}
