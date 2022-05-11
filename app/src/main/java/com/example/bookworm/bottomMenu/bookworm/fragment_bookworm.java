package com.example.bookworm.bottomMenu.bookworm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookworm.appLaunch.views.MainActivity;
import com.example.bookworm.bottomMenu.Feed.views.FeedViewModel;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.notification.MyFirebaseMessagingService;
import com.example.bookworm.R;
import com.example.bookworm.achievement.activity_achievement;
import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.userdata.PersonalD;
import com.example.bookworm.core.userdata.UserInfo;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class fragment_bookworm extends Fragment {

    private ImageView iv_bookworm;
    private ImageView iv_bg;
    private Button btn_Achievement;
    private Button btn_Achievement_bg;
    private Button btn_sendpush;

    private TextView tv_bookcount;


    private TextView tv_genrecount;

    private TextView tv_bookworm1, tv_bookworm2, tv_bookworm3, tv_bookworm4, tv_bookworm5, tv_bookworm6, tv_bookworm7,
            tv_bookworm8, tv_bookworm9, tv_bookworm10, tv_bookworm11;

    private MyFirebaseMessagingService myFirebaseMessagingService;
    private FirebaseDatabase mFirebaseDatabase;
    UserInfoViewModel uv;
    public static Context current_context;
//    private FBModule fbModule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        current_context = getContext();
        View view = inflater.inflate(R.layout.fragment_bookworm, container, false);
        uv = new ViewModelProvider(this, new UserInfoViewModel.Factory(getContext())).get(UserInfoViewModel.class);
        myFirebaseMessagingService = new MyFirebaseMessagingService();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        iv_bookworm = view.findViewById(R.id.iv_bookworm);
        iv_bg = view.findViewById(R.id.iv_bg);

        btn_Achievement = view.findViewById(R.id.btn_achievement);
        btn_Achievement_bg = view.findViewById(R.id.btn_achievement_bg);
        btn_sendpush = view.findViewById(R.id.btn_sendpush);

        tv_bookcount = view.findViewById(R.id.tv_bookworm_bookcount);

        tv_bookworm1 = view.findViewById(R.id.tv_bookworm_1);
        tv_bookworm2 = view.findViewById(R.id.tv_bookworm_2);
        tv_bookworm3 = view.findViewById(R.id.tv_bookworm_3);
        tv_bookworm4 = view.findViewById(R.id.tv_bookworm_4);
        tv_bookworm5 = view.findViewById(R.id.tv_bookworm_5);
        tv_bookworm6 = view.findViewById(R.id.tv_bookworm_6);
        tv_bookworm7 = view.findViewById(R.id.tv_bookworm_7);
        tv_bookworm8 = view.findViewById(R.id.tv_bookworm_8);
        tv_bookworm9 = view.findViewById(R.id.tv_bookworm_9);
        tv_bookworm10 = view.findViewById(R.id.tv_bookworm_10);
        tv_bookworm11 = view.findViewById(R.id.tv_bookworm_11);


        btn_Achievement_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(current_context, activity_achievement.class);
                // 1이면 activity achievement에서  bookworm 보여주게
                intent.putExtra("type", 1);

                startActivity(intent);
            }
        });
        btn_Achievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(current_context, activity_achievement.class);
                // 1이면 activity achievement에서  bg 보여주게
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });

        btn_sendpush.setOnClickListener(view1 -> {
            uv.getUser(null, false);
            uv.getData().observe((MainActivity) current_context, it -> {
                String fcmtoken = it.getFCMtoken();
                myFirebaseMessagingService.sendPostToFCM(current_context, fcmtoken, "message");
            });
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        uv.getUser(null, false);
        uv.getData().observe(this, userInfo -> {
            String genre[] = {"자기계발", "소설", "육아", "어린이", "청소년", "사회", "과학", "인문", "생활", "공부", "만화"};
            TextView bookworm[] = {tv_bookworm1, tv_bookworm2, tv_bookworm3, tv_bookworm4, tv_bookworm5,
                    tv_bookworm6, tv_bookworm7, tv_bookworm8, tv_bookworm9, tv_bookworm10, tv_bookworm11};
            for (int i = 0; i < genre.length; i++) {
                if (userInfo.getGenre().get(genre[i]) != null)
                    bookworm[i].append(String.valueOf(userInfo.getGenre().get(genre[i])));
            }
            uv.getBookWorm(userInfo.getToken());
        });
        uv.getBwdata().observe(this, bw -> {
            iv_bookworm.setImageResource(bw.getWormtype());
            iv_bg.setImageResource(bw.getBgtype());
            tv_bookcount.setText("읽은 권 수 : " + bw.getReadcount());
        });
        iv_bg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        iv_bg.setAdjustViewBounds(true);


    }
}



