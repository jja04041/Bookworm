package com.example.bookworm.bottomMenu.search.Modules;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bookworm.bottomMenu.search.subactivity.main.SearchPageChallengeFragment;
import com.example.bookworm.bottomMenu.search.subactivity.main.SearchPageFeedFragment;
import com.example.bookworm.bottomMenu.search.subactivity.main.SearchPageUserFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchFB {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    private int LIMIT = 10;
    SearchPageFeedFragment searchPageFeedFragment;
    SearchPageUserFragment searchPageUserFragment;
    SearchPageChallengeFragment searchPageChallengeFragment;



    public SearchFB(Context context) {
        this.context = context;
    }

    public void setLIMIT(int LIMIT) {
        this.LIMIT = LIMIT;
    }


    public void getUser(String Keyword, SearchPageUserFragment searchPageUserFragment) {
        CollectionReference collectionReference = db.collection("users");

        Query query = collectionReference.whereEqualTo("UserInfo.username", Keyword);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {//가져온 데이터가 존재할경우
                        searchPageUserFragment.moduleUpdated(querySnapshot.getDocuments());
                    } else {//가져온 데이터가 존재하지 않을 경우
                        searchPageUserFragment.isEmptyRecord(true);
                    }
                }
            }
        });
    }


    public void getFeed(String Keyword, SearchPageFeedFragment searchPageFeedFragment) {//Keyword = 검색 키워드
        CollectionReference collectionReference = db.collection("feed");

        Query query = collectionReference.whereEqualTo("book.title", Keyword).orderBy("likeCount", Query.Direction.DESCENDING).limit(LIMIT);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {//가져온 데이터가 존재할경우
                        searchPageFeedFragment.moduleUpdated(querySnapshot.getDocuments());
                    } else {//가져온 데이터가 존재하지 않을 경우
                        searchPageFeedFragment.isEmptyRecord(true);
                    }
                }
            }

          });
    }


    public void getChallenge(String Keyword, SearchPageChallengeFragment searchPageChallengeFragment) {
        CollectionReference collectionReference = db.collection("challenge");

        Query query = collectionReference.whereEqualTo("book.title", Keyword).orderBy("CurrentParticipation", Query.Direction.DESCENDING).limit(LIMIT);;

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {//가져온 데이터가 존재할경우
                        searchPageChallengeFragment.moduleUpdated(querySnapshot.getDocuments());
                    } else {//가져온 데이터가 존재하지 않을 경우
                        searchPageChallengeFragment.isEmptyRecord(true);
                    }
                }
            }
        }).addOnFailureListener(it->{
            Log.d("확인합니다.", it.getMessage());
        });
    }

}
