package com.example.bookworm.Core.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookworm.BottomMenu.Bookworm.BookWorm;
import com.example.bookworm.Core.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.Core.UserData.UserInfo;
import com.example.bookworm.Core.Internet.FBModule;
import com.example.bookworm.Core.UserData.PersonalD;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;

import java.util.HashMap;
import java.util.Map;

public class activity_login extends Activity {

    protected SessionCallback sessionCallback = new SessionCallback();
    private static final String TAG = "MainActivity";
    public static Context mContext;
    private FirebaseAuth mAuth;
    private FBModule fbModule;
    protected GoogleSignInAccount gsa;
    UserInfo userInfo;
    BookWorm bookworm;
    public static GoogleSignInClient gsi;
    Boolean isLogined=Boolean.FALSE;
    private int RC_SIGN_IN = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        fbModule = new FBModule(mContext);
        isLogined=Boolean.FALSE; //카카오 이중로그인 방지
        mAuth = FirebaseAuth.getInstance();
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            startLogin();
        } else {
            new AlertDialog.Builder(mContext)
                    .setMessage("인터넷 접속 후 다시 시도해 주세요")
                    .setPositiveButton("네트워크 설정", (dialog, which) -> {
                        dialog.dismiss();
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        finish();
                    }).setNegativeButton("닫기", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                finish();
            }).show();
        }
    }

    private void startLogin() {
        Session session = Session.getCurrentSession();
        session.addCallback(sessionCallback);


        // 구글

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        gsi = GoogleSignIn.getClient(this, gso);

        gsa = GoogleSignIn.getLastSignedInAccount(activity_login.this);


        // 구글 자동 로그인
        if (gsa != null) {
            signInGoogle();
        }

        ImageButton google_login_button = (ImageButton) findViewById(R.id.btn_login_google);

        google_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });


        // 카카오
        // 이미 로그인되어있고 세션이 살아있는 경우에만 작동
        if (Session.getCurrentSession().checkAndImplicitOpen() && isLogined==Boolean.TRUE) {
            Log.d(TAG, "onClick: 로그인 세션살아있음");
            // 카카오 자동 로그인
            sessionCallback.requestMe();
        }

        ImageButton kakao_login_button = (ImageButton) findViewById(R.id.btn_login_kakao);
        kakao_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카카오 로그인 시도 (창이 뜬다.)
                session.open(AuthType.KAKAO_LOGIN_ALL, activity_login.this);
                isLogined=Boolean.TRUE;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        //구글 로그인시도시
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            task.addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    //회원의 정보를 가져옴
                    GoogleSignInAccount account = task.getResult();
                    //회원가입 여부를 확인.
                    userInfo = new UserInfo();
                    userInfo.add(account);
                    userInfo.setToken(account.getId());
                    signUp(userInfo, account.getId());
                }
            });
        }
    }

    //로그인 함수
    public void signIn(Boolean ResultCode, UserInfo fbUserInfo, BookWorm bookworm) {
        if (ResultCode) move(fbUserInfo, bookworm);//회원이 아닌 경우
        else move(fbUserInfo, bookworm); //회원인 경우
    }

    //화면 이동 => 메인 액티비티로
    public void move(UserInfo userInfo, BookWorm bookworm) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        new PersonalD(this).saveUserInfo(userInfo); //값 저장
        new PersonalD(this).saveBookworm(bookworm);
        startActivity(intent);
        this.finish();
    }


    //회원가입 함수
    public void signUp(UserInfo userInfo, String idtoken) {
        if (null != idtoken && null != userInfo.getUsername()) {
            Map map = new HashMap();
            map.put("UserInfo", userInfo);
            fbModule.readData(0, map, idtoken);
        } else {
            Log.d("function signup", "nono token ");
        }
    }

    // 구글 로그인 메소드
    protected void signInGoogle() {
        Intent signInIntent = gsi.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}



