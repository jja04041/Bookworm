package com.example.bookworm.Feed.Comments;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class DiffUtilCallback extends DiffUtil.Callback{
    private ArrayList oldList;
    private ArrayList newList;
    public DiffUtilCallback(ArrayList Old,ArrayList New){
            this.oldList=Old;
            this.newList=New;
    }
    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return  newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if(oldList.get(oldItemPosition) instanceof Comment){
            return ((Comment) oldList.get(oldItemPosition)).getCommentID()==((Comment)newList.get(oldItemPosition)).getCommentID();
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition)==newList.get(newItemPosition);
    }
}
