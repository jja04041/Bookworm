package com.example.bookworm.Feed.items;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_result;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.databinding.FragmentFeedBinding;
import com.example.bookworm.databinding.LayoutFeedBinding;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFeedItemClickListener {
    ArrayList<Feed> FeedList;
    Context context;
    OnFeedItemClickListener listener;

    public FeedAdapter(ArrayList<Feed> data, Context c) {
        FeedList = data;
        context = c;
    }

    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            View view = inflater.inflate(R.layout.layout_feed, parent, false);
            return new ItemViewHolder(view, listener);
        } else {
            View view = inflater.inflate(R.layout.layout_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    public void setListener(OnFeedItemClickListener listener) {
        this.listener = listener;
    }

    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        if (holder instanceof ItemViewHolder) {
            Feed item = FeedList.get(safePosition);
            ((ItemViewHolder) holder).setItem(item);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, safePosition);
        }
    }

    @Override
    public int getItemCount() {
        return FeedList.size();
    }


    public void onItemClick(ItemViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    //로딩바 클래스
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //
    }

    public int getItemViewType(int pos) {
        if (FeedList.get(pos).getFeedID() != null) return 0;
        else return 1;
    }

    public void deleteLoading() {
        FeedList.remove(FeedList.size() - 1);
        // 로딩이 완료되면 프로그레스바를 지움
    }

    //뷰홀더 클래스 부분
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LayoutFeedBinding binding;
        UserInfo nowUser;
        ArrayList<String> strings;
        //생성자를 만든다.
        public ItemViewHolder(@NonNull View itemView, final OnFeedItemClickListener listener) {
            super(itemView);
            binding = LayoutFeedBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    //리스너 인터페이스 구현
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
//                            listener.onItemClick(ItemViewHolder.this, view, position);
//                            notifyItemChanged(position);
                        }
                    }
                }
            });
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
                binding.ivLike.setBackground(context.getDrawable(R.drawable.icon_like_red));
                binding.ivLike.setTag("pressed");
            } else {
                binding.ivLike.setBackground(context.getDrawable(R.drawable.icon_like));
                binding.ivLike.setTag("depressed");
            }

            binding.lllike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FBModule fbModule = new FBModule(context);
                    nowUser = new PersonalD(context).getUserInfo();
                    strings=nowUser.getLikedPost();
                    Map map = new HashMap();
                    Integer likeCount = Integer.parseInt(binding.tvLike.getText().toString());
                    if ((String) binding.ivLike.getTag() == "depressed") {
                        //현재 좋아요를 누르지 않은 상태
                        likeCount += 1;
                        binding.ivLike.setTag("pressed");
                        strings.add(item.getFeedID());
                        binding.ivLike.setBackground(context.getDrawable(R.drawable.icon_like_red));
                    } else {
                        //현재 좋아요를 누른 상태
                        likeCount -= 1;
                        binding.ivLike.setTag("depressed");
                        strings.removeAll(Arrays.asList(item.getFeedID()));
                        strings.remove(item.getFeedID());
                        binding.ivLike.setBackground(context.getDrawable(R.drawable.icon_like));
                    }
                    nowUser.setLikedPost(strings);
                    map.put("nowUser", nowUser);
                    binding.tvLike.setText(String.valueOf(likeCount));
                    map.put("likeCount", likeCount);
                    fbModule.readData(1, map, item.getFeedID());
                    new PersonalD(context).saveUserInfo(nowUser);

                }
            });
            if (item.getImgurl() != null)
                Glide.with(itemView).load(item.getImgurl()).into(binding.feedImage); //프로필 사진 보여줌
            else binding.feedImage.setVisibility(View.INVISIBLE);
            binding.tvFeedtext.setText(item.getFeedText());
            setLabel(item.getLabel()); //라벨 세팅
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
}
