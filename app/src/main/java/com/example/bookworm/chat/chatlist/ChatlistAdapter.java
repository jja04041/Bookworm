package com.example.bookworm.chat.chatlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.bookworm.achievement.Listener.OnViewHolderItemClickListener;
import com.example.bookworm.chat.MessageItem;

import java.util.ArrayList;

public class ChatlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<MessageItem> listData = new ArrayList<>();



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatlist, parent, false);
        return new ChatlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ChatlistViewHolder viewHolder = (ChatlistViewHolder) holder;
        viewHolder.onBind(listData.get(position));


        // 뷰홀더에 아이템클릭리스너 인터페이스 붙이기
        viewHolder.setOnViewHolderItemClickListener(new OnViewHolderItemClickListener() {
            @Override
            public void onViewHolderItemClick() {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(MessageItem messageItem) {
        listData.add(messageItem);
    }
}