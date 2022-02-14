package com.example.bookworm.modules;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FBModule {
    String location[] = {"users", "feed"}; //각 함수에서 전달받은 인덱스에 맞는 값을 뽑아냄.

    public FBModule() {
    }

    public void saveData(int idx, Map data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(location[idx]).document(data.get("idToken").toString()).set(data);
    }

    public void readData(int idx, String token, Map... map) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                saveData(0,map[0]);
                                Log.d("TAG2", "No such document");
                            }
                        } else {
                            Log.d("TAG3", "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void deleteData() {

    }
}
