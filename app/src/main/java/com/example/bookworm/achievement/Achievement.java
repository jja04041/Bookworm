package com.example.bookworm.achievement;

import android.content.Context;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.bookworm.BookWorm;
import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.userdata.PersonalD;
import com.example.bookworm.core.userdata.UserInfo;

import java.util.HashMap;

public class Achievement {

    Context context;
    FBModule fbModule;
    UserInfo userinfo;
    BookWorm bookworm;
    CustomDialog customDialog;
    Boolean exitactiviy;

    public Achievement (Context context, FBModule fbModule, UserInfo userinfo, BookWorm bookworm)
    {
        // 어떤곳에서든 업적 달성할 수 있도록 context와 fbmodule받음

        this.context = context;
        this.fbModule = fbModule;
        this.userinfo = userinfo;
        this.bookworm = bookworm;
        exitactiviy = true;
    }

    public boolean canreturn(){
        return exitactiviy;
    }

    private void ExecuteFB(int _drawblepath, String _key, int type) {
        // 업적 달성하면 FB에 정보를 줍니다.


        // 축하 다이얼로그
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

        customDialog = new CustomDialog(context, _key, _drawblepath);
        exitactiviy = customDialog.CallDialog();
    }


    public void CompleteAchievement(UserInfo userinfo, Context context) {

        HashMap<String, Object> map = new HashMap<>();

        if(userinfo.getGenre().get("자기계발") != null) {
            if (userinfo.getGenre().get("자기계발") == 2) {
                if (null == bookworm.getAchievementmap().get("자존감왕")) {
                    ExecuteFB(R.drawable.bw_confidence, "자존감왕", 0);
                }
            }
        }

        if(userinfo.getGenre().get("소설") != null) {
            if (userinfo.getGenre().get("소설") == 1) {
                if (null == bookworm.getAchievementmap().get("가을볼레")) {
                    ExecuteFB(R.drawable.bw_fall, "가을볼레", 0);
                }
            }
        }
        if(userinfo.getGenre().get("육아") != null) {
            if (userinfo.getGenre().get("육아") == 3) {
                if (null == bookworm.getAchievementmap().get("좋은엄마")) {
                    ExecuteFB(R.drawable.bw_mom, "좋은엄마", 0);
                }
            }
        }
        if(userinfo.getGenre().get("어린이") != null) {
            if (userinfo.getGenre().get("어린이") == 3) {
                if (null == bookworm.getAchievementmap().get("착한어린이")) {
                    ExecuteFB(R.drawable.bw_child, "착한어린이", 0);
                }
            }
        }

        if(userinfo.getGenre().get("청소년") != null) {
            if (userinfo.getGenre().get("청소년") == 3) {
                    if(null == bookworm.getAchievementmap().get("사춘기볼레")) {
                        ExecuteFB(R.drawable.bw_student, "사춘기볼레", 0);
                    }
                }
        }

        if(userinfo.getGenre().get("공부") != null) {
            if (userinfo.getGenre().get("공부") == 3) {
                if(null == bookworm.getAchievementmap().get("공부벌레")) {
                    ExecuteFB(R.drawable.bw_study, "공부벌레", 0);
                }
            }
        }
        if(userinfo.getGenre().get("사회") != null) {
            if (userinfo.getGenre().get("사회") == 3) {
                if(null == bookworm.getAchievementmap().get("사회왕")) {
                    ExecuteFB(R.drawable.bw_social, "사회왕", 0);
                }
            }
        }
        if(userinfo.getGenre().get("과학") != null) {
            if (userinfo.getGenre().get("과학") == 3) {
                if(null == bookworm.getAchievementmap().get("싸이언쓰볼레")) {
                    ExecuteFB(R.drawable.bw_science, "싸이언쓰볼레", 0);
                }
            }
        }
        if(userinfo.getGenre().get("인문") != null) {
            if (userinfo.getGenre().get("인문") == 3) {
                if(null == bookworm.getAchievementmap().get("문학볼레")) {
                    ExecuteFB(R.drawable.bw_literature, "문학볼레", 0);
                }
            }
        }
//        if(userinfo.getGenre().get("생활") != null) {
//            if (userinfo.getGenre().get("생활") == 3) {
//                if(null == bookworm.getAchievementmap().get("생활볼레")) {
//                    ExecuteFB(R.drawable.bw_detective, "생활볼레", 0);
//                }
//            }
//        }
        if(userinfo.getGenre().get("만화") != null) {
            if (userinfo.getGenre().get("만화") == 3) {
                if(null == bookworm.getAchievementmap().get("만화광")) {
                    ExecuteFB(R.drawable.bw_cartoon, "만화광", 0);
                }
            }
        }


        // 좋아요 업적


        if (userinfo.getLikedPost().size() == 2) {
                if(null == bookworm.getAchievementmap().get("하트배경")) {
                    ExecuteFB(R.drawable.bg_heart, "하트배경", 1);
                }
        }



     }
}