package com.example.bookworm.Feed.Comments;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Feed.items.Feed;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.databinding.LayoutCommentSummaryBinding;

public class SummaryViewHolder extends RecyclerView.ViewHolder{
    LayoutCommentSummaryBinding binding;
    Context context;
    public SummaryViewHolder(@NonNull View itemView, Context context){
        super(itemView);
        binding=LayoutCommentSummaryBinding.bind(itemView);
        this.context=context;
    }
    public void setItem(Feed item){
        //책 표시
        Book book = item.getBook();
        binding.feedBookTitle.setText(book.getTitle());
        Glide.with(context).load(book.getImg_url()).into(binding.feedBookThumb);
        binding.feedBookAuthor.setText(book.getAuthor());
        //피드 요약
        binding.tvFeedtext.setText(item.getFeedText());
        if (item.getImgurl() != "") Glide.with(context).load(item.getImgurl()).into(binding.feedImage);
        else  binding.feedImage.setVisibility(View.INVISIBLE);
        //댓글 표시
    }
}