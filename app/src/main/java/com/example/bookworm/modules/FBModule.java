package com.example.bookworm.modules;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bookworm.Challenge.subActivity.subactivity_challenge_challengeinfo;
import com.example.bookworm.Login.activity_login;
import com.example.bookworm.MainActivity;
import com.example.bookworm.ProfileSettingActivity;
import com.example.bookworm.User.UserInfo;
import com.example.bookworm.fragments.fragment_challenge;
import com.example.bookworm.fragments.fragment_feed;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class FBModule {
    String location[] = {"users", "feed", "challenge"}; //각 함수에서 전달받은 인덱스에 맞는 값을 뽑아냄.
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    private int LIMIT ;
    Task task = null;
    CollectionReference collectionReference;

    //생성자
    public FBModule(Context context) {
        this.context = context;
    }
    public void setLIMIT(int LIMIT){
        this.LIMIT=LIMIT;
    }
    //데이터 읽기
    public void readData(int idx, Map map, String token) {
        collectionReference = db.collection(location[idx]);
        Query query = collectionReference;
        //해당 정보가 있는지 확인(회원 여부 확인)
        //회원정보 검색,챌린지 중복 조회 등 , 토큰: 챌린지(챌린지 명) , 회원정보 검색(회원토큰값)
        if (token != null) task = collectionReference.document(token).get();
            //피드 표시(토큰
        else if (idx == 1) {
            //map객체: 팔로워 목록
            query = query.orderBy("FeedID");
            if (map.get("lastVisible") != null) {
                DocumentSnapshot snapshot = (DocumentSnapshot) map.get("lastVisible");
                query = query.startAfter(snapshot);
            }
            query=query.limit(LIMIT);
            task = query.get();
        }
        //챌린지 검색
        else if (idx == 2) {
            //챌린지인 경우 map으로 전달된 값은 쿼리에 넣는 값들이 됨.
            //paging 기법 사용
            if (map.get("like") != null) {
                String Keyword = (String) map.get("like");
                query = query.orderBy("strChallengeName").startAt(Keyword).endAt(Keyword + "\uf8ff"); //SQL의 Like문과 같음
            }
            //
            if (map.get("lastVisible") != null) {
                DocumentSnapshot snapshot = (DocumentSnapshot) map.get("lastVisible");
                query = query.startAfter(snapshot);
            }
            query = query.limit(LIMIT);
            task = query.get();
        }


        //결과 확인
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    if (token != null)
                        successRead((DocumentSnapshot) task.getResult(), idx, map);
                    else successRead((QuerySnapshot) task.getResult(), idx, map); //챌린지 조회
                } else {
                    Log.d("TAG3", "get failed with ", task.getException());
                }
            }
        });
    }

    //Document 확인 시
    private void successRead(DocumentSnapshot document, int idx, Map map) {
        //중복인 값이 있을 때, 값을 찾으면 그 데이터를 가져온다.
        if (document.exists()) {
            //유저정보 불러오기
            if (idx == 0) {
                // 장르 업데이트
                if (map.get("userinfo_genre") != null) { document.getReference().update("UserInfo.genre", map.get("userinfo_genre")); }
                // 업적 업데이트
                else if (map.get("userinfo_achievementmap") != null) {  document.getReference().update("UserInfo.achievementmap", map.get("userinfo_achievementmap")); }
                // 인벤토리 업데이트
                else if (map.get("userinfo_wormvec") != null){ document.getReference().update("UserInfo.wormvec", map.get("userinfo_wormvec")); }

                //회원인 경우, 로그인 처리
                else {
                    UserInfo userInfo = new UserInfo();
                    userInfo.add((Map) document.get("UserInfo"));
                    ((activity_login) context).signIn(Boolean.FALSE, userInfo);
                }
            }
            //피드 관련
            //챌린지 관련
            if (idx == 2) {
                //챌린지 참여용
                Object value = map.get("check");
                if (value != null) {
                    switch ((int) value) {
                        case 0: //참여중 인지 확인
                            ((subactivity_challenge_challengeinfo) context).isParticipating(document);
                            break;
                        case 1: //참여가능 확인
                            ((subactivity_challenge_challengeinfo) context).checkParticipating(document, (Dialog) map.get("dialog"));
                            break;
                        case 2: //챌린지 최신화
                            //받아온 값중에 CurrentParticipation의 값을 리스트에 넣음
                            ((subactivity_challenge_challengeinfo) context).setParticipating((ArrayList<String>) document.get("CurrentParticipation"));
                            break;
                    }
                }
                //챌린지 중복 조회
                else Toast.makeText(context, "이미 존재하는 챌린지명 입니다", Toast.LENGTH_SHORT).show();
            }
        }
        //중복인 값이 없을 때
        else {
            //해당 토큰이 파이어베이스에 등록되있지 않은 경우
            //해당 값을 파이어베이스에 등록
            saveData(idx, map);
            if (idx == 2) {  //챌린지 중복이 없는 경우
                Intent intent = new Intent();
                ((Activity) context).setResult(Activity.RESULT_OK, intent);
                ((Activity) context).finish();
                Toast.makeText(context, "챌린지 등록 성공", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Query 사용시
    private void successRead(QuerySnapshot querySnapshot, int idx, Map map) {

        //fragment_challenge에 있는 메소드를 사용하기 위함.
        fragment_challenge fc;
        //fragment_feed에 있는 메소드를 사용하기 위함.
        fragment_feed ff;
        if (querySnapshot.isEmpty()) {
            //피드 조회
            if (idx==1){
                ff = ((fragment_feed) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("0"));
                ff.moduleUpdated(null); //빈값을 반환하여, 찾는 값이 없음을 사용자에게 알림.
            }
            if (idx == 2) {
                    fc = ((fragment_challenge) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("3"));
                    fc.moduleUpdated(null); //빈값을 반환하여, 찾는 값이 없음을 사용자에게 알림.
            }
        } else {
            if (idx==1){
                ff = ((fragment_feed) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("0"));
                ff.moduleUpdated(querySnapshot.getDocuments()); //빈값을 반환하여, 찾는 값이 없음을 사용자에게 알림.
            }
            if (idx == 2) {
                //챌린지 검색
                fc = ((fragment_challenge) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("3"));
                fc.moduleUpdated(querySnapshot.getDocuments()); //찾은 챌린지 목록을 반환함.
            }
        }
    }


    public void saveData(int idx, Map data) {
        switch (idx) {
            case 0://회원가입
                UserInfo userInfo = (UserInfo) (data.get("UserInfo"));
                userInfo.Initbookworm();

                data.put("UserInfo", userInfo);
                db.collection(location[idx]).document(userInfo.getToken()).set(data);
                ((activity_login) context).signIn(Boolean.TRUE, userInfo); //회원이 아닌 경우
                break;

            case 1: //피드 작성
                UserInfo userInfo1 = new UserInfo();
                userInfo1 = (UserInfo) data.get("UserInfo");
                db.collection(location[idx]).document((String) data.get("FeedID")).set(data);
                break;

            case 2://챌린지 생성
                db.collection(location[idx]).document((String) data.get("strChallengeName")).set(data);
                break;
        }
    }

    public void deleteData(int idx, String token) {
        collectionReference = db.collection(location[idx]);
        //task 결정
        task = collectionReference.document(token).delete();
        //task 실행
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                successDelete(idx);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("오류", "Error deleting " + location[idx], e);
            }
        });
    }


    private void successDelete(int idx) {
        switch (idx) {
            case 0:
                (((ProfileSettingActivity) context)).moveToLogin();
                break;
        }
    }

}
