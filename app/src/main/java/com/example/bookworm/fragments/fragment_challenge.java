package com.example.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.bookworm.Challenge.activity_createchallenge;
import com.example.bookworm.R;

public class fragment_challenge extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

        Button btn_create_challenge = view.findViewById(R.id.btn_create_challenge);


        btn_create_challenge.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), activity_createchallenge.class);
                startActivity(intent);
                btn_create_challenge.clearFocus();
            }
        });

        return view;
    }

}
