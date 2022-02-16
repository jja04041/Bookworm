package com.example.bookworm.modules;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookworm.MainActivity;
import com.example.bookworm.fragments.fragment_profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Map;

public class FBModule {
    String location[] = {"users", "feed"}; //각 함수에서 전달받은 인덱스에 맞는 값을 뽑아냄.
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context=null;
    public FBModule(Context... context) {
        this.context=context[0];
    }

    public void saveData(int idx, Map data) {
        switch (idx) {
            case 0://회원가입
                db.collection(location[idx]).document(data.get("idToken").toString()).set(data);
                break;
        }
    }

    public void readData(int idx, String token, Map... map) {
        switch (idx) {
            //해당 정보가 있는지 확인(회원 여부 확인)
            case 0:
                db.collection(location[idx])
                        .document(token)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document.exists()) {
                                    } else {
                                        //해당 토큰이 파이어베이스에 등록되있지 않은 경우
                                        //해당 값을 파이어베이스에 등록
                                        saveData(0, map[0]);
                                    }
                                } else {
                                    Log.d("TAG3", "get failed with ", task.getException());
                                }
                            }
                        });
                break;
        }

    }

    public void deleteData(int idx, String token) {
        db.collection(location[idx]).document(token)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        switch (idx){
                            case 0:
                                ((fragment_profile)((MainActivity)context).getSupportFragmentManager().findFragmentByTag("4")).moveToLogin();
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
