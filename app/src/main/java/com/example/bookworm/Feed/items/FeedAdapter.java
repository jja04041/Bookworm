package com.example.bookworm.Feed.items;


import android.content.Context;
import android.graphics.Color;
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
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.databinding.FragmentFeedBinding;
import com.example.bookworm.databinding.LayoutFeedBinding;

import java.util.ArrayList;

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
        int safePosition=holder.getAdapterPosition();
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

    public void addItem(Feed item) {
        FeedList.add(item);
    }



//    @Override
//    //아이템 선택 시 보여줄 화면 구성 => 아마 인텐트로 넘기지 않을까?
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

        //생성자를 만든다.
        public ItemViewHolder(@NonNull View itemView, final OnFeedItemClickListener listener) {
            super(itemView);
            //findViewById를 통해 View와 연결
            //아이템 선택 시 리스너
            binding=LayoutFeedBinding.bind(itemView);
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


        }
        //아이템을 세팅하는 메소드
        public void setItem(Feed item) {
            Book book=item.getBook();
            UserInfo user=item.getCreator();
            binding.feedBookAuthor.setText(book.getAuthor());
            Glide.with(itemView).load(book.getImg_url()).into(binding.feedBookThumb); //책 썸네일 설정
            if(item.getImgurl()!=null) Glide.with(itemView).load(item.getImgurl()).into(binding.feedImage);
            else binding.feedImage.setVisibility(View.INVISIBLE);
            binding.feedBookTitle.setText(book.getTitle());
            binding.tvFeedtext.setText(item.getFeedText());
            setLabel(item.getLabel());
            binding.tvNickname.setText(user.getUsername());
            Glide.with(itemView).load(user.getProfileimg()).into(binding.ivProfileImage);

        }
        private void setLabel(ArrayList<String> label){
            binding.lllabel.setOrientation(LinearLayout.HORIZONTAL);
            for(int i=0;i<label.size();i++){
                TextView tv=new TextView(context);
                tv.setText(label.get(i));
                tv.setBackground(context.getDrawable(R.drawable.label_design));
                tv.setBackgroundColor(Color.parseColor("#0F000000"));
                LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10,0,10,0);
                tv.setLayoutParams(params);
                tv.setId(View.generateViewId());
                binding.lllabel.addView(tv);
            }
        }
    }
}
