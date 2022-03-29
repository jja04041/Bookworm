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
import com.example.bookworm.Bw.enum_wormtype;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;

import java.util.Vector;

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

        set_wormtype(userinfo);
    }



    // userinfo의 genre 배열의 어떤 인덱스(장르)가 가장 큰 값을 가지고 있는지 찾은 후
    // 그 인덱스를 반환
    private int favorgenre(Vector<Integer> genre) {
        int max = 0;

        for(int i=1; i<genre.size(); ++i)
            if(genre.get(max) < genre.get(i))
                max = i;

        return max;
    }

    private void set_wormtype(UserInfo userinfo) {
        switch (favorgenre(userinfo.getGenre()))
        {
            case 0:
                userinfo.setWormtype(enum_wormtype.디폴트);
                userinfo.getWormvec().set(0, R.drawable.bw_default);
                break;
            case 1:
                userinfo.setWormtype(enum_wormtype.공포);
                userinfo.getWormvec().set(0, R.drawable.bw_horror);
                int i = R.drawable.bw_horror;
                break;
            case 2:
                userinfo.setWormtype(enum_wormtype.추리);
                userinfo.getWormvec().set(0, R.drawable.bw_detective);
                break;
            case 3:
//                userinfo.setWormtype(enum_wormtype.로맨스);
//                userinfo.getWormvec().set(0, userinfo.getWormtype().value());
//                iv_bookworm.setImageResource(R.drawable.bw_romance);
                break;
            default:
                break;
        }
        iv_bookworm.setImageResource(userinfo.getWormvec().get(0));
    }


}