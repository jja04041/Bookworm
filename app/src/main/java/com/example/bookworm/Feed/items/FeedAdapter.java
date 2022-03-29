package com.example.bookworm.Feed.items;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.Challenge.subActivity.subactivity_challenge_challengeinfo;
import com.example.bookworm.Feed.ViewHolders.ItemNoImgViewHolder;
import com.example.bookworm.Feed.ViewHolders.ItemViewHolder;
import com.example.bookworm.R;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_result;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.databinding.FragmentFeedBinding;
import com.example.bookworm.databinding.LayoutFeedBinding;
import com.example.bookworm.databinding.SubactivityFeedCreateBinding;
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
    Dialog customDialog;

    public FeedAdapter(ArrayList<Feed> data, Context c) {
        FeedList = data;
        context = c;
    }

    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            //이미지가 없는 피드
            case 0:
                view = inflater.inflate(R.layout.layout_feed_no_image, parent, false);
                return new ItemNoImgViewHolder(view, listener, context);
            //이미지가 있는 피드
            case 1:
                view = inflater.inflate(R.layout.layout_feed, parent, false);
                return new ItemViewHolder(view, listener, context);
            //로딩바
            case 2:
                view = inflater.inflate(R.layout.layout_item_loading, parent, false);
                return new LoadingViewHolder(view);
        }
        return null;
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
        }
        else if(holder instanceof  ItemNoImgViewHolder){
            Feed item = FeedList.get(safePosition);
            ((ItemNoImgViewHolder) holder).setItem(item);
        }
        else if (holder instanceof LoadingViewHolder) {
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

    @Override
    public void onItemClick(ItemNoImgViewHolder holder, View view, int position) {

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
        if (FeedList.get(pos).getFeedID() != null&&FeedList.get(pos).getImgurl() == null) return 0;
        else if (FeedList.get(pos).getFeedID() != null) return 1;
        else return 2;
    }

    public void deleteLoading() {
        FeedList.remove(FeedList.size() - 1);
        // 로딩이 완료되면 프로그레스바를 지움
    }

    //뷰홀더 클래스 부분

}
