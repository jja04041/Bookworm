package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.challenge.board.Board;
import com.example.bookworm.bottomMenu.feed.Feed;
import com.example.bookworm.bottomMenu.feed.comments.SubActivityComment;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.FragmentRecordItemBinding;

import java.util.ArrayList;


public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnRecordItemClickListener {
    ArrayList<Feed> feedList;
    Context context;
    FragmentRecordItemBinding binding;
    OnRecordItemClickListener listener;
    UserInfoViewModel uv;
    UserInfo NowUser;


    public RecordAdapter(ArrayList<Feed> data, Context c) {
        feedList = data;
        context = c;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_record_item, parent, false);
        return new RecordAdapter.ItemViewHolder(view, listener);
    }

    public void setListener(OnRecordItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        Feed item = feedList.get(safePosition);
        ((RecordAdapter.ItemViewHolder) holder).setItem(item);

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

        public ItemViewHolder(@NonNull View itemView, final OnRecordItemClickListener listener) {
            super(itemView);
            binding = FragmentRecordItemBinding.bind(itemView);
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
            Glide.with(context).load(item.getBook().getImgUrl()).into(binding.ivBookThumb);
            binding.feedBookTitle.setText(item.getBook().getTitle());
            binding.tvFeedtext.setText(item.getFeedText());
            binding.tvFeedDate.setText(item.getDate().substring(5, 10));

            uv = new UserInfoViewModel(context);

            uv.getUser(item.getUserToken(), true);
            uv.getUserInfoLiveData().observe((LifecycleOwner) context, userInfo -> {
                NowUser = userInfo;
            });

            binding.frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SubActivityComment.class);
                    item.setCreatorInfo(NowUser);
                    intent.putExtra("Feed", item);
                    intent.putExtra("NowUser", NowUser);
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
