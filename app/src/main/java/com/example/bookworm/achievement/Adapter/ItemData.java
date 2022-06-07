package com.example.bookworm.achievement.Adapter;

import android.content.Context;

public class ItemData {
    int image;
    int type;
    String title;
    Context context;

    public ItemData(int image, int type, String title, Context context) {
        this.image = image;
        this.type = type;
        this.title = title;
        this.context = context;
    }

    public int getType() {
        return type;
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