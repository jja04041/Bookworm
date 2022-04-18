package com.example.bookworm.Achievement;

import android.content.Context;

import com.example.bookworm.Bw.BookWorm;
import com.example.bookworm.R;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.Core.UserData.PersonalD;

import java.util.HashMap;

public class Achievement extends activity_achievement {

    Context context;
    FBModule fbModule;
    UserInfo userinfo;
    BookWorm bookworm;
    CustomDialog customDialog;

    public Achievement (Context context, FBModule fbModule, UserInfo userinfo, BookWorm bookworm)
    {
        // 어떤곳에서든 업적 달성할 수 있도록 context와 fbmodule받음

        this.context = context;
        this.fbModule = fbModule;
        this.userinfo = userinfo;
        this.bookworm = bookworm;
    }


    private void ExecuteFB(int _drawblepath, String _key) {
        // 업적 달성하면 FB에 정보를 줍니다.


        // 축하 다이얼로그
        customDialog = new CustomDialog(context, _key, _drawblepath);
        customDialog.CallDialog();

        HashMap<String, Object> map = new HashMap<>();

        bookworm.getWormvec().add(_drawblepath);
        bookworm.getAchievementmap().put(_key, true);


        map.put("bookworm_achievementmap", bookworm.getAchievementmap());
        map.put("bookworm_wormvec", bookworm.getWormvec());

        fbModule.readData(0, map, bookworm.getToken());

        new PersonalD(context).saveBookworm(bookworm);
    }


    public void CheckAchievement(UserInfo userinfo) {

        // 업적을 달성했는지 체크할 필요가 있을지도 모르니 보류
    }

    public void CompleteAchievement(UserInfo userinfo, Context context) {

        HashMap<String, Object> map = new HashMap<>();

        if (userinfo.getGenre().get("공포") == 10
        && false == bookworm.getAchievementmap().get("공포왕")) {

            ExecuteFB(R.drawable.bw_horror, "공포왕");

        }
        else if (userinfo.getGenre().get("추리") == 10
                && false == bookworm.getAchievementmap().get("추리왕")) {

            ExecuteFB(R.drawable.bw_detective, "추리왕");
        }
    }
}