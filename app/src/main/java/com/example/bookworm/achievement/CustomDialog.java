package com.example.bookworm.achievement;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookworm.R;

public class CustomDialog {

    private Context context;
    private String key;
    int     resID;

    public CustomDialog(Context context) {
        this.context = context;
    }

    public CustomDialog(Context context, String _AchieveKey, int _resID) {
        this.context = context;
        key = _AchieveKey;
        resID = _resID;
    }

    public void CallDialog() {
        final Dialog dialog = new Dialog(context);

        // dialog 타이틀바 제거
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_dialog_achievement);

        dialog.show();

        final TextView tvdialog = (TextView) dialog.findViewById(R.id.tv_achievement_dialog);
        final Button btncancle = (Button) dialog.findViewById(R.id.btn_achievement_cancle);
        final ImageView ivdialog =(ImageView) dialog.findViewById(R.id.iv_achievement_dialog);

        tvdialog.setText("축하합니다! "+ key + " 획득!");
        ivdialog.setImageResource(resID);
        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 누르면 dialog탈출
                dialog.dismiss();
            }
        });

    }

}
