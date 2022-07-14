package com.example.bookworm.chat;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    ArrayList<MessageItem> messageItems;
    LayoutInflater layoutInflater;
    String username;
    String token;

    public ChatAdapter(ArrayList<MessageItem> messageItems, LayoutInflater layoutInflater, String username, String token) {
        this.messageItems = messageItems;
        this.layoutInflater = layoutInflater;
        this.username = username;
        this.token = token;
    }

    @Override
    public int getCount() {
        return messageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        //현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        MessageItem item = messageItems.get(position);

        //재활용할 뷰는 사용하지 않음!!
        View itemView = null;

        //메세지가 내 메세지인지??
        if (item.getName().equals(token)) {
            itemView = layoutInflater.inflate(R.layout.my_msgbox, viewGroup, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.other_msgbox, viewGroup, false);
        }

        //만들어진 itemView에 값들 설정
        ImageView iv = itemView.findViewById(R.id.iv);
        TextView tvName = itemView.findViewById(R.id.tv_name);
        TextView tvMsg = itemView.findViewById(R.id.tv_msg);
        TextView tvTime = itemView.findViewById(R.id.tv_time);


        tvName.setText(item.getName());
        tvMsg.setText(item.getMessage());
        tvTime.setText(item.getTime());

        Glide.with(itemView).load(item.getProfileUrl()).circleCrop().into(iv);


        return itemView;
    }
}