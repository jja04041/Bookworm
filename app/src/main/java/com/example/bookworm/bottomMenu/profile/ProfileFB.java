package com.example.bookworm.bottomMenu.profile;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

//fragment_profile에서 자기소개를 수정할때 사용하는 모듈
public class ProfileFB {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    Task task = null;
    CollectionReference collectionReference;


    ProfileFB(Context context) {
        this.context = context;
    }

    public void modifyIntroduce(Map map, String UserToken) {//token = 유저 토큰
        collectionReference = db.collection("users");
        task = collectionReference.document(UserToken).update(map);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}
