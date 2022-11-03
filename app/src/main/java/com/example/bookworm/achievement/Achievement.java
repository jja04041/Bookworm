package com.example.bookworm.achievement;

import android.content.Context;

import com.example.bookworm.bottomMenu.bookworm.BookWorm;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
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
    UserInfoViewModel uv;
    Boolean exitactiviy;

    public Achievement(Context context, FBModule fbModule, UserInfo userinfo, BookWorm bookworm) {
        // 어떤곳에서든 업적 달성할 수 있도록 context와 fbmodule받음

        this.context = context;
        this.fbModule = fbModule;
        this.userinfo = userinfo;
        if (bookworm != null) this.bookworm = bookworm;
        exitactiviy = true;
    }

    public boolean canreturn() {
        return exitactiviy;
    }

    private void ExecuteFB(String _drawblepath, String _key, int type) {
        // 업적 달성하면 FB에 정보를 줍니다.


        // 축하 다이얼로그
        HashMap<String, Object> map = new HashMap<>();

        // 볼레 보상
        if (type == 0) {
            bookworm.getWormList().add(_drawblepath);
            bookworm.getAchievementMap().put(_key, true);
            map.put("bookworm_achievementmap", bookworm.getAchievementMap());
            map.put("bookworm_wormvec", bookworm.getWormList());
        }
        // 배경 보상
        else if (type == 1) {
//            bookworm.getBgvec().add(_drawblepath);
            bookworm.getAchievementMap().put(_key, true);
            map.put("bookworm_achievementmap", bookworm.getAchievementMap());
            map.put("bookworm_bgvec", bookworm.getBgList());
        }

        //인증글 보상
        else if (type == 2) {
            //타입이 인증글일때
            uv = new UserInfoViewModel(context);
            uv.getUser(null, false);
            uv.updateUser(userinfo);

            customDialog = new CustomDialog(context, _key, _drawblepath);
            exitactiviy = customDialog.CallDialog();
        }

        //볼레 보상 이거나 배경 보상일때
        if (type == 0 || type == 1) {
            fbModule.readData(0, map, bookworm.getToken());
            new PersonalD(context).saveBookworm(bookworm);

            customDialog = new CustomDialog(context, _key, _drawblepath);
            exitactiviy = customDialog.CallDialog();
        }
    }


    public void CompleteAchievement(UserInfo userinfo, Context context) {

        HashMap<String, Object> map = new HashMap<>();

        if (userinfo.getGenre().get("자기계발") != null) {
            if (userinfo.getGenre().get("자기계발") == 2) {
                if (null == bookworm.getAchievementMap().get("자존감왕")) {
                    ExecuteFB("confidence", "자존감왕", 0);
                }
            }
        }

        if (userinfo.getGenre().get("소설") != null) {
            if (userinfo.getGenre().get("소설") == 1) {
                if (null == bookworm.getAchievementMap().get("가을볼레")) {
                    ExecuteFB("fall", "가을볼레", 0);
                }
            }
        }
        if (userinfo.getGenre().get("육아") != null) {
            if (userinfo.getGenre().get("육아") == 3) {
                if (null == bookworm.getAchievementMap().get("좋은엄마")) {
                    ExecuteFB("mom", "좋은엄마", 0);
                }
            }
        }
        if (userinfo.getGenre().get("어린이") != null) {
            if (userinfo.getGenre().get("어린이") == 3) {
                if (null == bookworm.getAchievementMap().get("착한어린이")) {
                    ExecuteFB("child", "착한어린이", 0);
                }
            }
        }

        if (userinfo.getGenre().get("청소년") != null) {
            if (userinfo.getGenre().get("청소년") == 3) {
                if (null == bookworm.getAchievementMap().get("사춘기볼레")) {
                    ExecuteFB("student", "사춘기볼레", 0);
                }
            }
        }

        if (userinfo.getGenre().get("공부") != null) {
            if (userinfo.getGenre().get("공부") == 3) {
                if (null == bookworm.getAchievementMap().get("공부벌레")) {
                    ExecuteFB("study", "공부벌레", 0);
                }
            }
        }
        if (userinfo.getGenre().get("사회") != null) {
            if (userinfo.getGenre().get("사회") == 3) {
                if (null == bookworm.getAchievementMap().get("사회왕")) {
                    ExecuteFB("social", "사회왕", 0);
                }
            }
        }
        if (userinfo.getGenre().get("과학") != null) {
            if (userinfo.getGenre().get("과학") == 3) {
                if (null == bookworm.getAchievementMap().get("싸이언쓰볼레")) {
                    ExecuteFB("science", "싸이언쓰볼레", 0);
                }
            }
        }
        if (userinfo.getGenre().get("인문") != null) {
            if (userinfo.getGenre().get("인문") == 3) {
                if (null == bookworm.getAchievementMap().get("문학볼레")) {
                    ExecuteFB("literature", "문학볼레", 0);
                }
            }
        }
//        if(userinfo.getGenre().get("생활") != null) {
//            if (userinfo.getGenre().get("생활") == 3) {
//                if(null == bookworm.getAchievementMap()().get("생활볼레")) {
//                    ExecuteFB(R.drawable.bw_detective, "생활볼레", 0);
//                }
//            }
//        }
        if (userinfo.getGenre().get("만화") != null) {
            if (userinfo.getGenre().get("만화") == 3) {
                if (null == bookworm.getAchievementMap().get("만화광")) {
                    ExecuteFB("cartoon", "만화광", 0);
                }
            }
        }


        // 좋아요 업적

        if (userinfo.getLikedPost().size() == 1) {
//        if (userinfo.getLikedPost().size() == 2) {
            if (null == bookworm.getAchievementMap().get("하트배경")) {
                ExecuteFB("bg_heart", "하트배경", 1);
            }
        }


        //인증글 업적
        //티어는 1 ~ n까지로, 일단 1티어는 브론즈로 해둠

        if (userinfo.getCompletedChallenge() >= 3 && userinfo.getTier() < 1) { //3 넘으면 브론즈로
            userinfo.setTier(Long.parseLong(String.valueOf(1)));
            ExecuteFB("1", "브론즈 티어", 2); //1티어, 브론즈 티어, type(인증글인지, 볼레인지)
        }

        if (userinfo.getCompletedChallenge() >= 10 && userinfo.getTier() < 2) { //10 넘으면 실버로
            userinfo.setTier(Long.parseLong(String.valueOf(2)));
            ExecuteFB("2", "실버 티어", 2); //2티어, 실버 티어, type(인증글인지, 볼레인지)
        }

        if (userinfo.getCompletedChallenge() >= 30 && userinfo.getTier() < 3) { //30 넘으면 골드로
            userinfo.setTier(Long.parseLong(String.valueOf(3)));
            ExecuteFB("3", "골드 티어", 2); //3티어, 골드 티어, type(인증글인지, 볼레인지)
        }


    }



}