package com.example.bookworm.chat.newchat;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.ItemChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomViewholder extends RecyclerView.ViewHolder{


    private ItemChatBinding binding;
    public CustomViewholder(ItemChatBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
    public void setItem(UserInfoViewModel uv,Context context, String destUid, String message) {
        MutableLiveData<UserInfo> data=new MutableLiveData<>();
        uv.getUser(destUid,data,true); //외부에서 해당 사용자에 대한 데이터를 가져온다
//        FirebaseDatabase.getInstance()
//                .getReference()
//                .child("users")
//                .child(destUid)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        uv.getUser(destUid, true); //외부에서 해당 사용자에 대한 데이터를 가져온다
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
        //새로운 데이터가 도착하는 경우
        data.observe((LifecycleOwner) context, opponent -> {
            Glide.with(context)
                    .load(opponent.getProfileimg())
                    .apply(new RequestOptions().circleCrop())
                    .into(binding.itemChatImageView);
            binding.itemChatTvTitle.setText(opponent.getUsername());
        });
        binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MessageActivity.class);
            intent.putExtra("destuid", destUid);
            context.startActivity(intent);
        });
        binding.itemChatTvComment.setText(message);
    }

}
