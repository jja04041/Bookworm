package com.example.bookworm.Challenge.subActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bookworm.R;
import com.example.bookworm.databinding.SubactivityChallengeBoardCommentBinding;

public class subactivity_challenge_board_comment extends AppCompatActivity {

    SubactivityChallengeBoardCommentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=SubactivityChallengeBoardCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}