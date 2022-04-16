package com.example.bookworm.Feed.Comments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Feed.items.Feed;
import com.example.bookworm.MainActivity;
import com.example.bookworm.ProfileInfoActivity;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.databinding.LayoutCommentItemBinding;
import com.example.bookworm.databinding.LayoutCommentSummaryBinding;
import com.example.bookworm.fragments.fragment_feed;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList commentList;
    Context context;

    public CommentAdapter(ArrayList data, Context c) {
        commentList =new ArrayList();
        commentList.addAll(data);
        context = c;
    }

    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case 0:
                view=inflater.inflate(R.layout.layout_comment_summary,parent,false);
                return new SummaryViewHolder(view,context);
            //댓글 뷰
            case 1:
                view = inflater.inflate(R.layout.layout_comment_item, parent, false);
                return new ItemViewHolder(view);
            //로딩바
            case 2:
                view = inflater.inflate(R.layout.layout_item_loading, parent, false);
                return new CommentAdapter.LoadingViewHolder(view);
        }
        return null;
    }

    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        if (holder instanceof ItemViewHolder) {
            Comment item = (Comment)commentList.get(safePosition);
            ((ItemViewHolder) holder).setItem(item);
        } else if (holder instanceof CommentAdapter.LoadingViewHolder) {
            showLoadingView((CommentAdapter.LoadingViewHolder) holder, safePosition);
        }else if(holder instanceof SummaryViewHolder){
            Feed item =(Feed) commentList.get(safePosition);
            ((SummaryViewHolder)holder).setItem(item);
        }
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setData(ArrayList data){
        commentList.clear();
        commentList.addAll(data);
    }
    //로딩바 클래스
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void showLoadingView(CommentAdapter.LoadingViewHolder viewHolder, int position) {
        //
    }

    public int getItemViewType(int pos) {
        if(commentList.get(pos) instanceof Feed){
            return 0;
        }
        else if (((Comment)commentList.get(pos)).getCommentID() != null) return 1;
        else return 2;
    }


    //뷰홀더 클래스 부분
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LayoutCommentItemBinding binding;

        //생성자를 만든다.
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=LayoutCommentItemBinding.bind(itemView);
        }

        //아이템을 세팅하는 메소드
        public void setItem(Comment item) {
            Glide.with(context).load(item.getUserThumb()).circleCrop().into(binding.imgProfile);
            binding.tvNickname.setText(item.getUserName());
            binding.tvCommentContent.setText(item.getContents());
            binding.tvDate.setText(item.getMadeDate());
            binding.llProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileInfoActivity.class);
                    intent.putExtra("userID", item.getUserToken());
                    context.startActivity(intent);
                }
            });
        }
    }

}
