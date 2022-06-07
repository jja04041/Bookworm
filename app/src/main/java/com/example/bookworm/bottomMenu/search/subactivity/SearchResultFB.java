package com.example.bookworm.bottomMenu.search.subactivity;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class SearchResultFB {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    private int LIMIT = 10;
    CollectionReference collectionReference;


    SearchResultFB(Context context) {
        this.context = context;
    }

    public void setLIMIT(int LIMIT) {
        this.LIMIT = LIMIT;
    }

    public void getData(Map map, String itemId) {//itemId = 책 id 값
        collectionReference = db.collection("feed");
        Query query = collectionReference.whereEqualTo("book.itemId", itemId).orderBy("FeedID", Query.Direction.DESCENDING).limit(LIMIT);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {//가져온 데이터가 존재할경우
                        ((search_fragment_subActivity_result) context).moduleUpdated(querySnapshot.getDocuments());
                    } else {//가져온 데이터가 존재하지 않을 경우
                        ((search_fragment_subActivity_result) context).isEmptyReview(true);
                    }
                }
            }
        });
    }

}
