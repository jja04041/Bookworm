package com.example.bookworm.bottomMenu.search.subactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.Feed.items.Feed;
import com.example.bookworm.bottomMenu.challenge.board.Board;
import com.example.bookworm.databinding.SearchFragmentResultFeedBinding;

import java.util.ArrayList;


public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnSearchResultItemClickListener {
    ArrayList<Feed> feedList;
    Context context;
    SearchFragmentResultFeedBinding binding;
    OnSearchResultItemClickListener listener;


    public SearchResultAdapter(ArrayList<Feed> data, Context c) {
        feedList = data;
        context = c;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_fragment_result_feed, parent, false);
        return new SearchResultAdapter.ItemViewHolder(view, listener);
    }

    public void setListener(OnSearchResultItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        Feed item = feedList.get(safePosition);
        ((SearchResultAdapter.ItemViewHolder) holder).setItem(item);

    }


    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public void onItemClick(ItemViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView, final OnSearchResultItemClickListener listener) {
            super(itemView);
            binding = SearchFragmentResultFeedBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    //리스너 인터페이스 구현
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onItemClick(ItemViewHolder.this, view, position);
                        }
                    }
                }
            });
        }

        public void setItem(Feed item) {
//            Glide.with(context).load(item.getBook().getImg_url()).into(binding.ivBookThumb);
//            binding.feedBookTitle.setText(item.getBook().getTitle());
//            binding.tvFeedtext.setText(item.getFeedText());
//            binding.tvFeedDate.setText(item.getDate().substring(5,10));
            binding.tvCommentContent.setText(item.getFeedText());
            binding.tvDate.setText(item.getDate());
        }
    }


    class BoardDiffCallback extends DiffUtil.ItemCallback<Board> {

        @Override
        public boolean areItemsTheSame(@NonNull Board oldItem, @NonNull Board newItem) {
            return oldItem.getBoardID() == newItem.getBoardID();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Board oldItem, @NonNull Board newItem) {
            return oldItem == newItem;
        }
    }
}
