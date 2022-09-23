package com.example.bookworm.bottomMenu.profile.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookworm.R;
import com.example.bookworm.appLaunch.views.MainActivity;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.ActivityPrefergenreBinding;

import java.util.ArrayList;

public class PreferGenreActivity extends AppCompatActivity {

    private UserInfo NowUser;

    private ActivityPrefergenreBinding binding;
    Context context;

    private String[][] genreTable = {
            {"자기계발",
                    "소설",
                    "육아",
                    "사회"},

            {"어린이",
                    "인문",
                    "생활",
                    "공부"}
    };

    TextView lbllist[][] = new TextView[2][4];
    int[][] GenreId = {{R.id.lbl0, R.id.lbl1, R.id.lbl2, R.id.lbl3}
            , {R.id.lbl4, R.id.lbl5, R.id.lbl6, R.id.lbl7}};
    ArrayList<String> lblSelected = new ArrayList<String>(); //선택한 라벨 목록을 담을 리스트


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrefergenreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;


        UserInfoViewModel pv = new ViewModelProvider(this, new UserInfoViewModel.Factory(context)).get(UserInfoViewModel.class);
        pv.getUser(null, false);

        pv.getUserInfoLiveData().observe(this, userInfo -> {

            int isLogin = 0;
            Intent intent = getIntent();
            isLogin = intent.getIntExtra("Login", 0);


            NowUser = userInfo;

            binding.btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


            for (int i = 0; i < lbllist.length; i++) {
                for (int j = 0; j < lbllist[i].length; ++j) {
                    Log.d("i:", String.valueOf(lbllist.length));
                    Log.d("j:", String.valueOf(lbllist[i].length));
                    final int indexX = i;
                    final int indexY = j;
                    lbllist[indexX][indexY] = findViewById(GenreId[indexX][indexY]);
                    lbllist[indexX][indexY].setText(genreTable[indexX][indexY]);
                    lbllist[indexX][indexY].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            {
                                if (lbllist[indexX][indexY].isSelected()) {
                                    // for문을 돌면서 if lblselected[i]에 선택한lbllist값이 있으면 그 값 추출
                                    for (int i = 0; i < lblSelected.size(); ++i) {
                                        if (lblSelected.get(i).equals(lbllist[indexX][indexY].getText().toString())) {
                                            lblSelected.remove(i);
                                            lbllist[indexX][indexY].setSelected(false);
                                            break;
                                        }
                                    }
                                } else {
                                    if (5 == lblSelected.size()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("선호장르는 최대 5개까지만 선택할 수 있습니다.");
                                        builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    } else {

                                        lblSelected.add(lbllist[indexX][indexY].getText().toString());
                                        lbllist[indexX][indexY].setSelected(true);
                                    }
                                }
                            }
                        }
                    });
                }
            }

            // 유저의 선호장르와 비교하여 미리 라벨 선택
            for (int i = 0; i < lbllist.length; i++) {
                for (int j = 0; j < lbllist[i].length; ++j) {
                    PreSelect(NowUser, lbllist[i][j]);
                }
            }

            int finalIsLogin = isLogin;
            binding.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NowUser.setPrefergenre(lblSelected);
                    pv.updateUser(NowUser);
                    if (0 == finalIsLogin)
                        finish();
                    else {
                        MoveToMain();
                    }
                }
            });
        });
    }

    private void Addlabel(String _strgenre) {
//        TextView label = new TextView(this);
//        label.setWidth(ConvertDPtoPX(this, 32));
//        label.setHeight(ConvertDPtoPX(this, 32));
//
//        label.setPadding(
//                0,
//                ConvertDPtoPX(this, 5),
//                0,
//                ConvertDPtoPX(this, 5));
////        LinearLayout.LayoutParams LP = (LinearLayout.LayoutParams)binding.genrelayout1.getLayoutParams();
////
////        LP.setMargins(
////                ConvertDPtoPX(this, 5),
////                ConvertDPtoPX(this, 5),
////                ConvertDPtoPX(this, 5),
////                ConvertDPtoPX(this, 5));
//
//        label.setGravity(Gravity.CENTER);
//        label.setBackgroundResource(R.drawable.label_design);
//        label.setText(_strgenre);
//        label.setTextSize(ConvertDPtoPX(this, 18));
//        label.setId(View.generateViewId());
//
//        viewId.add(label.getId());
//        //label.setOnClickListener(this);
//        binding.genrelayout1.addView(label);
    }

    public boolean RemoveLbl(TextView _lblSelected) {
        for (int j = 0; j < lbllist.length; ++j) {
            for (int k = 0; k < 4; ++k) {
                if (_lblSelected == lbllist[j][k]) {
                    lblSelected.remove(_lblSelected);
                    return true;
                }
            }
        }
        return false;
    }

    public static int ConvertDPtoPX(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public void MoveToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    // 라벨
    public void PreSelect(UserInfo _NowUser, TextView _tv) {
        ArrayList<String> temp = _NowUser.getPrefergenre();

        for (int i = 0; i < temp.size(); ++i) {
            if (temp.get(i).equals(_tv.getText().toString())) {
                _tv.setSelected(true);
                lblSelected.add(temp.get(i));
            }
        }
    }
}