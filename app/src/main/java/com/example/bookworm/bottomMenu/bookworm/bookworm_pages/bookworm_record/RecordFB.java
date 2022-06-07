package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_record;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.bookworm.appLaunch.views.MainActivity;
import com.example.bookworm.bottomMenu.bookworm.fragment_bookworm;
import com.example.bookworm.bottomMenu.challenge.board.Board;
import com.example.bookworm.bottomMenu.challenge.board.subactivity_challenge_board;
import com.example.bookworm.bottomMenu.challenge.board.subactivity_challenge_board_comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class RecordFB {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    private int LIMIT = 10;
    CollectionReference collectionReference;
    fragment_record fragmentRecord;


    RecordFB(Context context) {
        this.context = context;
    }

    public void setLIMIT(int LIMIT) {
        this.LIMIT = LIMIT;
    }

    public void getData(Map map, String UserToken) {//token = 유저 토큰
        collectionReference = db.collection("feed");
        Query query = collectionReference.whereEqualTo("UserToken", UserToken).orderBy("FeedID", Query.Direction.DESCENDING).limit(LIMIT);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {//가져온 데이터가 존재할경우
                        fragmentRecord = ((fragment_record) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("2").getChildFragmentManager().findFragmentByTag("1"));
                        fragmentRecord.moduleUpdated(querySnapshot.getDocuments());
                    } else {//가져온 데이터가 존재하지 않을 경우
                        fragmentRecord = ((fragment_record) ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("2").getChildFragmentManager().findFragmentByTag("1"));
                        fragmentRecord.isEmptyRecord(true);//인증글이 없습니다 라는 문구를 띄워줌
                    }
                }
            }
        });
    }

}
