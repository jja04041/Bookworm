package com.example.bookworm.bottomMenu.challenge.board;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bookworm.databinding.SubactivityChallengeBoardCommentBinding;

//인증 게시판에서 사진을 누르면 댓글이 보이도록 한다.

public class subactivity_challenge_board_comment extends AppCompatActivity {

    SubactivityChallengeBoardCommentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= SubactivityChallengeBoardCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}