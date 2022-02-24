package com.example.bookworm.modules;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bookworm.MainActivity;
import com.example.bookworm.ProfileSettingActivity;
import com.example.bookworm.fragments.fragment_challenge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FBModule {
    String location[] = {"users", "feed", "challenge"}; //각 함수에서 전달받은 인덱스에 맞는 값을 뽑아냄.
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context = null;
    public final static int LIMIT = 10;
    Task task = null;

    CollectionReference collectionReference;

    //생성자
    public FBModule(Context... context) {
        if (context != null) this.context = context[0];
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

    public void readData(int idx, Map map,String... token) {
        collectionReference = db.collection(location[idx]);
        //해당 정보가 있는지 확인(회원 여부 확인)
        if (idx != 2||token!=null) task = collectionReference.document(token[0]).get();
        else {
            //챌린지인 경우 map으로 전달된 값은 쿼리에 넣는 값들이 됨.
            //paging 기법과
            Query query = collectionReference;
            if (map.get("like") != null) {
                String Keyword = map.get("like").toString();
                query = query.startAt(Keyword).endAt(Keyword + "\uf8ff"); //SQL의 Like문과 같음
            }
            if (map.get("lastVisible") != null) {
                DocumentSnapshot snapshot=(DocumentSnapshot)map.get("lastVisible");
                query = query.startAfter(snapshot);
            }
            query=query.limit(LIMIT);
            task = query.get();
        }
        //결과 확인
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    if (idx != 2||token!=null) successRead((DocumentSnapshot) task.getResult(), idx, map);
                    else successRead((QuerySnapshot) task.getResult(), map); //챌린지 조회
                } else {
                    Log.d("TAG3", "get failed with ", task.getException());
                }
            }
        });

    }

    public void deleteData(int idx, String token) {
        collectionReference = db.collection(location[idx]);
        //task 결정
        task = collectionReference.document(token).delete();
        //task 실행
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                successDelete(idx);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("오류", "Error deleting " + location[idx], e);
            }
        });
    }

    //Document 확인 시
    private void successRead(DocumentSnapshot document, int idx, Map... map) {
        //중복인 값이 있을 때
        if (document.exists()) {
            if (idx == 2) Toast.makeText(context, "이미 존재하는 챌린지명 입니다", Toast.LENGTH_SHORT).show();
            Log.d("data print", document.toString());
        }
        //중복인 값이 없을 때
        else {
            //해당 토큰이 파이어베이스에 등록되있지 않은 경우
            //해당 값을 파이어베이스에 등록
            saveData(idx, map[0]);
            Log.d("data print", document.toString());
        }
    }

    //Query 사용시
    private void successRead(QuerySnapshot querySnapshot, Map... map) {
        //fragment_challenge에 있는 메소드를 사용하기 위함.
        fragment_challenge f= ((fragment_challenge)((MainActivity)context).getSupportFragmentManager().findFragmentByTag("3"));
        if (querySnapshot.isEmpty()) {
            f.moduleUpdated(null);
        } else {
           f.moduleUpdated(querySnapshot.getDocuments());
        }
    }

    private void successDelete(int idx) {
        switch (idx) {
            case 0:
                (((ProfileSettingActivity) context)).moveToLogin();
                break;
        }
    }
}
