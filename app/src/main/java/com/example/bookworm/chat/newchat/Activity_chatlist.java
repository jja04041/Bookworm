package com.example.bookworm.chat.newchat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;

public class Activity_chatlist extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = Activity_chatlist.this;

        setContentView(R.layout.fragment_chat);

        ImageView btnBack = (ImageView) findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.activity_chatlist);
        getLayoutInflater().inflate(R.layout.fragment_chat, viewGroup, false);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fragment_chat_recyclerView);
        recyclerView.setAdapter(new ChatlistRecyclerViewAdapter(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));

    }
}
