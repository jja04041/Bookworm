package com.example.bookworm.bottomMenu.profile.views;

import static com.example.bookworm.core.login.LoginActivity.gsi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel;
import com.example.bookworm.core.login.LoginActivity;
import com.example.bookworm.core.userdata.PersonalD;
import com.example.bookworm.core.internet.FBModule;
import com.example.bookworm.core.userdata.UserInfo;
import com.example.bookworm.databinding.ActivityProfileSettingBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.kakao.auth.Session;
import com.kakao.network.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

public class ProfileSettingActivity extends AppCompatActivity {

    private UserInfo userInfo;
    private ActivityProfileSettingBinding binding;


    Context current_context;
    UserInfoViewModel pv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityProfileSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        gsi = GoogleSignIn.getClient(ProfileSettingActivity.this, gso);
        current_context = this;

        userInfo=new PersonalD(current_context).getUserInfo();
        //설정 버튼
        binding.btnModify.setOnClickListener(view -> {
            Intent intent = new Intent(current_context, ProfileModifyActivity.class);
            //프로필 수정 화면으로 유저정보 넘겨주기
//                intent.putExtra("userinfo",userInfo);
            startActivity(intent);
        });

        //뒤로가기 버튼
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //로그아웃 버튼
        binding.btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(current_context, "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                if(GoogleSignIn.getLastSignedInAccount(current_context)!=null)
                {
                    gsi.signOut();
                }
                else{
                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            if(Session.getCurrentSession().checkAndImplicitOpen()){
                                Session.getCurrentSession().clearCallbacks(); }
                        }});
                }
                localLogout();
                Intent intent = new Intent(current_context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        //회원탈퇴 버튼
        binding.btnSignout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBModule fbModule = new FBModule(current_context);
                new AlertDialog.Builder(current_context)
                        .setMessage("탈퇴하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //토큰을 이용하여 파이어베이스에 있는 데이터 삭제
                                //카카오로 가입한 계정인 경우
                                String platform=userInfo.getPlatform();
                                if (platform.equals("Kakao")) {
                                    signOutKakao(fbModule, userInfo);
                                } else if (platform.equals("Google")) {
                                    signOutGoogle(fbModule, userInfo);
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    //로그인 액티비티로 이동
    public void moveToLogin() {
        Intent intent = new Intent(current_context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();

    }

    private void localLogout(){
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("key_user");
        editor.commit();
        pref = getSharedPreferences("bookworm", MODE_PRIVATE);
        editor = pref.edit();
        editor.remove("key_bookworm");
        editor.commit();
    }
    //카카오 회원탈퇴  메소드
    private void signOutKakao(FBModule fbModule, UserInfo userInfo) {
        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
            @Override
            // 회원탈퇴 실패
            public void onFailure(ErrorResult errorResult) {
                int result = errorResult.getErrorCode();
                if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                    Toast.makeText(current_context, "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(current_context, "회원탈퇴에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            // 세션 닫힘
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(current_context, "세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                fbModule.deleteData(0, userInfo.getToken()); //계정 삭제
            }

            @Override
            // 가입 안된 계정 탈퇴시
            public void onNotSignedUp() {
                Toast.makeText(current_context, "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                fbModule.deleteData(0, userInfo.getToken()); //계정 삭제
            }

            //성공할 시
            @Override
            public void onSuccess(Long result) {
                Toast.makeText(current_context, "회원탈퇴에 성공했습니다.", Toast.LENGTH_SHORT).show();
                fbModule.deleteData(0, userInfo.getToken()); //계정 삭제
            }
        });
    }

    //구글 회원탈퇴 메소드
    private void signOutGoogle(FBModule fbModule, UserInfo userInfo) {
        gsi.revokeAccess();
        fbModule.deleteData(0, userInfo.getToken());
     }
}
