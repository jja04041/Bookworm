package com.example.bookworm.Login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bookworm.MainActivity;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

public class SessionCallback implements ISessionCallback {


    // 로그인에 성공한 상태
    @Override
    public void onSessionOpened() {
        Log.d("done","끝남");
//        requestMe();
    }

    // 로그인에 실패한 상태
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
    }

    // 사용자 정보 요청
    //이곳에 우리가 필요한 정보를 전달받아 retrofit 등을 통해 db에 저장하면 될 듯 함.
    public void requestMe() {

        UserManagement.getInstance()
                .me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        Log.i("KAKAO_API", "사용자 아이디: " + result.getId());
                        String id = String.valueOf(result.getId());
                        UserAccount kakaoAccount = result.getKakaoAccount();
                        //회원정보 보유 시
                        if (kakaoAccount != null) {

                            // 이메일
                            String email = kakaoAccount.getEmail();
                            Profile profile = kakaoAccount.getProfile();

                            if (profile == null) {
                                Log.d("KAKAO_API", "onSuccess:profile null ");
                            } else {
                                Log.d("KAKAO_API", "onSuccess:getProfileImageUrl " + profile.getProfileImageUrl());
                                Log.d("KAKAO_API", "onSuccess:getNickname " + profile.getNickname());
                            }
                            if (email != null) {

                                Log.d("KAKAO_API", "onSuccess:email " + email);
                            }

                            // 프로필
                            Profile _profile = kakaoAccount.getProfile();

                            if (_profile != null) {

                                Log.d("KAKAO_API", "nickname: " + _profile.getNickname());
                                Log.d("KAKAO_API", "profile image: " + _profile.getProfileImageUrl());
                                Log.d("KAKAO_API", "thumbnail image: " + _profile.getThumbnailImageUrl());

                            } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 프로필 정보 획득 가능

                            } else {
                                // 프로필 획득 불가
                            }
                            UserInfo userinfo = new UserInfo(email, _profile.getNickname(), _profile.getProfileImageUrl());
                            ((activity_login)activity_login.mContext).move(userinfo);
                        } else {
                            Log.i("KAKAO_API", "onSuccess: kakaoAccount null");
                        }
                    }
                });

    }
}
