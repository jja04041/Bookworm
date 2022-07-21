package com.example.bookworm.chat.newchat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;

public class CustomViewholder extends RecyclerView.ViewHolder {

        public ImageView iv_profileimg;
        public TextView tv_title;
        public TextView tv_comment;

        public CustomViewholder(View view){
            super(view);
            iv_profileimg = (ImageView)view.findViewById(R.id.item_chat_imageView);
            tv_title = (TextView)view.findViewById(R.id.item_chat_tv_title);
            tv_comment = (TextView)view.findViewById(R.id.item_chat_tv_comment);

        }

}
