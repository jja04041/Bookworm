package com.example.bookworm.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookworm.MainActivity;
import com.example.bookworm.R;
import com.example.bookworm.User.UserInfo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;

public class activity_login extends Activity {

    private SessionCallback sessionCallback = new SessionCallback();
    private static final String TAG = "MainActivity";
    public static Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private GoogleSignInClient gsi;

    private int RC_SIGN_IN=123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;
        mAuth = FirebaseAuth.getInstance();

        Session session = Session.getCurrentSession();
        session.addCallback(new SessionCallback());


        // 구글


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("906416699266-sqj1tkdtpigv2tq0mbqhn3afhdr3uug8.apps.googleusercontent.com")
                .requestEmail()
                .build();
        gsi = GoogleSignIn.getClient(this, gso);


        /*
        if(null != gsi) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();

        }
        mAuth = FirebaseAuth.getInstance();
*/
        ImageButton google_login_button = (ImageButton) findViewById(R.id.btn_login_google);

        google_login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signIn();
            }
        });


        // 카카오

        if (Session.getCurrentSession().checkAndImplicitOpen()) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {

            /*
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
            */

            Intent intent=new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();


        }
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
            databaseReference.child("kakao_id_token").push().setValue(idtoken);
            databaseReference.child("User_name").push().setValue(UserInfo.username);

                } else {
                     Log.d("fucntion signup", "nono token ");
                }
    }






    // 구글 관련 메소드

    // 사용자 정보 가져오기
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personGivenName "+personGivenName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personId "+personId);
                Log.d(TAG, "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d(TAG, "handleSignInResult:personPhoto "+personPhoto);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void signIn() {
        Intent signInIntent = gsi.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mAuth.signOut();

        gsi.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void revokeAccess() {
        mAuth.signOut();

        gsi.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }

}



