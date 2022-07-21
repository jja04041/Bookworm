package com.example.bookworm.bottomMenu.challenge.board;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Feed.CustomPopup;
import com.example.bookworm.bottomMenu.Feed.Fragment_feed;
import com.example.bookworm.bottomMenu.search.items.Book;
import com.example.bookworm.bottomMenu.search.subactivity.search_fragment_subActivity_result;
import com.example.bookworm.core.userdata.PersonalD;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.core.userdata.interfaces.UserContract;
import com.example.bookworm.core.userdata.modules.LoadUser;
import com.example.bookworm.databinding.LayoutCommentSummaryBinding;

public class Board_SummaryViewHolder extends RecyclerView.ViewHolder implements UserContract.View {
    LayoutCommentSummaryBinding binding;
    Context context;
    BoardFB boardFB = new BoardFB(context);
    LoadUser user;

    public Board_SummaryViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        binding = LayoutCommentSummaryBinding.bind(itemView);
        this.context = context; //subComment의 context  =>
        user = new LoadUser(this);
    }

    public void setItem(Board item) {
        //책 표시
        Book book = item.getBook();
        binding.feedBookTitle.setText(book.getTitle());
        Glide.with(context).load(book.getImg_url()).into(binding.feedBookThumb);
        binding.feedBookAuthor.setText(book.getAuthor());
        //프로필 표시
        user.getData(item.getUserToken(), null);

        //현재 로그인중인 유저
        UserInfo nowUser = new PersonalD(context).getUserInfo();
        //피드 요약
        binding.tvFeedtext.setText(item.getBoardText());
        if (item.getImgurl() != "") {
            Glide.with(context).load(item.getImgurl()).into(binding.feedImage);
            binding.feedImage.setVisibility(View.VISIBLE);
        } else binding.feedImage.setVisibility(View.INVISIBLE);

        binding.llbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, search_fragment_subActivity_result.class);
                intent.putExtra("itemid", book.getItemId());
                context.startActivity(intent);
            }
        });

//        setLabel(item.getLabel());
        //댓글 표시

        //메뉴바
        binding.ivFeedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomPopup popup1 = new CustomPopup(context, view);
                popup1.setItems(subactivity_challenge_board_comment.context, boardFB, item);
                popup1.setVisible(nowUser.getToken().equals(item.getMasterToken()));
                popup1.setDeleteVisible(nowUser.getToken().equals(item.getUserToken()));
                popup1.setOnMenuItemClickListener(popup1);
                popup1.show();
            }
        });
    }

    @Override
    public void showProfile(@NonNull UserInfo userInfo, @NonNull Boolean bool) {
        Glide.with(context).load(userInfo.getProfileimg()).circleCrop().into(binding.ivProfileImage);
        binding.tvNickname.setText(userInfo.getUsername());
    }
}