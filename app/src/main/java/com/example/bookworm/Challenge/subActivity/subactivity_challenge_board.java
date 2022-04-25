package com.example.bookworm.Challenge.subActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.bookworm.Challenge.items.Challenge;
import com.example.bookworm.Search.items.Book;
import com.example.bookworm.Search.subActivity.search_fragment_subActivity_result;
import com.example.bookworm.databinding.SubactivityChallengeBoardBinding;
import com.example.bookworm.databinding.SubactivityFeedCreateBinding;

public class subactivity_challenge_board extends AppCompatActivity {

    SubactivityChallengeBoardBinding binding;
    Context context;
    Challenge challenge;
    Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubactivityChallengeBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        Intent intent = getIntent();
        //넘겨받은 값 챌린지 객체에 넣음
        challenge = (Challenge) intent.getSerializableExtra("challenge");

//        Book book = item.getBook();
//        binding.feedBookAuthor.setText(challenge.get);
        Glide.with(this).load(challenge.getBookThumb()).into(binding.feedBookThumb); //책 썸네일 설정
        binding.feedBookTitle.setText(challenge.getBookTitle());
        binding.llbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, search_fragment_subActivity_result.class);
                intent.putExtra("itemid", challenge.getBookId());
                context.startActivity(intent);
            }
        });
    }
}