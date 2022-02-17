package com.example.bookworm.fragments;

import static com.example.bookworm.Login.activity_login.gsi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookworm.Login.activity_login;
import com.example.bookworm.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.network.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

public class fragment_profile extends Fragment {

    String strNickname, strProfile, strEmail;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        Button btnLogout = (Button) view.findViewById(R.id.btn_logout);
        Button btnSignout = (Button) view.findViewById(R.id.btn_withdraw);


        TextView tvNickname = view.findViewById(R.id.tv_frag_profile_nickname);
        ImageView imgProfile = view.findViewById(R.id.img_frag_profile_profile);
        TextView tvEmail = view.findViewById(R.id.tv_frag_profile_email);

        Context current_context = getActivity();

        Intent intent = getActivity().getIntent();

        strNickname = intent.getStringExtra("name");
        strProfile = intent.getStringExtra("profileimg");
        strEmail = intent.getStringExtra("email");



        tvNickname.setText(strNickname);
        tvEmail.setText(strEmail);
        Glide.with(this).load(strProfile).into(imgProfile);



        btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(current_context, "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        gsi.signOut();
                        Intent intent = new Intent(current_context, activity_login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });

        btnSignout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(current_context)
                        .setMessage("탈퇴하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    // 회원탈퇴 실패
                                    public void onFailure(ErrorResult errorResult) {
                                        int result = errorResult.getErrorCode();

                                        if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                            Toast.makeText(current_context, "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(current_context, "회원탈퇴에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    // 세션 닫힘
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        gsi.revokeAccess();
                                        Toast.makeText(current_context, "세션이 닫혔습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(current_context, activity_login.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }

                                    @Override
                                    // 가입 안된 계정 탈퇴시
                                    public void onNotSignedUp() {
                                        Toast.makeText(current_context, "가입되지 않은 계정입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(current_context, activity_login.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onSuccess(Long result) {
                                        Toast.makeText(current_context, "회원탈퇴에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(current_context, activity_login.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });

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

        return view;

    }



}