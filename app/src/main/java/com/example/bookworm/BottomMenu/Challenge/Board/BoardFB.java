package com.example.bookworm.BottomMenu.Challenge.Board;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class BoardFB {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    Task task = null;
    CollectionReference collectionReference;
    DocumentReference documentReference;

    BoardFB(Context context) {
        this.context = context;
    }

    public void getData(Map map, String token) {//token = 챌린지 명
        collectionReference = db.collection("challenge").document(token).collection("feed");
        Query query = collectionReference.orderBy("boardID", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    ((subactivity_challenge_board) context).moduleUpdated(querySnapshot.getDocuments());
                }
            }
        });
    }
}
