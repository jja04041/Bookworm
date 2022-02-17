package com.example.bookworm.modules;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookworm.MainActivity;
import com.example.bookworm.ProfileSettingActivity;
import com.example.bookworm.fragments.fragment_profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Map;

public class FBModule {
    String location[] = {"users", "feed", "challenge"}; //각 함수에서 전달받은 인덱스에 맞는 값을 뽑아냄.
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context = null;

    //생성자
    public FBModule(Context... context) {
        this.context = context[0];
    }

    public void saveData(int idx, Map data) {
        switch (idx) {
            case 0://회원가입
                db.collection(location[idx]).document(data.get("idToken").toString()).set(data);
                break;
            case 2://챌린지 생성
                db.collection(location[idx]).document(data.get("strChallengeName").toString()).set(data);
                break;
        }
    }

    public void readData(int idx, String token, Map... map) {

        //해당 정보가 있는지 확인(회원 여부 확인)
        db.collection(location[idx])
                .document(token)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            //중복인 값이 있을 때
                            if (document.exists()) {
                                // 이거 챌린지 명 중복 있으면 컨텍스트때매 오류나는듯 함
                                // 일단 중복 있으면 아무 행동도 안하는걸로 해놓음
//                                if (idx==2){
//                                    Toast.makeText(context, "이미 존재하는 챌린지명 입니다", Toast.LENGTH_SHORT).show();
//                                }
                            }
                            //중복인 값이 없을 때
                            else {
                                //해당 토큰이 파이어베이스에 등록되있지 않은 경우
                                //해당 값을 파이어베이스에 등록
                                saveData(idx, map[0]);
                            }
                        } else {
                            Log.d("TAG3", "get failed with ", task.getException());
                        }
                    }
                });

    }

    public void deleteData(int idx, String token) {
        db.collection(location[idx]).document(token)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        switch (idx) {
                            case 0:
                                (((ProfileSettingActivity) context)).moveToLogin();
                                break;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("오류", "Error deleting document", e);
                    }
                });
    }
}
