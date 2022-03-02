package com.example.bookworm.Challenge.items;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Challenge.Challenge;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.items.OnBookItemClickListener;
import com.example.bookworm.Search.items.RecomBookAdapter;
import com.example.bookworm.modules.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChallengeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnChallengeItemClickListener {
    ArrayList<Challenge> ChallengeList;
    Context context;
    OnChallengeItemClickListener listener;

    public ChallengeAdapter(ArrayList<Challenge> data, Context c) {
        ChallengeList = data;
        context = c;
    }

    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            View view = inflater.inflate(R.layout.layout_challenge_item, parent, false);
            return new ItemViewHolder(view, listener);
        } else {
            View view = inflater.inflate(R.layout.search_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    public void setListener(OnChallengeItemClickListener listener) {
        this.listener = listener;
    }

    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition=holder.getAdapterPosition();
        if (holder instanceof ItemViewHolder) {
            Challenge item = ChallengeList.get(safePosition);
            ((ItemViewHolder) holder).setItem(item);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, safePosition);
        }


    }
    @Override
    public int getItemCount() {
        return ChallengeList.size();
    }

    public void addItem(Challenge item) {
        ChallengeList.add(item);
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
        if (ChallengeList.get(pos).getTitle() != null) return 0;
        else return 1;
    }

    public void deleteLoading() {
        ChallengeList.remove(ChallengeList.size() - 1);
        // 로딩이 완료되면 프로그레스바를 지움
    }

    //뷰홀더 클래스 부분
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle, tvCTitle, tvChallengeStartDate, tvChallengeEndDate,tvPerson;
        ImageView ivThumb;

        //생성자를 만든다.
        public ItemViewHolder(@NonNull View itemView, final OnChallengeItemClickListener listener) {
            super(itemView);
            //findViewById를 통해 View와 연결
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvCTitle = itemView.findViewById(R.id.tvCtitle);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvChallengeStartDate = itemView.findViewById(R.id.tvChallengeStartDate);
            tvChallengeEndDate = itemView.findViewById(R.id.tvChallengeEndDateDate);
            tvPerson=itemView.findViewById(R.id.tvPerson);
            //아이템 선택 시 리스너
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    //리스너 인터페이스 구현
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onItemClick(ItemViewHolder.this, view, position);
                            notifyItemChanged(position);
                        }
                    }
                }
            });


        }
        //아이템을 세팅하는 메소드
        public void setItem(Challenge item) {
            tvCTitle.setText(item.getTitle());
            tvChallengeStartDate.setText(item.getStartDate().substring(5));
            tvChallengeEndDate.setText(item.getEndDate().substring(5));
            tvPerson.setText(String.valueOf(item.getCurrentPart().size()));
            tvBookTitle.setText(item.getBookTitle());
            Glide.with(itemView).load(item.getBookThumb()).into(ivThumb); //책 썸네일 설정

        }
    }
}