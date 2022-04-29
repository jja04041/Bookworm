package com.example.bookworm.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.bookworm.Achievement.activity_achievement;
import com.example.bookworm.Bw.BookWorm;
import com.example.bookworm.R;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.Core.Internet.FBModule;
import com.example.bookworm.Core.UserData.PersonalD;

public class fragment_bookworm extends Fragment {

    private ImageView iv_bookworm;
    private ImageView iv_bg;
    private Button btn_Achievement;
    private Button btn_Achievement_bg;

    private UserInfo userinfo;
    private BookWorm bookworm;

    public static Context current_context;
    private FBModule fbModule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookworm, container, false);

        iv_bookworm = view.findViewById(R.id.iv_bookworm);
        iv_bg = view.findViewById(R.id.iv_bg);

        btn_Achievement = view.findViewById(R.id.btn_achievement);
        btn_Achievement_bg = view.findViewById(R.id.btn_achievement_bg);

        btn_Achievement_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(current_context, activity_achievement.class);
                // 1이면 activity achievement에서  bookworm 보여주게
                intent.putExtra("type", 1);

                startActivity(intent);
            }
        });
        btn_Achievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(current_context, activity_achievement.class);
                // 1이면 activity achievement에서  bg 보여주게
                intent.putExtra("type", 0  );
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        current_context = getActivity();
        fbModule = new FBModule(current_context);
        userinfo = new PersonalD(current_context).getUserInfo();
        bookworm = new PersonalD(current_context).getBookworm();

        iv_bookworm.setImageResource(bookworm.getWormtype());
        iv_bg.setImageResource(bookworm.getBgtype());

    }
}