package com.example.bookworm.bottomMenu.search;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.bookworm.appLaunch.views.MainActivity;
import com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_record.fragment_record;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class RankFB {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    private int LIMIT = 3;
    CollectionReference collectionReference;
    fragment_search fragment_search;


    RankFB(Context context) {
        this.context = context;
    }

    public void setLIMIT(int LIMIT) {
        this.LIMIT = LIMIT;
    }

    public void getData() {
        collectionReference = db.collection("users");
        Query query = collectionReference.orderBy("BookWorm.readcount", Query.Direction.DESCENDING).limit(LIMIT);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {//가져온 데이터가 존재할경우
                        fragment_search = ((fragment_search) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("1"));
                        fragment_search.setRanking(querySnapshot.getDocuments());
                    } else {//가져온 데이터가 존재하지 않을 경우

                    }
                }
            }
        });
    }

}
