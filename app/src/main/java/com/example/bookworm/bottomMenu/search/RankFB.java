package com.example.bookworm.bottomMenu.search;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.bookworm.appLaunch.views.MainActivity;
import com.example.bookworm.bottomMenu.search.views.FragmentSearch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class RankFB {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    private int LIMIT = 3;
    CollectionReference collectionReference;
    FragmentSearch fragmentSearch;
    ArrayList<Map> userInfoList = null;


    public RankFB(Context context) {
        this.context = context;
    }

    public void setLIMIT(int LIMIT) {
        this.LIMIT = LIMIT;
    }

    public void getRanking() {
        collectionReference = db.collection("BookWorm");
        Query query = collectionReference.orderBy("readCount", Query.Direction.DESCENDING).limit(LIMIT);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {//가져온 데이터가 존재할경우
                    userInfoList = new ArrayList<>();
                    for (int i = 0; i < querySnapshot.getDocuments().size(); i++) {
                        fragmentSearch = ((FragmentSearch) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("1"));
                        fragmentSearch.setRanking((String.valueOf(querySnapshot.getDocuments().get(i).get("readCount"))), i);
                        getUserData((String) querySnapshot.getDocuments().get(i).get("token"), i);
                    }
                } else {//가져온 데이터가 존재하지 않을 경우

                }
            }
        });
    }

    public void getUserData(String UserToken, int index) {
        DocumentReference docRef = db.collection("users").document(UserToken);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { //가져온 데이터가 존재할 경우
                        fragmentSearch = ((FragmentSearch) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("1"));
                        fragmentSearch.setRanking(document.getData(), index);
                    } else {

                    }
                } else { //실패

                }
            }
        });
    }

}
