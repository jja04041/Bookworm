package com.example.bookworm.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.modules.FBModule;
import com.example.bookworm.modules.personalD.PersonalD;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;

import java.util.HashMap;

public class activity_login extends Activity {

    protected SessionCallback sessionCallback = new SessionCallback();
    private static final String TAG = "MainActivity";
    public static Context mContext;
    private FirebaseAuth mAuth;
    private FBModule fbModule;
    protected GoogleSignInAccount gsa;

    public static GoogleSignInClient gsi;

    private int RC_SIGN_IN = 123;

//    public activity_login(GoogleSignInAccount gsa)
//    {
//        gsa = GoogleSignIn.getLastSignedInAccount(activity_login.this);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fbModule = new FBModule(mContext);
        mContext = this;
        mAuth = FirebaseAuth.getInstance();

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
        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            Log.d(TAG, "onClick: 로그인 세션살아있음");
            // 카카오 자동 로그인
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
                    GoogleSignInAccount account = task.getResult();
                    Log.d("account", account.getIdToken());
                    UserInfo userInfo = new UserInfo();
                    userInfo.add(account);
                    signUp(userInfo, account.getId());
                    move(userInfo); //회원정보를 메인 액티비티로 넘기고, 액티비티를 메인액티비티로 변경함.
                }
            });
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getServerAuthCode());
//
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//            }
        }
    }

    public void move(UserInfo userInfo) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        new PersonalD(this).saveUserInfo(userInfo); //값 저장
        startActivity(intent);
        this.finish();
    }


    //회원가입 함수
    public void signUp(UserInfo UserInfo, String idtoken) {
        if (null != idtoken && null != UserInfo.getUsername()) {
            HashMap<String, String> map = new HashMap<>();
//            Log.d("token",Session.getCurrentSession().getTokenInfo().getAccessToken()); //실제 토큰
            UserInfo.setToken(idtoken);
            map.put("user_name", UserInfo.getUsername());
            map.put("idToken", idtoken);
            map.put("platform", UserInfo.getPlatform());
            map.put("email", UserInfo.getEmail());
            map.put("profileURL", UserInfo.getProfileimg());

            UserInfo.Initbookworm();
            //파이어베이스에 해당 계정이 등록되있지 않다면
            fbModule.readData(0, map, idtoken);
        } else {
            Log.d("fucntion signup", "nono token ");
        }
    }


    // 구글 로그인 메소드
    protected void signInGoogle() {
        Intent signInIntent = gsi.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


}



