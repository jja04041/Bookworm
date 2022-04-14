package com.example.bookworm.Feed.Comments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Feed.CustomPopup;
import com.example.bookworm.Feed.items.Feed;
import com.example.bookworm.MainActivity;
import com.example.bookworm.ProfileModifyActivity;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.databinding.LayoutCommentSummaryBinding;
import com.example.bookworm.fragments.fragment_feed;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;

import java.util.ArrayList;

public class SummaryViewHolder extends RecyclerView.ViewHolder {
    LayoutCommentSummaryBinding binding;
    Context context;
    FBModule fbModule = new FBModule(context);

    public SummaryViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        binding = LayoutCommentSummaryBinding.bind(itemView);
        this.context = context;
    }

    public void setItem(Feed item) {
        //책 표시
        Book book = item.getBook();
        binding.feedBookTitle.setText(book.getTitle());
        Glide.with(context).load(book.getImg_url()).into(binding.feedBookThumb);
        binding.feedBookAuthor.setText(book.getAuthor());
        //프로필 표시
        UserInfo userInfo=item.getCreator();
        Glide.with(context).load(userInfo.getProfileimg()).circleCrop().into(binding.ivProfileImage);
        binding.tvNickname.setText(userInfo.getUsername());
        //현재 로그인중인 유저
        UserInfo nowUser = new PersonalD(context).getUserInfo();
        //피드 요약
        binding.tvFeedtext.setText(item.getFeedText());
        if (item.getImgurl() != "")
            Glide.with(context).load(item.getImgurl()).into(binding.feedImage);
        else binding.feedImage.setVisibility(View.INVISIBLE);
        setLabel(item.getLabel());
        //댓글 표시

        //메뉴바
        binding.ivFeedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomPopup popup1=new CustomPopup(context ,view);
                popup1.setItems(fragment_feed.mContext,fbModule,item);
                popup1.setOnMenuItemClickListener(popup1);
                popup1.show();
            }
        });
    }

    private void setLabel(ArrayList<String> label) {
        binding.lllabel.removeAllViews(); //기존에 설정된 값을 초기화 시켜줌.
        int idx = label.indexOf("");
        for (int i = 0; i < idx; i++) {
            //뷰 생성
            TextView tv = new TextView(context);
            tv.setText(label.get(i)); //라벨에 텍스트 삽입
            tv.setBackground(context.getDrawable(R.drawable.label_design)); //디자인 적용
            tv.setBackgroundColor(Color.parseColor("#0F000000")); //배경색 적용
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //사이즈 설정
            params.setMargins(10, 0, 10, 0); //마진 설정
            tv.setLayoutParams(params); //설정값 뷰에 저장
            binding.lllabel.addView(tv); //레이아웃에 뷰 세팅
        }
    }
}