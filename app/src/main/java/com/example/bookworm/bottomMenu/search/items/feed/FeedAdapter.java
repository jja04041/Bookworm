package com.example.bookworm.bottomMenu.search.items.feed;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.Feed.comments.subactivity_comment;
import com.example.bookworm.bottomMenu.Feed.items.Feed;
import com.example.bookworm.databinding.FragmentRecordItemBinding;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFeedItemClickListener {
    ArrayList<Feed> feedList;
    Context context;
    FragmentRecordItemBinding binding;
    OnFeedItemClickListener listener;


    public FeedAdapter(ArrayList<Feed> data, Context c) {
        feedList = data;
        context = c;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_record_item, parent, false);
        return new FeedAdapter.ItemViewHolder(view, listener);
    }

    public void setListener(OnFeedItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        Feed item = feedList.get(safePosition);
        ((FeedAdapter.ItemViewHolder) holder).setItem(item);

    }


    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public void onItemClick(FeedAdapter.ItemViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView, final OnFeedItemClickListener listener) {
            super(itemView);
            binding = FragmentRecordItemBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    //리스너 인터페이스 구현
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onItemClick(FeedAdapter.ItemViewHolder.this, view, position);
                        }
                    }
                }
            });
        }

        public void setItem(Feed item) {
            Glide.with(context).load(item.getBook().getImg_url()).into(binding.ivBookThumb);
            binding.feedBookTitle.setText(item.getBook().getTitle());
            binding.tvFeedtext.setText(item.getFeedText());
            binding.tvFeedDate.setText(item.getDate().substring(5, 10));
            //binding.tvFeedWriter.setText(item.getUserToken());
            binding.frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, subactivity_comment.class);
                    intent.putExtra("item", item);
                    intent.putExtra("position", getAbsoluteAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }


    }

    public void setData(ArrayList data) {
        feedList.clear();
        feedList.addAll(data);
    }


}
