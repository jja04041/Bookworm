package com.example.bookworm.bottomMenu.bookworm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookworm.R;
import com.example.bookworm.databinding.FragmentBookwormBinding;
import com.example.bookworm.databinding.FragmentRecodeBinding;


public class fragment_recode extends Fragment {

    private FragmentRecodeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();






        return view;
    }
}