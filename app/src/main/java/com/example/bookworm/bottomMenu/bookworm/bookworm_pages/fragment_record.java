package com.example.bookworm.bottomMenu.bookworm.bookworm_pages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;

import com.example.bookworm.databinding.FragmentRecordBinding;


public class fragment_record extends Fragment {

    private FragmentRecordBinding binding;
    private UserInfoViewModel uv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(getContext())).get(UserInfoViewModel.class);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        uv.getUser(null, false);
        uv.getData().observe(this, userInfo -> {
            String[] genre = {
                    "자기계발",
                    "소설",
                    "육아",
                    "어린이",
                    "청소년",
                    "사회",
                    "과학",
                    "인문",
                    "생활",
                    "공부",
                    "만화"};
            TextView bookworm[] = {
                    binding.tvBookworm1,
                    binding.tvBookworm2,
                    binding.tvBookworm3,
                    binding.tvBookworm4,
                    binding.tvBookworm5,
                    binding.tvBookworm6,
                    binding.tvBookworm7,
                    binding.tvBookworm8,
                    binding.tvBookworm9,
                    binding.tvBookworm10,
                    binding.tvBookworm11};
            for (int i = 0; i < genre.length; i++) {
                if (userInfo.getGenre().get(genre[i]) != null)
                    bookworm[i].setText(genre[i] + " : " + userInfo.getGenre().get(genre[i]));
            }
            uv.getBookWorm(userInfo.getToken());
        });
        uv.getBwdata().observe(this,bw-> {
            binding.tvBookwormBookcount.setText("읽은 권 수 : " + bw.getReadcount());
        });
    }
}