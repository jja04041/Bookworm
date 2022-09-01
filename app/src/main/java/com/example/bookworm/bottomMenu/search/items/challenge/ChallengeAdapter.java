package com.example.bookworm.bottomMenu.search.items.challenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.challenge.items.Challenge;
import com.example.bookworm.databinding.FragmentSearchChallengeItemBinding;

import java.util.ArrayList;

public class ChallengeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnChallengeItemClickListener {
    ArrayList<Challenge> challengelist;
    Context context;
    FragmentSearchChallengeItemBinding binding;
    OnChallengeItemClickListener listener;


    public ChallengeAdapter(ArrayList<Challenge> data, Context c) {
        challengelist = data;
        context = c;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_search_challenge_item, parent, false);
        return new ChallengeAdapter.ItemViewHolder(view, listener);
    }

    public void setListener(OnChallengeItemClickListener listener) { this.listener = listener; }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int safePosition = holder.getAdapterPosition();
        Challenge item = challengelist.get(safePosition);
        ((ChallengeAdapter.ItemViewHolder) holder).setItem(item);

    }


    @Override
    public int getItemCount() {
        return challengelist.size();
    }

    public void onItemClick(ChallengeAdapter.ItemViewHolder holder, View view, int position) {
        if (listener != null) {
            //listener.onItemClick(holder, view, position);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView, final OnChallengeItemClickListener listener) {
            super(itemView);
            binding = FragmentSearchChallengeItemBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    //리스너 인터페이스 구현
                    if (position != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            //listener.onItemClick(ChallengeAdapter.ItemViewHolder.this, view, position);
                        }
                    }
                }
            });
        }

        public void setItem(Challenge item) {
            Glide.with(context).load(item.getBook().getImg_url()).into(binding.ivBookThumb);
            binding.tvChallengeTitle.setText(item.getTitle());
            binding.tvBookTitle.setText(item.getBook().getTitle());
            binding.tvChallengeDescription.setText(item.getChallengeDescription());
            binding.tvChallengeStartDate.setText(item.getStartDate());
            binding.tvChallengeEndDate.setText(item.getEndDate());
//        binding.tvDday.setText(item.getStartDate());

            binding.frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                Intent intent = new Intent(context, subactivity_comment.class);
//                intent.putExtra("item", item);
//                intent.putExtra("position", getAbsoluteAdapterPosition());
//                context.startActivity(intent);
                }
            });
        }


    }

    public void setData(ArrayList data) {
        challengelist.clear();
        challengelist.addAll(data);
    }


}
