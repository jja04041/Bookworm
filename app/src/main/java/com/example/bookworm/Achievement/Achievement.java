package com.example.bookworm.Achievement;

import static com.example.bookworm.MainActivity.achievedialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookworm.Bw.enum_wormtype;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;

import java.util.HashMap;

public class Achievement extends activity_achievement {

    Context context;
    FBModule fbModule;
    UserInfo userinfo;

        public Achievement (Context context, FBModule fbModule, UserInfo userinfo)
    {
        // 어떤곳에서든 업적 달성할 수 있도록 context와 fbmodule받음

        this.context = context;
        this.fbModule = fbModule;
        this.userinfo = userinfo;
    }


    private void ExecuteFB(int _drawblepath, String _key) {
        // 업적 달성하면 FB에 정보를 줍니다.

        HashMap<String, Object> map = new HashMap<>();

        userinfo.getWormvec().add(_drawblepath);
        userinfo.getAchievementmap().put(_key, true);

        // 축하합니다! 책볼레 획득! 책볼레 사진 출력
        ShowDialog(_key + "획득", context, _drawblepath);

        map.put("userinfo_achievementmap", userinfo.getAchievementmap());
        fbModule.readData(0, map, userinfo.getToken());
        map.put("userinfo_wormvec", userinfo.getWormvec());
        fbModule.readData(0, map, userinfo.getToken());

        new PersonalD(context).saveUserInfo(userinfo);
    }


    public void CheckAchievement(UserInfo userinfo) {

        // 업적을 달성했는지 체크할 필요가 있을지도 모르니 보류
    }

    public void CompleteAchievement(UserInfo userinfo, Context context) {

        HashMap<String, Object> map = new HashMap<>();

        if (userinfo.getGenre().get(enum_wormtype.공포.value()) == 10
        && false == userinfo.getAchievementmap().get("공포왕")) {

            ExecuteFB(R.drawable.bw_horror, "공포왕");

        }
        else if (userinfo.getGenre().get(enum_wormtype.추리.value()) == 10
                && false == userinfo.getAchievementmap().get("추리왕")) {

            ExecuteFB(R.drawable.bw_detective, "추리왕");
        }
    }

    public void ShowDialog(String _strKey, Context context, int img)
    {

        TextView tvdialog = new TextView(context);
        Button btncancle = new Button(context);
        ImageView ivdialog = new ImageView(context);

        achievedialog = new Dialog(context);
        // dialog 타이틀바 제거
        achievedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        achievedialog.setContentView(R.layout.custom_dialog_achievement);

        tvdialog.findViewById(R.id.tv_achievement_dialog);
        tvdialog.setText(_strKey);

        ivdialog.findViewById(R.id.iv_achievement_dialog);
        ivdialog.setImageResource(img);

        btncancle.findViewById(R.id.btn_achievement_cancle);


        btncancle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 누르면 dialog탈출
                achievedialog.dismiss();
            }
        });

        achievedialog.show();
    }



}