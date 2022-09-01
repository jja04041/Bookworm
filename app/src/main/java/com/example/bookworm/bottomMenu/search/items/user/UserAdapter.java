package com.example.bookworm.bottomMenu.search.items.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.FragmentSearchPageUserItemBinding;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>implements OnUserItemClickListener {
    ArrayList<UserInfo> userlist;
    Context context;
    FragmentSearchPageUserItemBinding binding;
    OnUserItemClickListener listener;


    public UserAdapter(ArrayList<UserInfo> data, Context c) {
        userlist = data;
        context = c;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_search_page_user_item, parent, false);
        return new UserAdapter.ItemViewHolder(view, listener);
    }

    public void setListener(OnUserItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        UserInfo item = userlist.get(safePosition);
        ((UserAdapter.ItemViewHolder) holder).setItem(item);

    }


    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public void onItemClick(UserAdapter.ItemViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView, final OnUserItemClickListener listener) {
            super(itemView);
            binding = FragmentSearchPageUserItemBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    //리스너 인터페이스 구현
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onItemClick(UserAdapter.ItemViewHolder.this, view, position);
                        }
                    }
                }
            });
        }

        public void setItem(UserInfo item) {
            Glide.with(context).load(item.getProfileimg()).into(binding.ivProfileImg);
            binding.tvProfileID.setText(item.getUsername());

            binding.frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(context, subactivity_comment.class);
//                    intent.putExtra("item", item);
//                    intent.putExtra("position", getAbsoluteAdapterPosition());
//                    context.startActivity(intent);
                }
            });
        }


    }

    public void setData(ArrayList data) {
        userlist.clear();
        userlist.addAll(data);
    }


}
