package com.example.bookworm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookworm.R;

public class fragment_feed extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        ImageView profileimg = (ImageView) view.findViewById(R.id.img_profile);
        ImageView profileimg_like = (ImageView) view.findViewById(R.id.img_like_profile);
        ImageView profileimg_reply = (ImageView) view.findViewById(R.id.img_reply_profle);

        Glide.with(this).load(R.drawable.profile_img).circleCrop().into(profileimg);
        Glide.with(this).load(R.drawable.profile_img).circleCrop().into(profileimg_like);
        Glide.with(this).load(R.drawable.profile_img).circleCrop().into(profileimg_reply);


        // Inflate the layout for this fragment
        return view;
    }
}