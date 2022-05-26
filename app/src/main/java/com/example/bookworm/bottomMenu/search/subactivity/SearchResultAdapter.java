package com.example.bookworm.bottomMenu.search.subactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.Feed.items.Feed;
import com.example.bookworm.bottomMenu.Feed.views.FeedViewModel;
import com.example.bookworm.bottomMenu.challenge.board.Board;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.core.userdata.interfaces.UserContract;
import com.example.bookworm.core.userdata.modules.LoadUser;
import com.example.bookworm.databinding.SearchFragmentResultFeedBinding;

import java.util.ArrayList;
import java.util.Observer;


public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnSearchResultItemClickListener {
    ArrayList<Feed> feedList;
    Context context;
    OnSearchResultItemClickListener listener;
    FeedViewModel pv;
    LifecycleOwner lifecycleOwner;

    public SearchResultAdapter(ArrayList<Feed> data, Context c, LifecycleOwner owner) {
        feedList = data;
        context = c;
        pv = new FeedViewModel(context);
        lifecycleOwner = owner;
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

    public class ItemViewHolder extends RecyclerView.ViewHolder implements UserContract.View {

        SearchFragmentResultFeedBinding binding;
//        LoadUser user;
        MutableLiveData<UserInfo> feedUserInfo = new MutableLiveData<>();

        public ItemViewHolder(@NonNull View itemView, final OnSearchResultItemClickListener listener) {
            super(itemView);
            binding = SearchFragmentResultFeedBinding.bind(itemView);
//            user = new LoadUser(this);

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
//            user.getData(item.getUserToken(), null);
            binding.tvCommentContent.setText(item.getFeedText());
            binding.tvDate.setText(item.getDate());

            pv.getUser(item.getUserToken(), feedUserInfo);
            feedUserInfo.observe(lifecycleOwner, feedUserInfo -> {
                showProfile(feedUserInfo, false);
            });
        }


        @Override
        public void showProfile(@NonNull UserInfo userInfo, @NonNull Boolean bool) {
            Glide.with(context).load(userInfo.getProfileimg()).circleCrop().into(binding.imgProfile);
            binding.tvNickname.setText(userInfo.getUsername());
//            ((search_fragment_subActivity_result) context).showShimmer(false);
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
