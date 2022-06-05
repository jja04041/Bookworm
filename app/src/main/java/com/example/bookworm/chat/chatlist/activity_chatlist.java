package com.example.bookworm.chat.chatlist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.bookworm.chat.Chatmodel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class activity_chatlist extends AppCompatActivity {

    private Context context;
    private ChatlistAdapter adapter;

    //Firebase Database 관리 객체참조변수
    private FirebaseDatabase firebaseDatabase;

    // chat노드의 참조객체 참조변수
    private DatabaseReference chatRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        //Firebase DB관리 객체와 chat노드 참조객체 얻어오기
        firebaseDatabase = FirebaseDatabase.getInstance();
        chatRef = firebaseDatabase.getReference("chatpath");


        init();
        getData();
    }


    private void init() {
        RecyclerView recyclerView = findViewById(R.id.chatlist_recyclerbiew);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ChatlistAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {

        Uri uri = Uri.parse("https://k.kakaocdn.net/dn/b8DtBK/btqRorUTUCy/w10D6Zn5IMsop8v2BJY5VK/img_640x640.jpg");
        Chatmodel.MessageItem messageItem = new Chatmodel.MessageItem("곽성근", "5분전", "졸작 화이팅", "https://k.kakaocdn.net/dn/b8DtBK/btqRorUTUCy/w10D6Zn5IMsop8v2BJY5VK/img_640x640.jpg");
        adapter.addItem(messageItem);

    }



}
