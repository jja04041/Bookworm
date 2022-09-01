package com.example.bookworm.bottomMenu.search.items.book;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;

import java.util.ArrayList;

public class RecomBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnBookItemClickListener {
    ArrayList<Book> BookList;
    Context context;
    OnBookItemClickListener listener;

    public RecomBookAdapter(ArrayList<Book> data, Context c) {
        BookList = data;
        context = c;
    }

    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_favoriteitem, parent, false);
        return new ItemViewHolder(view, listener);
    }

    public void setListener(OnBookItemClickListener listener) {
        this.listener = listener;
    }

    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        Book item = BookList.get(safePosition);
        ((ItemViewHolder) holder).setItem(item);
    }

    @Override
    public int getItemCount() {
        return BookList.size();
    }

    public void addItem(Book item) {
        BookList.add(item);
    }

    @Override
    public void onItemClick(BookAdapter.ItemViewHolder holder, View view, int position) {
    }

    //이곳에서 처리됨
    public void onItemClick(RecomBookAdapter.ItemViewHolder holder, View view, int position) {

    }

    //뷰홀더 클래스 부분
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBook;
        TextView tvBookTitle;

        //생성자를 만든다.
        public ItemViewHolder(@NonNull View itemView, final OnBookItemClickListener listener) {
            super(itemView);
            //findViewById를 통해 View와 연결
            ivBook = itemView.findViewById(R.id.ivBook);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);

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
            //북 클래스의 각 아이템을 세팅하는 메소드

        }

        public void setItem(Book item) {
            Glide.with(itemView).load(item.getImg_url()).into(ivBook);
            tvBookTitle.setText(item.getTitle());
        }
    }
}
