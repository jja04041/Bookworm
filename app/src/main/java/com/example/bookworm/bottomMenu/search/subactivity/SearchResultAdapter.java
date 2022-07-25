package com.example.bookworm.bottomMenu.search.subactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.Feed.items.Feed;
import com.example.bookworm.bottomMenu.Feed.views.FeedViewModel;
import com.example.bookworm.bottomMenu.challenge.board.Board;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.core.userdata.interfaces.UserContract;
import com.example.bookworm.databinding.SearchFragmentResultFeedBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnSearchResultItemClickListener {
    ArrayList<Feed> feedList;
    Context context;
    OnSearchResultItemClickListener listener;
    FeedViewModel pv;
    LifecycleOwner lifecycleOwner;
    String dateDuration; //작성시간 n분, n시간, 등 으로 표시

    public SearchResultAdapter(ArrayList<Feed> data, Context c, LifecycleOwner owner) {
        feedList = data;
        context = c;
        pv = new FeedViewModel(context);
        lifecycleOwner = owner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_fragment_result_feed, parent, false);
        return new SearchResultAdapter.ItemViewHolder(view, listener);
    }

    public void setListener(OnSearchResultItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        Feed item = feedList.get(safePosition);
        ((SearchResultAdapter.ItemViewHolder) holder).setItem(item);

    }


    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public void onItemClick(ItemViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements UserContract.View {

        SearchFragmentResultFeedBinding binding;
        MutableLiveData<UserInfo> feedUserInfo = new MutableLiveData<>();

        public ItemViewHolder(@NonNull View itemView, final OnSearchResultItemClickListener listener) {
            super(itemView);
            binding = SearchFragmentResultFeedBinding.bind(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    //리스너 인터페이스 구현
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onItemClick(ItemViewHolder.this, view, position);
                        }
                    }
                }
            });
        }

        public void setItem(Feed item) {
            binding.tvCommentContent.setText(item.getFeedText());
            getDateDuration(item.getDate());
            binding.tvDate.setText(dateDuration + " 전");

            pv.getUser(item.getUserToken(), feedUserInfo);
            feedUserInfo.observe(lifecycleOwner, feedUserInfo -> {
                showProfile(feedUserInfo, false); //프로필 설정
            });
        }


        @Override
        public void showProfile(@NonNull UserInfo userInfo, @NonNull Boolean bool) {
            Glide.with(context).load(userInfo.getProfileimg()).circleCrop().into(binding.imgProfile);
            binding.tvNickname.setText(userInfo.getUsername());
            setMedal(userInfo);
        }

        //시간차 구하기 n분 전, n시간 전 등등
        public void getDateDuration(String createdTime) {
            long now = System.currentTimeMillis();
            Date dateNow = new Date(now);//현재시각
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date dateCreated = dateFormat.parse(createdTime);
                long duration = dateNow.getTime() - dateCreated.getTime();//시간차이 mills

                if (duration / 1000 / 60 == 0) {
                    dateDuration = "방금";
                } else if (duration / 1000 / 60 <= 59) {
                    dateDuration = String.valueOf(duration / 1000 / 60) + "분";
                } else if (duration / 1000 / 60 / 60 <= 23) {
                    dateDuration = String.valueOf(duration / 1000 / 60 / 60) + "시간";
                } else if (duration / 1000 / 60 / 60 / 24 <= 29) {
                    dateDuration = String.valueOf(duration / 1000 / 60 / 60 / 24) + "일";
                } else if (duration / 1000 / 60 / 60 / 24 / 30 <= 12) {
                    dateDuration = String.valueOf(duration / 1000 / 60 / 60 / 24 / 30) + "개월";
                } else {
                    dateDuration = String.valueOf(duration / 1000 / 60 / 60 / 24 / 30 / 12) + "년";
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
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


    class BoardDiffCallback extends DiffUtil.ItemCallback<Board> {

        @Override
        public boolean areItemsTheSame(@NonNull Board oldItem, @NonNull Board newItem) {
            return oldItem.getBoardID() == newItem.getBoardID();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Board oldItem, @NonNull Board newItem) {
            return oldItem == newItem;
        }
    }


}
