package com.example.bookworm.chat.newchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class fragment_chatlist extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.newchat_fragment_chat, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_recyclerView);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return view;
    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        private UserInfoViewModel uv;
        private UserInfoViewModel pv;
        private UserInfo userinfo;

        private List<ChatModel> chatModels = new ArrayList<>();
        private String uid;
        private Context context;

        private ArrayList<String> destusers = new ArrayList<>();

        public ChatRecyclerViewAdapter(){

            context = getContext();

            pv = new UserInfoViewModel(context);
            uv = new ViewModelProvider(getActivity(), new UserInfoViewModel.Factory(context)).get(UserInfoViewModel.class);

            pv.getUser(null, false);

            pv.getData().observe(getActivity(), userInfo -> {
                userinfo = userInfo;

                uid = userinfo.getToken();

                FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot item :snapshot.getChildren())
                        {
                            chatModels.add(item.getValue(ChatModel.class));

                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            });

        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            String destuid = null;

            for(String user: chatModels.get(position).users.keySet()) {
                if(!user.equals(uid)) {
                    destuid = user;
                    destusers.add(destuid);
                }
            }

            String finalDestuid = destuid;
            FirebaseDatabase.getInstance().getReference().child("users").child(destuid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    uv.getUser(finalDestuid, true);
                    uv.getData().observe(getViewLifecycleOwner(), userInfo -> {

                        UserInfo opponent = userInfo;
                        Glide.with(customViewHolder.itemView.getContext())
                                .load(opponent.getProfileimg())
                                .apply(new RequestOptions().circleCrop())
                                .into(customViewHolder.iv_profileimg);

                        customViewHolder.tv_title.setText(opponent.getUsername());
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // 메시지 내림순으로 정렬 후 마지막 메시지 키 가져옴
            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            customViewHolder.tv_comment.setText(chatModels.get(position).comments.get(lastMessageKey).message);

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                    intent.putExtra("destuid", destusers.get(position));

                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public ImageView iv_profileimg;
            public TextView tv_title;
            public TextView tv_comment;

            public CustomViewHolder(View view){
                super(view);
                iv_profileimg = (ImageView)view.findViewById(R.id.item_chat_imageView);
                tv_title = (TextView)view.findViewById(R.id.item_chat_tv_title);
                tv_comment = (TextView)view.findViewById(R.id.item_chat_tv_comment);

            }
        }

    }
}
