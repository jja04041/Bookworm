package com.example.bookworm.Achievement.Adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.Achievement.Listener.OnViewHolderItemClickListener;
import com.example.bookworm.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<ItemData> listData = new ArrayList<>();

    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.onBind(listData.get(position), position, selectedItems);

        // 뷰홀더에 아이템클릭리스너 인터페이스 붙이기
        viewHolder.setOnViewHolderItemClickListener(new OnViewHolderItemClickListener() {
            @Override
            public void onViewHolderItemClick() {
                if (selectedItems.get(viewHolder.getAdapterPosition())) {
                    // 펼쳐진 Item을 클릭 시
                    selectedItems.delete(viewHolder.getAdapterPosition());
                } else {
                    // 직전의 클릭됐던 Item의 클릭상태를 지움
                    selectedItems.delete(prePosition);
                    // 클릭한 Item의 position을 저장
                    selectedItems.put(viewHolder.getAdapterPosition(), true);
                }
                // 해당 포지션의 변화를 알림
                if (prePosition != -1) notifyItemChanged(prePosition);
                notifyItemChanged(viewHolder.getAdapterPosition());
                // 클릭된 position 저장
                prePosition = viewHolder.getAdapterPosition();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(ItemData itemData) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(itemData);
    }
}