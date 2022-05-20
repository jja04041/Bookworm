package com.example.bookworm.bottomMenu.bookworm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail.fragment_bookworm_detail;
import com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_record.fragment_record;
import com.example.bookworm.databinding.FragmentBookwormBinding;
import com.example.bookworm.R;

public class fragment_bookworm extends Fragment {
    FragmentManager fm;
    fragment_record fragmentRecord;
    fragment_bookworm_detail fragmentbookwormdetail;
    FragmentBookwormBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookwormBinding.inflate(inflater);

        //버튼 누르면 페이징 처리
        fragmentbookwormdetail = new fragment_bookworm_detail();
        fm = getChildFragmentManager(); //프레그먼트 내에서 프레그먼트 이용하기 위함.
        fm.beginTransaction().replace(R.id.bwContainer, fragmentbookwormdetail, "0").commitAllowingStateLoss();

        binding.tvBookworm.setOnClickListener(it -> {
            binding.tvRecord.setTextColor(getResources().getColor(R.color.gray, null));
            binding.tvBookworm.setTextColor(getResources().getColor(R.color.black, null));
            binding.tvRecord.setTextSize(20);
            binding.tvBookworm.setTextSize(25);

            if (fragmentRecord != null) fm.beginTransaction().hide(fragmentRecord).commitAllowingStateLoss();
            fm.beginTransaction().show(fragmentbookwormdetail).commitAllowingStateLoss();
        });
        binding.tvRecord.setOnClickListener(it -> {
            binding.tvRecord.setTextColor(getResources().getColor(R.color.black, null));
            binding.tvBookworm.setTextColor(getResources().getColor(R.color.gray, null));
            binding.tvRecord.setTextSize(25);
            binding.tvBookworm.setTextSize(20);
            if (fragmentbookwormdetail != null) fm.beginTransaction().hide(fragmentbookwormdetail).commitAllowingStateLoss();
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



