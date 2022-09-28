package com.example.bookworm.achievement;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.feed.subActivity_Feed_Create;

import java.util.Arrays;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class CustomDialog extends AppCompatActivity {

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
            resID = "medal_bronze";
        } else if (_resID.equals("2")) { //티어 2일때
            resID = "medal_silver";
        } else if (_resID.equals("3")) { //티어 3일때
            resID = "medal_gold";
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

        final KonfettiView konfettiView = (KonfettiView)dialog.findViewById(R.id.viewKonfetti);
        final TextView tvdialog = (TextView) dialog.findViewById(R.id.tv_achievement_dialog);
        final TextView tvdialognotice = (TextView) dialog.findViewById(R.id.tv_achievement_dialog_notice);

        final Button btncancle = (Button) dialog.findViewById(R.id.btn_achievement_cancle);
        final ImageView ivdialog = (ImageView) dialog.findViewById(R.id.iv_achievement_dialog);

        tvdialog.setText("축하합니다! " + key + " 획득!");
        tvdialognotice.setText("획득한 보상을 인벤토리에서 확인해보세요!!");


        DisplayMetrics display = new DisplayMetrics();

        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(display);
        konfettiView.build()
                .addColors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setDirection(-0.0, -999.0)
                .setSpeed(1f, 4f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12,5))
                .setPosition(-50f,display.widthPixels +50f, -50f,-50f)
                .streamFor(300, 5000L);



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
            return task_info.get(i).topActivity.getClassName();
        }

        return "";
    }


}
