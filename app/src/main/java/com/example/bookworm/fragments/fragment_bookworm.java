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
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;

public class fragment_bookworm extends Fragment {

    private ImageView iv_bookworm;
    private Button btn_Achievement;

    private UserInfo userinfo;

    public static Context current_context;
    private FBModule fbModule;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookworm, container, false);

        iv_bookworm = view.findViewById(R.id.iv_bookworm);
        btn_Achievement = view.findViewById(R.id.btn_achievement);

        btn_Achievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(current_context, activity_achievement.class);
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
        userinfo = new PersonalD(current_context).getUserInfo(); //저장된 UserInfo값을 가져온다.

    }



    private void set_wormtype(UserInfo userinfo) {

        // 인벤토리에서 선택한 벌레로 책볼레 프레그먼트에 보여줄 생각
    }
}