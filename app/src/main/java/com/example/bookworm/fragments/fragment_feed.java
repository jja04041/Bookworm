package com.example.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.bookworm.Feed.subActivity_Feed_Create;
import com.example.bookworm.R;

public class fragment_feed extends Fragment {
    ImageView imgCreate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        //ImageView profileimg = (ImageView) view.findViewById(R.id.img_profile);
        imgCreate = view.findViewById(R.id.img_createfeed);

        //Glide.with(this).load(R.drawable.profile_img).circleCrop().into(profileimg);

//        //Create New Feed
        imgCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), subActivity_Feed_Create.class);
                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }
}