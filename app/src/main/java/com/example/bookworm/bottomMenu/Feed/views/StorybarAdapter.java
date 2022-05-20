package com.example.bookworm.bottomMenu.Feed.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.Feed.items.Story;

import java.util.List;

public class StorybarAdapter extends RecyclerView.Adapter<StorybarAdapter.StoriesViewHolder> {

    private List<Story> stories;
    private Context context;

    public StorybarAdapter (List<Story> stories)
    {
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.story_item, parent, false);
        StoriesViewHolder viewHolder = new StoriesViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull StoriesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class StoriesViewHolder extends RecyclerView.ViewHolder{

        private CardView storyOutline;
        public StoriesViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.outline);
        }
    }

}
