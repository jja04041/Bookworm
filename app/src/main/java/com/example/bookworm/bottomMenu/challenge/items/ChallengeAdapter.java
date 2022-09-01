package com.example.bookworm.bottomMenu.challenge.items;


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
import com.example.bookworm.databinding.LayoutItemLoadingBinding;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

import java.util.ArrayList;
import java.util.Calendar;

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
            View view = inflater.inflate(R.layout.layout_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    public void setListener(OnChallengeItemClickListener listener) {
        this.listener = listener;
    }

    //Arraylist에 있는 아이템을 뷰 홀더에 바인딩 하는 메소드
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
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
        LayoutItemLoadingBinding binding = LayoutItemLoadingBinding.bind(viewHolder.itemView);
        Sprite Circle = new Circle();
        Circle.setAnimationDelay(0);
        binding.progressBar.setIndeterminateDrawable(Circle);

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
        TextView tvBookTitle, tvCTitle, tvChallengeStartDate, tvChallengeEndDate, tvPerson, tvDday;
        ImageView ivThumb;

        //생성자를 만든다.
        public ItemViewHolder(@NonNull View itemView, final OnChallengeItemClickListener listener) {
            super(itemView);
            //findViewById를 통해 View와 연결
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvCTitle = itemView.findViewById(R.id.tvCtitle);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvChallengeStartDate = itemView.findViewById(R.id.tvChallengeStartDate);
            tvChallengeEndDate = itemView.findViewById(R.id.tvChallengeEndDate);
            tvPerson = itemView.findViewById(R.id.tvPerson);
            tvDday = itemView.findViewById(R.id.tvDday);
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
            tvBookTitle.setText(item.getBook().getTitle());
            tvDday.setText(countDday(item.getEndDate()));
            Glide.with(itemView).load(item.getBook().getImg_url()).into(ivThumb); //책 썸네일 설정
        }
    }

    //D-day 계산
    public String countDday(String EndDate) {
        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar todaCal = Calendar.getInstance(); //오늘날짜 가져오기
            Calendar ddayCal = Calendar.getInstance(); //오늘날짜를 가져와 변경시킴

            int year, month, day;

            year = Integer.parseInt(EndDate.substring(0, 4));
            month = Integer.parseInt(EndDate.substring(5, 7));
            day = Integer.parseInt(EndDate.substring(8, 10));

            month -= 1; // 받아온날짜에서 -1을 해줘야함.
            ddayCal.set(year, month, day);// D-day의 날짜를 입력

            long today = todaCal.getTimeInMillis() / 86400000; //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
            long dday = ddayCal.getTimeInMillis() / 86400000;
            long count = dday - today; // 오늘 날짜에서 dday 날짜를 빼주게 됨.

            if (count < 0) {
                return "종료됨";
            } else {
                return "D-" + String.valueOf(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
