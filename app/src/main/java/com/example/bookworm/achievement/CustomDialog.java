package com.example.bookworm.achievement;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.Feed.subActivity_Feed_Create;

import java.util.List;

public class CustomDialog {

    private Context context;
    private String key;
    String resID;
    boolean exit;

    public CustomDialog(Context context) {
        this.context = context;
    }

    public CustomDialog(Context context, String _AchieveKey, String _resID) {
        this.context = context;
        key = _AchieveKey;
        if (_resID.equals("1")) { //티어 1일때
            resID = "bronze_medal";
        } else if (_resID.equals("2")) { //티어 2일때
            //resID = R.drawable.silver_medal; 아마도 실버로??
        } else {
            resID = _resID;
        }
        exit = false;
    }

    public boolean CallDialog() {
        final Dialog dialog = new Dialog(context);

        // dialog 타이틀바 제거
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_dialog_achievement);

        dialog.show();

        final TextView tvdialog = (TextView) dialog.findViewById(R.id.tv_achievement_dialog);
        final TextView tvdialognotice = (TextView) dialog.findViewById(R.id.tv_achievement_dialog_notice);

        final Button btncancle = (Button) dialog.findViewById(R.id.btn_achievement_cancle);
        final ImageView ivdialog = (ImageView) dialog.findViewById(R.id.iv_achievement_dialog);

        tvdialog.setText("축하합니다! " + key + " 획득!");
        tvdialognotice.setText("획득한 보상을 인벤토리에서 확인해보세요!!");


        if (!resID.contains("medal")) resID = "bw_" + resID;
        ivdialog.setImageResource(context.getResources().getIdentifier(resID, "drawable", context.getPackageName()));

        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 누르면 dialog탈출
                dialog.dismiss();
                exit = true;
                String activityname = "";

                activityname = getRunActivity();
                if (activityname.contains("subActivity_Feed_Create") == true)
                    ((subActivity_Feed_Create) context).finish();
            }
        });
        return exit;
    }

    String getRunActivity() {
        ActivityManager activity_manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task_info = activity_manager.getRunningTasks(9999);

        for (int i = 0; i < task_info.size(); i++) {
            Log.e("test", "[" + i + "] activity:" + task_info.get(i).topActivity.getPackageName() + " >> " + task_info.get(i).topActivity.getClassName());
            return task_info.get(i).topActivity.getClassName();
        }

        return "";
    }
}
