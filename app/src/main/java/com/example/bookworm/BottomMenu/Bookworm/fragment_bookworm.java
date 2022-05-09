package com.example.bookworm.BottomMenu.Bookworm;

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

import com.example.bookworm.Achievement.activity_achievement;
import com.example.bookworm.Core.Internet.FBModule;
import com.example.bookworm.Core.UserData.PersonalD;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.Notification.MyFirebaseMessagingService;
import com.example.bookworm.R;
import com.google.firebase.database.FirebaseDatabase;

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

    private MyFirebaseMessagingService MyFirebaseMessagingService;
    private FirebaseDatabase mFirebaseDatabase;

    private UserInfo userinfo;
    private BookWorm bookworm;

    public static Context current_context;
    private FBModule fbModule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookworm, container, false);

        MyFirebaseMessagingService = new MyFirebaseMessagingService();
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

        btn_sendpush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String fcmtoken = userinfo.getFCMtoken();

                MyFirebaseMessagingService.sendPostToFCM(fcmtoken,"message", mFirebaseDatabase);

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

        tv_bookcount.setText("읽은 권 수 : " + String.valueOf(bookworm.getReadcount()));

        if(userinfo.getGenre().get("자기계발") != null)
            tv_bookworm1.append(String.valueOf(userinfo.getGenre().get("자기계발")));
        if(userinfo.getGenre().get("소설") != null)
            tv_bookworm2.append(String.valueOf(userinfo.getGenre().get("소설")));
        if(userinfo.getGenre().get("육아") != null)
            tv_bookworm3.append(String.valueOf(userinfo.getGenre().get("육아")));
        if(userinfo.getGenre().get("어린이") != null)
            tv_bookworm4.append(String.valueOf(userinfo.getGenre().get("어린이")));
        if(userinfo.getGenre().get("청소년") != null)
            tv_bookworm5.append(String.valueOf(userinfo.getGenre().get("청소년")));
        if(userinfo.getGenre().get("사회") != null)
            tv_bookworm6.append(String.valueOf(userinfo.getGenre().get("사회")));
        if(userinfo.getGenre().get("과학") != null)
            tv_bookworm7.append(String.valueOf(userinfo.getGenre().get("과학")));
        if(userinfo.getGenre().get("인문") != null)
            tv_bookworm8.append(String.valueOf(userinfo.getGenre().get("인문")));
        if(userinfo.getGenre().get("생활") != null)
            tv_bookworm9.append(String.valueOf(userinfo.getGenre().get("생활")));
        if(userinfo.getGenre().get("공부") != null)
            tv_bookworm10.append(String.valueOf(userinfo.getGenre().get("공부")));
        if(userinfo.getGenre().get("만화") != null)
            tv_bookworm11.append(String.valueOf(userinfo.getGenre().get("만화")));

        iv_bg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        iv_bg.setAdjustViewBounds(true);





    }
}



