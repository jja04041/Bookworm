package com.example.bookworm.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;

public class activity_login extends Activity {

    private SessionCallback sessionCallback = new SessionCallback();
    private static final String TAG = "MainActivity";
    public static Context mContext;
//    private FirebaseAuth mAuth;
//    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;
//        mAuth = FirebaseAuth.getInstance();

        Session session = Session.getCurrentSession();
        Log.d("session_now",String.valueOf(session.isOpened()));
        session.addCallback(new SessionCallback());

        if (session.checkAndImplicitOpen()) {
            Log.d(TAG, "onClick: 로그인 세션살아있음");
            // 카카오 로그인 시도 (창이 안뜬다.)
            sessionCallback.requestMe();
        }

        ImageButton kakao_login_button = (ImageButton) findViewById(R.id.btn_login_kakao);
        kakao_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Log.d(TAG, "onClick: 로그인 세션끝남");
                    // 카카오 로그인 시도 (창이 뜬다.)

                    session.open(AuthType.KAKAO_LOGIN_ALL, activity_login.this);
            }
        });

        ImageButton google_login_button = (ImageButton) findViewById(R.id.btn_login_google);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void move(UserInfo UserInfo){
        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("name", UserInfo.username);
        intent.putExtra("profileimg", UserInfo.profileimg);
        intent.putExtra("email", UserInfo.email);
        startActivity(intent);
        this.finish();
    }


    public void signUp(UserInfo UserInfo, String idtoken) {

        if(null != idtoken && null != UserInfo.username) {
//            databaseReference.child("kakao_id_token").push().setValue(idtoken);
//            databaseReference.child("User_name").push().setValue(UserInfo.username);

                } else {
                     Log.d("fucntion signup", "nono token ");
                }
        }
}



