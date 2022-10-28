package com.example.bookworm.chat.newchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.ItemChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChatlistRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private UserInfoViewModel pv;
    private UserInfo userinfo;

    private List<ChatModel> chatModels = new ArrayList<>();
    private String uid;
    private Context context;

    private ArrayList<String> destusers = new ArrayList<>();


    public ChatlistRecyclerViewAdapter(Context context) {

        this.context = context;

        pv = new UserInfoViewModel(context);
        pv.getUser(null, false);
        pv.getUserInfoLiveData().observe((LifecycleOwner) context, userInfo -> {
            userinfo = userInfo;
            uid = userinfo.getToken();

            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item : snapshot.getChildren()) {
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
        ItemChatBinding binding = ItemChatBinding.bind(view);
        return new CustomViewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String destuid = null;
        for (String user : chatModels.get(position).users.keySet()) {
            if (!user.equals(uid)) {
                destuid = user;
                destusers.add(destuid);
            }
        }
        // 메시지 내림순으로 정렬 후 마지막 메시지 키 가져옴

        Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
        commentMap.putAll(chatModels.get(position).comments);
        String lastMessageKey = (String) commentMap.keySet().toArray()[0];
        String message = chatModels.get(position).comments.get(lastMessageKey).message; //최근 메시지를 가져온다

        //viewHolder에 아이템을 세팅한다.
        ((CustomViewholder) holder)
                .setItem(new ViewModelProvider((Activity_chatlist) context,
                                new UserInfoViewModel
                                        .Factory(context))
                                .get(UserInfoViewModel.class),
                        context, destuid, message); //데이터를 세팅한다.
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

}
