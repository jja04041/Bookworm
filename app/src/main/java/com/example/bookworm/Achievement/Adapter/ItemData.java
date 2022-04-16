package com.example.bookworm.Achievement.Adapter;

import android.content.Context;

public class ItemData {
    int image;
    String title;
    Context context;

    public ItemData(int image, String title, Context context){
        this.image = image;
        this.title = title;
        this.context = context;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}