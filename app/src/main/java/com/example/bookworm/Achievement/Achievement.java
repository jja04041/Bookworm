package com.example.bookworm.Achievement;

import android.content.Context;

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
    CustomDialog customDialog;

    public Achievement (Context context, FBModule fbModule, UserInfo userinfo)
    {
        // 어떤곳에서든 업적 달성할 수 있도록 context와 fbmodule받음

        this.context = context;
        this.fbModule = fbModule;
        this.userinfo = userinfo;
    }


    private void ExecuteFB(int _drawblepath, String _key) {
        // 업적 달성하면 FB에 정보를 줍니다.


        // 축하 다이얼로그
        customDialog = new CustomDialog(context, _key, _drawblepath);
        customDialog.CallDialog();

        HashMap<String, Object> map = new HashMap<>();

        userinfo.getWormvec().add(_drawblepath);
        userinfo.getAchievementmap().put(_key, true);


        map.put("userinfo_achievementmap", userinfo.getAchievementmap());
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
}