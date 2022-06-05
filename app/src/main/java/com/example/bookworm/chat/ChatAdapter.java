package com.example.bookworm.chat;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookworm.R;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    ArrayList<Chatmodel> messageItems;
    LayoutInflater layoutInflater;
    String username;

    public ChatAdapter(ArrayList<Chatmodel> messageItems, LayoutInflater layoutInflater, String username) {
        this.messageItems = messageItems;
        this.layoutInflater = layoutInflater;
        this.username = username;
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
        Chatmodel item= messageItems.get(position);

        View itemView=null;

        //메세지가 내 메세지인지??
        if(item.getMessageitem().getName().equals(username)){
            itemView= layoutInflater.inflate(R.layout.my_msgbox,viewGroup,false);
        }else{
            itemView= layoutInflater.inflate(R.layout.other_msgbox,viewGroup,false);
        }

        //만들어진 itemView에 값들 설정
        ImageView iv= itemView.findViewById(R.id.iv);
        TextView tvName= itemView.findViewById(R.id.tv_name);
        TextView tvMsg= itemView.findViewById(R.id.tv_msg);
        TextView tvTime= itemView.findViewById(R.id.tv_time);

        tvName.setText(item.getMessageitem().getName());
        tvMsg.setText(item.getMessageitem().getMessage());
        tvTime.setText(item.getMessageitem().getTime());

        Glide.with(itemView).load(item.getMessageitem().getProfileUri()).apply(new RequestOptions().circleCrop().centerCrop() ).into(iv);

        return itemView;
    }
}