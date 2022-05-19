package com.example.bookworm.bottomMenu.bookworm;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookworm.bottomMenu.bookworm.bookworm_pages.FragmentBW;
import com.example.bookworm.bottomMenu.bookworm.bookworm_pages.fragment_record;
import com.example.bookworm.databinding.FragmentBookwormBinding;
import com.example.bookworm.R;

public class fragment_bookworm extends Fragment {
    FragmentManager fm;
    fragment_record fragmentRecord;
    FragmentBW fragmentBW;
    FragmentBookwormBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookwormBinding.inflate(inflater);

        //버튼 누르면 페이징 처리
        fragmentBW = new FragmentBW();
        fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.bwContainer, fragmentBW, "0").commitAllowingStateLoss();

        binding.tvBookworm.setOnClickListener(it -> {
            binding.tvRecord.setTextColor(getResources().getColor(R.color.gray, null));
            binding.tvBookworm.setTextColor(getResources().getColor(R.color.black, null));
            binding.tvRecord.setTextSize(20);
            binding.tvBookworm.setTextSize(25);

            if (fragmentRecord != null) fm.beginTransaction().hide(fragmentRecord).commitAllowingStateLoss();
            fm.beginTransaction().show(fragmentBW).commitAllowingStateLoss();
        });
        binding.tvRecord.setOnClickListener(it -> {
            binding.tvRecord.setTextColor(getResources().getColor(R.color.black, null));
            binding.tvBookworm.setTextColor(getResources().getColor(R.color.gray, null));
            binding.tvRecord.setTextSize(25);
            binding.tvBookworm.setTextSize(20);
            if (fragmentBW != null) fm.beginTransaction().hide(fragmentBW).commitAllowingStateLoss();
            if (fragmentRecord != null)fm.beginTransaction().show(fragmentRecord).commitAllowingStateLoss();
            else {
                fragmentRecord = new fragment_record();
                fm.beginTransaction().add(R.id.bwContainer, fragmentRecord, "1").commitAllowingStateLoss();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        fm=null;
        binding = null;
        super.onDestroyView();
    }
}



