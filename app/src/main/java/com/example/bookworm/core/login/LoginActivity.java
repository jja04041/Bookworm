package com.example.bookworm.core.login;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookworm.appLaunch.modules.MainViewModel;
import com.example.bookworm.bottomMenu.bookworm.BookWorm;
import com.example.bookworm.appLaunch.views.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.core.internet.FBModule;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;

public class LoginActivity extends AppCompatActivity {

    protected SessionCallback sessionCallback = new SessionCallback();
    private static final String TAG = "MainActivity";
    public static Context mContext;
    private FirebaseAuth mAuth;
    private FBModule fbModule;
    protected GoogleSignInAccount gsa;
    UserInfo userInfo;
    BookWorm bookworm;
    public static GoogleSignInClient gsi;
    Boolean isLogined = Boolean.FALSE;
    private int RC_SIGN_IN = 123;
    private MainViewModel mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        mContext = this;
        fbModule = new FBModule(mContext);
        isLogined = Boolean.FALSE; //카카오 이중로그인 방지
        mAuth = FirebaseAuth.getInstance();
        mv = new ViewModelProvider(this, new MainViewModel.Factory(this)).get(MainViewModel.class);
        startLogin();
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
        gsa = GoogleSignIn.getLastSignedInAccount(this);
        // 구글 자동 로그인
        if (gsa != null) {
            signInGoogle();
            return;
        }
        ImageButton google_login_button = (ImageButton) findViewById(R.id.btn_login_google);
        google_login_button.setOnClickListener(it -> {
                    signInGoogle();
                }
        );


        // 카카오
        // 이미 로그인되어있고 세션이 살아있는 경우에만 작동
        if (Session.getCurrentSession().checkAndImplicitOpen() && isLogined == Boolean.TRUE) {
            Log.d(TAG, "onClick: 로그인 세션살아있음");
            // 카카오 자동 로그인
            sessionCallback.requestMe();
            return;
        }

        ImageButton kakao_login_button = (ImageButton) findViewById(R.id.btn_login_kakao);
        kakao_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카카오 로그인 시도 (창이 뜬다.)
                if (!Session.getCurrentSession().checkAndImplicitOpen()) {
                    session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
                    isLogined = Boolean.TRUE;
                }
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
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) return;

        //구글 로그인시도시
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            task.addOnCompleteListener(task1 -> {
                //회원의 정보를 가져옴
                GoogleSignInAccount account = task1.getResult();
                //회원가입 여부를 확인.
                userInfo = new UserInfo();
                userInfo.add(account);
                userInfo.setToken(account.getId());
                signUp(userInfo);
            });
        }
    }


    //메인액티비티로 이동
    public void move() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }


    //회원가입 함수
    public void signUp(UserInfo userInfo) {
        mv.getUser(userInfo.getToken(), true); //회원 여부 확인을 위한 회원정보 조회
        mv.getUserInfo().observe(this, it -> {
            if (null != userInfo.getUsername()) {
                //회원인 경우
                if (it != null) move();
                    //회원이 아닌 경우
                else {
                    //사용자 생성
                    mv.createUser(userInfo);
                    //액티비티 이동
                    mv.getData().observe(this, et -> {
                        if (et) move();
                    });
                }

            } else {
                Log.d("function signup", "nono token ");
            }
        });

    }

    // 구글 로그인 메소드
    protected void signInGoogle() {
        Intent signInIntent = gsi.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}



