package com.example.bookworm.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class activity_chating extends AppCompatActivity {

    private EditText et;
    private ListView listView;

    private ArrayList<Chatmodel> messageItems=new ArrayList<>();
    private ChatAdapter adapter;

    //Firebase Database 관리 객체참조변수
    private FirebaseDatabase firebaseDatabase;

    // chat노드의 참조객체 참조변수
    private DatabaseReference chatRef;

    // 유저 데이터
    private UserInfoViewModel uv;
    private UserInfoViewModel pv;
    private UserInfo userinfo;

    private Context context;
    private TextView topbar;

    private String oppotoken;
    private String opponame;
    private String mytoken;
    private String chatpath;

    private String chatRoomUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        // 상대방 토큰 받음
        Intent intent = getIntent();
        oppotoken = (String)intent.getSerializableExtra("opponent");
        opponame = intent.getStringExtra("opponentname");

        topbar = findViewById(R.id.tv_chattopbar);

        context = activity_chating.this;

        pv = new UserInfoViewModel(context);
        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(context)).get(UserInfoViewModel.class);

        topbar.setText(opponame);

        pv.getUser(null, false);

        pv.getData().observe(this, userInfo -> {
            userinfo = userInfo;

            //제목줄 제목글시를 닉네임으로(또는 채팅방)

            et = findViewById(R.id.et);
            listView = findViewById(R.id.chat_listview);
            adapter = new ChatAdapter(messageItems, getLayoutInflater(), userinfo.getUsername());
            listView.setAdapter(adapter);

            // 내토큰 + 상대토큰 = 채팅방 이름
            mytoken = (userinfo.getToken());
            //chatpath = oppotoken + "_" + mytoken;


            //Firebase DB관리 객체와 chat노드 참조객체 얻어오기
            firebaseDatabase = FirebaseDatabase.getInstance();
            //chatRef = firebaseDatabase.getReference(chatpath);

            //checkChatRoom();
        });



    }
    public void clickSend(View view) {

        Chatmodel chatmodel = new Chatmodel();
        chatmodel.users.put(mytoken, true);
        chatmodel.users.put(oppotoken, true);

        Chatmodel.MessageItem messageItem = new Chatmodel.MessageItem();
        Calendar calendar = Calendar.getInstance(); //현재 시간을 가지고 있는 객체
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        messageItem.setToken(oppotoken);
        messageItem.setName(userinfo.getUsername());
        messageItem.setMessage(et.getText().toString());
        messageItem.setProfileUri(userinfo.getProfileimg());
        messageItem.setTime(time);

        chatmodel.setMessageitem(messageItem);

        //chatRef.push().setValue(messageItem);
        //chat노드에 MessageItem객체를 통해
        //새로운 메세지를 리스뷰에 추가하기 위해 ArrayList에 추가
        messageItems.add(chatmodel);

        //리스트뷰를 갱신
        adapter.notifyDataSetChanged();
        listView.setSelection(messageItems.size() - 1); //리스트뷰의 마지막 위치로 스크롤 위치 이동


        //push() 데이터가 쌓이기 위해 채팅방 key가 생성
        if(chatRoomUid == null){
            Toast.makeText(activity_chating.this, "채팅방 생성", Toast.LENGTH_SHORT).show();
            firebaseDatabase.getReference().child("chatrooms").push().setValue(chatmodel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    checkChatRoom();
                }
            });
        }else{
            sendMsgToDataBase();
        }

    }



    private void sendMsgToDataBase()
    {

        if(!et.getText().toString().equals(""))
        {
            Chatmodel.MessageItem messageItem = new Chatmodel.MessageItem();
            Calendar calendar = Calendar.getInstance(); //현재 시간을 가지고 있는 객체
            String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

            messageItem.setToken(oppotoken);
            messageItem.setName(userinfo.getUsername());
            messageItem.setMessage(et.getText().toString());
            messageItem.setProfileUri(userinfo.getProfileimg());
            messageItem.setTime(time);

//            chatRef.push().setValue(messageItem);

            firebaseDatabase.getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(messageItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //EditText에 있는 글씨 지우기
                    et.setText("");

                    //소프트키패드를 안보이도록
                    InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                }
            });
        }
    }


    private void checkChatRoom()
    {
        //자신 key == true 일때 chatModel 가져온다.
        firebaseDatabase.getReference().child("chatrooms").orderByChild("users/"+mytoken).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) //나, 상대방 id 가져온다.
                {
                    Chatmodel chatModel = dataSnapshot.getValue(Chatmodel.class);
                    if(chatModel.users.containsKey(oppotoken)){           //상대방 id 포함돼 있을때 채팅방 key 가져옴
                        chatRoomUid = dataSnapshot.getKey();


                        adapter.notifyDataSetChanged();
                        listView.setSelection(messageItems.size() - 1); //리스트뷰의 마지막 위치로 스크롤 위치 이동

                        //메시지 보내기
                        sendMsgToDataBase();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getDestUid()
    {
//        firebaseDatabase.getReference().child("users").child(oppotoken).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                //채팅 내용 읽어들임
//                getMessageList();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
    }

//    //채팅 내용 읽어들임
//    private void getMessageList()
//    {
//        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                comments.clear();
//
//                for(DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    comments.add(dataSnapshot.getValue(ChatModel.Comment.class));
//                }
//                notifyDataSetChanged();
//
//                recyclerView.scrollToPosition(comments.size()-1);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
//    }
}