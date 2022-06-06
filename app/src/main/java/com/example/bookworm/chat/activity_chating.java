package com.example.bookworm.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class activity_chating extends AppCompatActivity {

    EditText et;
    ListView listView;

    ArrayList<MessageItem> messageItems=new ArrayList<>();
    ChatAdapter adapter;

    //Firebase Database 관리 객체참조변수
    FirebaseDatabase firebaseDatabase;

    // chat노드의 참조객체 참조변수
    DatabaseReference chatRef;

    // 유저 데이터
    private UserInfoViewModel uv;
    private UserInfoViewModel pv;
    private UserInfo userinfo;
    private UserInfo opponent;

    private TextView tv;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        // 상대방 토큰 받음
        Intent intent = getIntent();
        opponent = (UserInfo) intent.getSerializableExtra("opponent");

        tv = findViewById(R.id.tv_chatroomtopbar);
        tv.setText(opponent.getUsername());

        context = activity_chating.this;

        pv = new UserInfoViewModel(context);
        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(context)).get(UserInfoViewModel.class);


        pv.getUser(null, false);

        pv.getData().observe(this, userInfo -> {
            userinfo = userInfo;


            et = findViewById(R.id.et);
            listView = findViewById(R.id.chat_listview);
            adapter = new ChatAdapter(messageItems, getLayoutInflater(), userinfo.getUsername());
            listView.setAdapter(adapter);



            //Firebase DB관리 객체와 chat노드 참조객체 얻어오기
            firebaseDatabase = FirebaseDatabase.getInstance();
            String key = userinfo.getToken() + opponent.getToken();
            chatRef = firebaseDatabase.getReference(key);

            //firebaseDB에서 채팅 메세지들 실시간 읽어오기
            //chat노드에 저장되어 있는 데이터들을 읽어오기
            //chatRef에 데이터가 변경되는 것을 받는 리스너 추가
            chatRef.addChildEventListener(new ChildEventListener() {
                //새로 추가된 것만 줌 ValueListener는 하나의 값만 바뀌어도 처음부터 다시 값을 줌
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    //새로 추가된 데이터(값 : MessageItem객체) 가져오기
                    MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);

                    //새로운 메세지를 리스뷰에 추가하기 위해 ArrayList에 추가
                    messageItems.add(messageItem);

                    //리스트뷰를 갱신
                    adapter.notifyDataSetChanged();
                    listView.setSelection(messageItems.size() - 1); //리스트뷰의 마지막 위치로 스크롤 위치 이동
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        });


//        getSupportActionBar().setTitle(opponent.getUsername());
    }
    public void clickSend(View view) {

        //firebase DB에 저장할 값들
        String nickName= userinfo.getUsername();
        String opponent = userinfo.getUsername();
        String message= et.getText().toString();
        String pofileUrl= userinfo.getProfileimg();
        String token= userinfo.getToken();


        //메세지 작성 시간 문자열로
        Calendar calendar= Calendar.getInstance(); //현재 시간을 가지고 있는 객체
        String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);

        //firebase DB에 저장할 값(MessageItem객체) 설정
        MessageItem messageItem= new MessageItem(nickName, message,time,pofileUrl, token);


        //chat노드에 MessageItem객체를 전달
        chatRef.push().setValue(messageItem);

        //EditText에 있는 글씨 지우기
        et.setText("");

//        //소프트키패드를 안보이도록
//        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

    }
}