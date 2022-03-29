package com.example.bookworm.Feed.ViewHolders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Feed.items.Feed;
import com.example.bookworm.Feed.items.OnFeedItemClickListener;
import com.example.bookworm.Feed.likeCounter;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_result;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.databinding.LayoutFeedBinding;
import com.example.bookworm.databinding.LayoutFeedNoImageBinding;
import com.example.bookworm.modules.personalD.PersonalD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    LayoutFeedBinding binding;
    UserInfo nowUser;
    ArrayList<String> strings;
    Boolean liked = false;
    int limit = 0;
    Boolean restricted = false;
    Context context;
    Dialog customDialog;
    ArrayList<Feed> FeedList;

    //생성자를 만든다.
    public ItemViewHolder(@NonNull View itemView, final OnFeedItemClickListener listener, Context context,ArrayList<Feed> list) {
        super(itemView);
        binding = LayoutFeedBinding.bind(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                FeedList=list;
                //리스너 인터페이스 구현
                if (position != RecyclerView.NO_POSITION) {
                    if (listener != null) {
//                            listener.onItemClick(ItemViewHolder.this, view, position);
//                            notifyItemChanged(position);
                    }
                }
            }
        });
        this.context=context;
        nowUser = new PersonalD(context).getUserInfo();
    }

    //아이템을 세팅하는 메소드
    public void setItem(Feed item) {
        //피드에 삽입한 책
        Book book = item.getBook();
        binding.feedBookAuthor.setText(book.getAuthor());
        Glide.with(itemView).load(book.getImg_url()).into(binding.feedBookThumb); //책 썸네일 설정
        binding.feedBookTitle.setText(book.getTitle());
        binding.llbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, search_fragment_subActivity_result.class);
                intent.putExtra("itemid", book.getItemId());
                context.startActivity(intent);
            }
        });
        //작성자 UserInfo
        UserInfo user = item.getCreator();
        binding.tvNickname.setText(user.getUsername());
        Glide.with(itemView).load(user.getProfileimg()).into(binding.ivProfileImage);
        //피드 내용

        binding.tvLike.setText(String.valueOf(item.getLikeCount()));

        if (nowUser.getLikedPost().contains(item.getFeedID())) {
            binding.btnLike.setBackground(context.getDrawable(R.drawable.icon_like_red));
            binding.btnLike.setTag("pressed");
            liked = true;
        } else {
            binding.btnLike.setBackground(context.getDrawable(R.drawable.icon_like));
            binding.btnLike.setTag("depressed");
            liked = false;
        }
        //좋아요 표시 관리
        binding.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlLike(item,binding);
            }
        });
        if (item.getImgurl() != null)
            Glide.with(itemView).load(item.getImgurl()).into(binding.feedImage); //프로필 사진 보여줌
        else binding.feedImage.setVisibility(View.INVISIBLE);
        binding.tvFeedtext.setText(item.getFeedText());
        setLabel(item.getLabel()); //라벨 세팅
    }

    private void controlLike(Feed item, Object bind) {
        LayoutFeedNoImageBinding binding;
        LayoutFeedBinding binding1;
        Button btnLike=null;
        TextView tvLike=null;
        if (bind instanceof LayoutFeedBinding) {
            binding1 = (LayoutFeedBinding) bind;
            btnLike=binding1.btnLike;
            tvLike=binding1.tvLike;
        }
        else if (bind instanceof LayoutFeedNoImageBinding) {
            binding = (LayoutFeedNoImageBinding) bind;
            btnLike=binding.btnLike;
            tvLike=binding.tvLike;
        }

        if (limit < 5) {
            limit += 1;
            nowUser = new PersonalD(context).getUserInfo();
            strings = nowUser.getLikedPost();
            Map map = new HashMap();
            Integer likeCount = Integer.parseInt(tvLike.getText().toString());

            if ((String) btnLike.getTag() == "depressed") {
                //현재 좋아요를 누르지 않은 상태
                likeCount += 1;
                liked = true;
                btnLike.setTag("pressed");
                strings.add(item.getFeedID());
                btnLike.setBackground(context.getDrawable(R.drawable.icon_like_red));
            } else {
                //현재 좋아요를 누른 상태
                likeCount -= 1;
                liked = false;
                btnLike.setTag("depressed");
                strings.removeAll(Arrays.asList(item.getFeedID()));
                strings.remove(item.getFeedID());
                btnLike.setBackground(context.getDrawable(R.drawable.icon_like));
            }
            nowUser.setLikedPost(strings);
            map.put("nowUser", nowUser);
            tvLike.setText(String.valueOf(likeCount));
            map.put("liked", liked);
            new PersonalD(context).saveUserInfo(nowUser);
            new likeCounter().updateCounter(map, item.getFeedID());
        } else {
            new AlertDialog.Builder(context)
                    .setMessage("커뮤니티 활동 보호를 위해 잠시 후에 다시 시도해주세요")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            if (!restricted) {
                restricted = true;
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        limit = 0;
                        restricted = false;
                    }
                }, 10000);
            }
        }
    }

    public void showcustomDialog(int position) {
        customDialog.show(); // 다이얼로그 띄우기
//        EditText edtComment = customDialog.findViewById(R.id.edtComment);
        TextView tvCommentCount = customDialog.findViewById(R.id.tvCommentCount);
        tvCommentCount.setText(FeedList.get(position).getFeedID());
//        edtComment.setText(FeedList.get(position).getFeedID());

    }
    //라벨을 동적으로 생성
    private void setLabel(ArrayList<String> label) {
        for (int i = 0; i < label.size(); i++) {
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