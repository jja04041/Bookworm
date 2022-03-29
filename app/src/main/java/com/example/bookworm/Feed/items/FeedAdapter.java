package com.example.bookworm.Feed.items;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.Feed.ViewHolders.ItemViewHolder;
import com.example.bookworm.R;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Feed> FeedList;
    Context context;

    public FeedAdapter(ArrayList<Feed> data, Context c) {
        FeedList = new ArrayList();
        FeedList.addAll(data);
        context = c;
    }

    //뷰홀더가 만들어질때 작동하는 메서드
    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            //이미지가 있는 피드
            case 1:
                view = inflater.inflate(R.layout.layout_feed, parent, false);
                return new ItemViewHolder(view, context);
            //로딩바
            case 2:
                view = inflater.inflate(R.layout.layout_item_loading, parent, false);
                return new LoadingViewHolder(view);
        }
        return null;
    }

    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        if (holder instanceof ItemViewHolder) {
            Feed item = FeedList.get(safePosition);
            if (item.getImgurl() != null) {
                ((ItemViewHolder) holder).setVisibillity(true);
            } else {
                ((ItemViewHolder) holder).setVisibillity(false);
            }
            ((ItemViewHolder) holder).setItem(item);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, safePosition);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        int safePosition = holder.getAdapterPosition();
        if (holder instanceof ItemViewHolder) {
            Feed item = FeedList.get(safePosition);
            if (item.getImgurl() != null) {
                Log.d(safePosition+"위치",item.getImgurl() );
                ((ItemViewHolder) holder).setVisibillity(true);
            } else {
                ((ItemViewHolder) holder).setVisibillity(false);
            }
            ((ItemViewHolder) holder).setItem(item);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, safePosition);
        }
    }

    @Override
    public int getItemCount() {
        return FeedList.size();
    }

    public void addList(ArrayList<Feed> list) {
        FeedList.addAll(list);
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
        if (FeedList.get(pos).getFeedID() != null) return 1;
        else return 2;
    }

    public void deleteLoading() {
        FeedList.remove(FeedList.size() - 1);
        // 로딩이 완료되면 프로그레스바를 지움
    }

}
