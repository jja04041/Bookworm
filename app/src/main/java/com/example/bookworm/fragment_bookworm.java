package com.example.bookworm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class fragment_bookworm extends Fragment {

    public static fragment_bookworm newInstance(){
        fragment_bookworm fragment_bookworm = new fragment_bookworm();
        return fragment_bookworm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookworm, container, false);
    }
}