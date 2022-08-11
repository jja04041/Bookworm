package com.example.bookworm.chat.newchat;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MessageActivity extends AppCompatActivity {
    private String chatRoomUid; //채팅방 하나 id
    private String myuid;       //나의 id
    private String destUid;     //상대방 uid

    private RecyclerView recyclerView;
    private Button button, button2;
    private EditText editText;

    private FirebaseDatabase firebaseDatabase;


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy.MM.dd HH:mm");


    private UserInfoViewModel uv;
    private UserInfoViewModel pv;
    private UserInfo userinfo;
    private UserInfo opponent;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        context = MessageActivity.this;

        Intent intent = getIntent();
        opponent = (UserInfo) intent.getSerializableExtra("opponent");


        TextView tv = findViewById(R.id.tv_chatroomtopbar);

        pv = new UserInfoViewModel(context);
        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(context)).get(UserInfoViewModel.class);

        pv.getUser(null, false);

        pv.getData().observe(this, userInfo -> {
            userinfo = userInfo;

            //Firebase DB관리 객체와 chat노드 참조객체 얻어오기
            firebaseDatabase = FirebaseDatabase.getInstance();

            myuid = userinfo.getToken();

            if(opponent != null) {
                destUid = opponent.getToken();
                tv.setText(opponent.getUsername());
            }
            else
            {
                destUid = (String) intent.getSerializableExtra("destuid");

                uv.getUser(destUid, true);
                uv.getData().observe(this, oppo -> {
                    opponent = oppo;
                    tv.setText(opponent.getUsername());
                });
            }


            init();
            sendMsg();


            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = "안녕 나는 책벌레야";
                    Create_DynamicLink(message);
                    Intent msg = new Intent(Intent.ACTION_SEND);

                    msg.addCategory(Intent.CATEGORY_DEFAULT);
                    msg.putExtra(Intent.EXTRA_TEXT, "https://bookbollae.page.link/63fF");
                    msg.putExtra(Intent.EXTRA_TITLE, "제목");
                    msg.setType("text/plain");
                    startActivity(Intent.createChooser(msg, "앱을 선택해 주세요"));


                }
            });
        });


    }

    private void init()
    {
        //myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //destUid = getIntent().getStringExtra("destUid");        //채팅 상대

        recyclerView = (RecyclerView)findViewById(R.id.message_recyclerview);
        button=(Button)findViewById(R.id.message_btn);
        editText = (EditText)findViewById(R.id.message_editText);

        button2 = (Button)findViewById(R.id.btnsharee);

        firebaseDatabase = FirebaseDatabase.getInstance();

        if(editText.getText().toString() == null) button.setEnabled(false);
        else button.setEnabled(true);

        checkChatRoom();
    }



    private void sendMsg()
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(myuid,true);
                chatModel.users.put(destUid,true);

                //push() 데이터가 쌓이기 위해 채팅방 key가 생성
                if(chatRoomUid == null){
                    Toast.makeText(MessageActivity.this, "채팅방 생성", Toast.LENGTH_SHORT).show();
                    button.setEnabled(false);
                    firebaseDatabase.getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                }else{
                    sendMsgToDataBase();
                }
            }
        });
    }
    //작성한 메시지를 데이터베이스에 보낸다.
    private void sendMsgToDataBase()
    {
        if(!editText.getText().toString().equals(""))
        {
            ChatModel.Comment comment = new ChatModel.Comment();
            comment.uid = myuid;
            comment.message = editText.getText().toString();
            comment.timestamp = ServerValue.TIMESTAMP;
            firebaseDatabase.getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    editText.setText("");
                }
            });
        }
    }

    private void checkChatRoom()
    {
        //자신 key == true 일때 chatModel 가져온다.
        /* chatModel
        public Map<String,Boolean> users = new HashMap<>(); //채팅방 유저
        public Map<String, ChatModel.Comment> comments = new HashMap<>(); //채팅 메시지
        */
        firebaseDatabase.getReference().child("chatrooms").orderByChild("users/"+myuid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) //나, 상대방 id 가져온다.
                {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destUid)){           //상대방 id 포함돼 있을때 채팅방 key 가져옴
                        chatRoomUid = dataSnapshot.getKey();
                        //opponent = snapshot.getValue(UserInfo.class);
                        button.setEnabled(true);

                        //동기화
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());

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

    //===============채팅 창===============//
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
    {
        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter(){
            comments = new ArrayList<>();

            getDestUid();
        }

        //상대방 uid 하나(single) 읽기
        private void getDestUid()
        {
            firebaseDatabase.getReference().child("users").child(destUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //opponent = snapshot.getValue(UserInfo.class);

                    //채팅 내용 읽어들임
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        //채팅 내용 읽어들임
        private void getMessageList()
        {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        comments.add(dataSnapshot.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged();

                    recyclerView.scrollToPosition(comments.size()-1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_messagebox,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
            ViewHolder viewHolder = ((ViewHolder)holder);

            if(comments.get(position).uid.equals(myuid)) //나의 uid 이면
            {
                //나의 말풍선 오른쪽으로
                viewHolder.textViewMsg.setText(comments.get(position).message);
                viewHolder.textViewMsg.setBackgroundResource(R.drawable.rightbubble);
                viewHolder.linearLayoutDest.setVisibility(View.INVISIBLE);        //상대방 레이아웃
                viewHolder.linearLayoutRoot.setGravity(Gravity.RIGHT);
                viewHolder.linearLayoutTime.setGravity(Gravity.RIGHT);

            }else{
                //상대방 말풍선 왼쪽
                Glide.with(holder.itemView.getContext())
                        .load(opponent.getProfileimg())
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.imageViewProfile);
                viewHolder.textViewName.setText(opponent.getUsername());
                viewHolder.linearLayoutDest.setVisibility(View.VISIBLE);
                viewHolder.textViewMsg.setBackgroundResource(R.drawable.leftbubble);
                viewHolder.textViewMsg.setText(comments.get(position).message);
                viewHolder.linearLayoutRoot.setGravity(Gravity.LEFT);
                viewHolder.linearLayoutTime.setGravity(Gravity.LEFT);
            }
            viewHolder.textViewTimeStamp.setText(getDateTime(position));

        }

        public String getDateTime(int position)
        {
            long unixTime=(long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            return time;
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView textViewMsg;   //메시지 내용
            public TextView textViewName;
            public TextView textViewTimeStamp;
            public ImageView imageViewProfile;
            public LinearLayout linearLayoutDest;
            public LinearLayout linearLayoutRoot;
            public LinearLayout linearLayoutTime;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                textViewMsg = (TextView)itemView.findViewById(R.id.item_messagebox_textview_msg);
                textViewName = (TextView)itemView.findViewById(R.id.item_messagebox_TextView_name);
                textViewTimeStamp = (TextView)itemView.findViewById(R.id.item_messagebox_textview_timestamp);
                imageViewProfile = (ImageView)itemView.findViewById(R.id.item_messagebox_ImageView_profile);
                linearLayoutDest = (LinearLayout)itemView.findViewById(R.id.item_messagebox_LinearLayout);
                linearLayoutRoot = (LinearLayout)itemView.findViewById(R.id.item_messagebox_root);
                linearLayoutTime = (LinearLayout)itemView.findViewById(R.id.item_messagebox_layout_timestamp);
            }
        }
    }


        // (subject = 들어갈 문구, pageurl =
        public void Create_DynamicLink (final String subject){

            // 다른 이미지 넣고싶으면 함수 인자로 이미지 받기
            Uri appiconuri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, R.drawable.appicon_bookworm);

            Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("www.google.com")) // pageurl
                    .setDomainUriPrefix("https://b00kworm.page.link/TG78")
                    .setAndroidParameters(
                            new DynamicLink.AndroidParameters.Builder(getPackageName())
                                    .build())
                    .setSocialMetaTagParameters(
                            new DynamicLink.SocialMetaTagParameters.Builder()
                                    .setTitle("공유하기 테스트")
                                    .setImageUrl(appiconuri)
                                    .build())
                    .buildShortDynamicLink()
                    .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                        @Override
                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                            if (task.isSuccessful()) {
                                Uri ShortLink = task.getResult().getShortLink();
                                try {
                                    Intent Sharing_Intent = new Intent();
                                    Sharing_Intent.setAction(Intent.ACTION_SEND);
                                    Sharing_Intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                    Sharing_Intent.putExtra(Intent.EXTRA_TEXT, ShortLink.toString());
                                    Sharing_Intent.setType("text/plain");
                                    startActivity(Intent.createChooser(Sharing_Intent, "sharing"));
                                }
                                catch (Exception e) {
                                }
                            }
                        }
                    });


        }


}