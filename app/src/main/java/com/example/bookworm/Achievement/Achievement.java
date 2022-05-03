package com.example.bookworm.Achievement;

import android.content.Context;

import com.example.bookworm.BottomMenu.Bookworm.BookWorm;
import com.example.bookworm.R;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.Core.Internet.FBModule;
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


    private void ExecuteFB(int _drawblepath, String _key, int type) {
        // 업적 달성하면 FB에 정보를 줍니다.


        // 축하 다이얼로그
        customDialog = new CustomDialog(context, _key, _drawblepath);
        customDialog.CallDialog();

        HashMap<String, Object> map = new HashMap<>();

        // 볼레보상
        if(type == 0) {
            bookworm.getWormvec().add(_drawblepath);
            bookworm.getAchievementmap().put(_key, true);
            map.put("bookworm_achievementmap", bookworm.getAchievementmap());
            map.put("bookworm_wormvec", bookworm.getWormvec());
        }
        // 배경 보상
        else if(type == 1) {
            bookworm.getBgvec().add(_drawblepath);
            bookworm.getAchievementmap().put(_key, true);
            map.put("bookworm_achievementmap", bookworm.getAchievementmap());
            map.put("bookworm_bgvec", bookworm.getBgvec());
        }

        fbModule.readData(0, map, bookworm.getToken());
        new PersonalD(context).saveBookworm(bookworm);
    }


    public void CompleteAchievement(UserInfo userinfo, Context context) {

        HashMap<String, Object> map = new HashMap<>();


//        if (userinfo.getGenre().get("과학") == 10) {
//            if(null == bookworm.getAchievementmap().get("과학")) {
//                ExecuteFB(R.drawable.bw_horror, "과학왕", 0);
//            }
//        }
//        else if (userinfo.getGenre().get("수필") == 10) {
//            if(null == bookworm.getAchievementmap().get("수필")) {
//                ExecuteFB(R.drawable.bw_detective, "수필왕", 0);
//            }
//        }

        // 배경
         if (userinfo.getLikedPost().size() == 2) {
            if(null == bookworm.getAchievementmap().get("하트배경")) {
                ExecuteFB(R.drawable.bg_heart, "하트배경", 1);
            }
        }
        else if (userinfo.getLikedPost().size() == 3) {
            if(null == bookworm.getAchievementmap().get("가나다")) {
                ExecuteFB(R.drawable.bg_heart, "가나다", 1);
            }
        }
    }
}