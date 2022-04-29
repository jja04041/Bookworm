package com.example.bookworm.Extension;

import androidx.recyclerview.widget.DiffUtil;

import com.example.bookworm.Feed.Comments.Comment;
import com.example.bookworm.Feed.items.Feed;
import com.example.bookworm.Core.UserData.UserInfo;

import java.util.ArrayList;

public class DiffUtilCallback extends DiffUtil.Callback {
    private ArrayList oldList;
    private ArrayList newList;

    public DiffUtilCallback(ArrayList Old, ArrayList New) {
        this.oldList = Old;
        this.newList = New;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldItem = oldList.get(oldItemPosition);
        Object newItem = newList.get(newItemPosition);
        if (oldItem instanceof Comment && newItem instanceof Comment) {
            return ((Comment) oldItem).getCommentID() == ((Comment) newItem).getCommentID();
        } else if (oldItem instanceof Feed && newItem instanceof Feed) {
            return ((Feed) oldItem).getFeedID() == ((Feed) newItem).getFeedID();
        } else if (oldItem instanceof UserInfo && newItem instanceof UserInfo) {
            return ((UserInfo) oldItem).getToken() == ((UserInfo) newItem).getToken();
        }
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }
}
