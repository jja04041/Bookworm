package com.example.bookworm.chat.chatlist;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.chat.MessageItem;
import com.example.bookworm.core.userdata.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_chatlist extends AppCompatActivity {

    private Context context;
    private ChatlistAdapter adapter;

    //Firebase Database 관리 객체참조변수
    private FirebaseDatabase firebaseDatabase;

    // chat노드의 참조객체 참조변수
    private DatabaseReference chatRef;

    private UserInfoViewModel uv;
    private UserInfoViewModel pv;
    private UserInfo userinfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        //Firebase DB관리 객체와 chat노드 참조객체 얻어오기
        firebaseDatabase = FirebaseDatabase.getInstance();
        chatRef = firebaseDatabase.getReference();

        context = activity_chatlist.this;

        pv = new UserInfoViewModel(context);
        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(context)).get(UserInfoViewModel.class);


        pv.getUser(null, false);

        pv.getData().observe(this, userInfo -> {
            userinfo = userInfo;

            init();
            getData();
        });
    }


    private void init() {
        RecyclerView recyclerView = findViewById(R.id.chatlist_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ChatlistAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {

        Uri uri = Uri.parse("https://k.kakaocdn.net/dn/b8DtBK/btqRorUTUCy/w10D6Zn5IMsop8v2BJY5VK/img_640x640.jpg");
        MessageItem messageItem = new MessageItem("곽성근", "5분전", "졸작 화이팅", "https://k.kakaocdn.net/dn/b8DtBK/btqRorUTUCy/w10D6Zn5IMsop8v2BJY5VK/img_640x640.jpg", "123");
        adapter.addItem(messageItem);


//        orderByChild("token/"+userinfo.getToken()).equalTo(true).
        firebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    dataSnapshot.getValue();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}