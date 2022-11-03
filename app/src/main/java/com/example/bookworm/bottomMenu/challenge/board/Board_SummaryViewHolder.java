package com.example.bookworm.bottomMenu.challenge.board;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.search.bookitems.Book;
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
        Glide.with(context).load(book.getImgUrl()).into(binding.feedBookThumb);
        binding.feedBookAuthor.setText(book.getAuthor());
        //프로필 표시
        user.getData(item.getUserToken(), null);

        //현재 로그인중인 유저
        UserInfo nowUser = new PersonalD(context).getUserInfo();
        //피드 요약
        binding.tvFeedText.setText(item.getBoardText());
        if (item.getImgurl() != "") {
            Glide.with(context).load(item.getImgurl()).into(binding.ivFeedImage);
            binding.ivFeedImage.setVisibility(View.VISIBLE);
        } else binding.ivFeedImage.setVisibility(View.INVISIBLE);

        binding.llbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, search_fragment_subActivity_result.class);
//                intent.putExtra("itemid", book.getItemId());
//                context.startActivity(intent);
            }
        });

//        setLabel(item.getLabel());
        //댓글 표시

        //메뉴바
        binding.btnFeedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CustomPopup popup1 = new CustomPopup(context, view);
//                popup1.setItems(subactivity_challenge_board_comment.context, boardFB, item);
//                popup1.setVisible(nowUser.getToken().equals(item.getMasterToken()));
//                popup1.setDeleteVisible(nowUser.getToken().equals(item.getUserToken()));
//                popup1.setOnMenuItemClickListener(popup1);
//                popup1.show();
            }
        });
    }

    @Override
    public void showProfile(@NonNull UserInfo userInfo, @NonNull Boolean bool) {
        Glide.with(context).load(userInfo.getProfileimg()).circleCrop().into(binding.ivProfileImage);
        binding.tvNickname.setText(userInfo.getUsername());
        setMedal(userInfo);
    }

    //메달 표시 유무에 따른 세팅
    private void setMedal(UserInfo userInfo) {
        if (userInfo.getMedalAppear()) { //메달을 표시한다면
            binding.ivMedal.setVisibility(View.VISIBLE);
            switch (Integer.parseInt(String.valueOf(userInfo.getTier()))) { //티어 0 ~ 5에 따라 다른 메달이 나오게
                case 1:
                    binding.ivMedal.setImageResource(R.drawable.medal_bronze);
                    break;
                case 2:
                    binding.ivMedal.setImageResource(R.drawable.medal_silver);
                    break;
                case 3:
                    binding.ivMedal.setImageResource(R.drawable.medal_gold);
                    break;
                case 4:
//                    binding.ivMedal.setImageResource(R.drawable.medal_platinum);
                    break;
                case 5:
//                    binding.ivMedal.setImageResource(R.drawable.medal_diamond);
                    break;
                default: //티어가 없을때
                    binding.ivMedal.setImageResource(0);
            }
        } else { //메달을 표시하지 않을거라면
            binding.ivMedal.setVisibility(View.GONE);
            binding.ivMedal.setImageResource(0);
        }
    }
}