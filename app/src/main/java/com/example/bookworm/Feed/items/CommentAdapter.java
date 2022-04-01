//package com.example.bookworm.Feed.items;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.bookworm.Feed.ViewHolders.ItemViewHolder;
//import com.example.bookworm.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CommentAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
//    ArrayList<Comment> commentList;
//    Context context;
//
//    public CommentAdapter(ArrayList<Comment> data, Context c) {
//        commentList = data;
//        context = c;
//    }
//
//    //뷰홀더가 만들어질때 작동하는 메서드
//    //화면을 인플레이트하고 인플레이트된 화면을 리턴한다.
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = null;
//        switch (viewType) {
//            //이미지가 있는 피드
//            case 1:
//                view = inflater.inflate(R.layout.layout_feed, parent, false);
//                return new ItemViewHolder(view, context);
//            //로딩바
//            case 2:
//                view = inflater.inflate(R.layout.layout_item_loading, parent, false);
//                return new CommentAdapter.LoadingViewHolder(view);
//        }
//        return null;
//    }
//
//    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        int safePosition = holder.getAdapterPosition();
//        if (holder instanceof ItemViewHolder) {
//            Comment item = commentList.get(safePosition);
//
//            ((ItemViewHolder) holder).setItem(item);
//        } else if (holder instanceof CommentAdapter.LoadingViewHolder) {
//            showLoadingView((CommentAdapter.LoadingViewHolder) holder, safePosition);
//        }
//    }
//
////    @Override
////    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
////        super.onViewRecycled(holder);
////        int safePosition = holder.getAdapterPosition();
////        if (holder instanceof ItemViewHolder) {
////            Feed item = FeedList.get(safePosition);
////            if (item.getImgurl() != null) {
////                Log.d(safePosition+"위치",item.getImgurl() );
////                ((ItemViewHolder) holder).setVisibillity(true);
////            } else {
////                ((ItemViewHolder) holder).setVisibillity(false);
////            }
////            ((ItemViewHolder) holder).setItem(item);
////        } else if (holder instanceof FeedAdapter.LoadingViewHolder) {
////            showLoadingView((FeedAdapter.LoadingViewHolder) holder, safePosition);
////        }
////    }
//
//    @Override
//    public int getItemCount() {
//        return commentList.size();
//    }
//
//
//    //로딩바 클래스
//    private class LoadingViewHolder extends RecyclerView.ViewHolder {
//        public LoadingViewHolder(@NonNull View itemView) {
//            super(itemView);
//        }
//    }
//
//    private void showLoadingView(CommentAdapter.LoadingViewHolder viewHolder, int position) {
//        //
//    }
//
//    public int getItemViewType(int pos) {
//        if (commentList.get(pos).getUserToken() != null) return 1;
//        else return 2;
//    }
//
//    public void deleteLoading() {
//        commentList.remove(commentList.size() - 1);
//        // 로딩이 완료되면 프로그레스바를 지움
//    }
//}
