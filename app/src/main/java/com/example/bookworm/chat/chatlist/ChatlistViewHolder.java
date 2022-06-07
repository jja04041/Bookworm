package com.example.bookworm.chat.chatlist;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.bookworm.achievement.Listener.OnViewHolderItemClickListener;
import com.example.bookworm.chat.MessageItem;
import com.example.bookworm.core.internet.FBModule;

public class ChatlistViewHolder extends RecyclerView.ViewHolder {

    TextView tv_opponame, tv_time, tv_content;
    // 샘플 iv와 tap시 나오는 큰 iv
    ImageView iv_oppopic;
    LinearLayout chatlistitem;
    FBModule fbModule;

    OnViewHolderItemClickListener onViewHolderItemClickListener;


    public ChatlistViewHolder(@NonNull View itemView) {
        super(itemView);


        chatlistitem = itemView.findViewById(R.id.chatlist_item);
        tv_opponame = itemView.findViewById(R.id.tv_opponame);
        tv_time = itemView.findViewById(R.id.tv_chatlisttime);
        tv_content = itemView.findViewById(R.id.tv_chatlistcontent);
        iv_oppopic = itemView.findViewById(R.id.iv_oppopic);


        chatlistitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewHolderItemClickListener.onViewHolderItemClick();
            }
        });
    }

    public void onBind(MessageItem itemData) {
        tv_opponame.setText(itemData.getName());
        tv_time.setText(itemData.getTime());
        tv_content.setText(itemData.getMessage());
        Uri uri = Uri.parse(itemData.getProfileUrl());
        iv_oppopic.setImageURI(uri);
    }


    public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {

        this.onViewHolderItemClickListener = onViewHolderItemClickListener;
    }
}