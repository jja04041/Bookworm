package com.example.bookworm.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.bookworm.R;
//import com.example.bookworm.modules.module_search;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_main;
//탐색 탭

public class fragment_search extends Fragment {
    EditText edtSearchBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        edtSearchBtn = v.findViewById(R.id.edtSearchBtn);
        edtSearchBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {
                    Intent intent = new Intent(getActivity(), search_fragment_subActivity_main.class);
                    startActivity(intent);
                    edtSearchBtn.clearFocus();
                }
            }
        });
        return v;
    }
}